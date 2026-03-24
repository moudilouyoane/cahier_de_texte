package yoanemoudilou.cahiertexte.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service d'envoi d'emails applicatifs via SMTP Gmail.
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final Path LOCAL_MAIL_CONFIG_PATH = Path.of("mail-local.properties");
    private static final ExecutorService EMAIL_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "email-dispatcher");
        thread.setDaemon(true);
        return thread;
    });

    private final String host;
    private final int port;
    private final String username;
    private final String appPassword;
    private final String fromName;
    private final boolean enabled;

    public EmailService() {
        this(loadLocalProperties());
    }

    private EmailService(Properties localProperties) {
        this(
                getConfigValue(localProperties, "MAIL_HOST", "smtp.gmail.com"),
                parseInt(getConfigValue(localProperties, "MAIL_PORT", "587"), 587),
                getConfigValue(localProperties, "MAIL_USERNAME", ""),
                getConfigValue(localProperties, "MAIL_APP_PASSWORD", ""),
                getConfigValue(localProperties, "MAIL_FROM_NAME", "Cahier de texte"),
                parseBoolean(getConfigValue(localProperties, "MAIL_ENABLED", "false"))
        );
    }

    public EmailService(String host, int port, String username, String appPassword, String fromName, boolean enabled) {
        this.host = host;
        this.port = port;
        this.username = username == null ? "" : username.trim();
        this.appPassword = appPassword == null ? "" : appPassword.trim();
        this.fromName = fromName == null || fromName.isBlank() ? "Cahier de texte" : fromName.trim();
        this.enabled = enabled;
    }

    public void envoyerCoursAttribueAsync(User enseignant, Cours cours, Classe classe) {
        CompletableFuture.runAsync(() -> envoyerCoursAttribue(enseignant, cours, classe), EMAIL_EXECUTOR)
                .exceptionally(ex -> {
                    LOGGER.log(Level.WARNING, "Echec de l'envoi asynchrone de l'email d'affectation.", ex);
                    return null;
                });
    }

    public void envoyerCoursAttribue(User enseignant, Cours cours, Classe classe) {
        if (!isConfigured()) {
            LOGGER.info("Service email desactive ou non configure. Envoi Gmail ignore.");
            return;
        }

        validateEmailRequest(enseignant, cours, classe);

        try {
            Session session = Session.getInstance(buildMailProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, appPassword);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, fromName, StandardCharsets.UTF_8.name()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(enseignant.getEmail(), false));
            message.setSubject("Nouvelle affectation de cours", StandardCharsets.UTF_8.name());
            message.setText(buildCoursAttribueBody(enseignant, cours, classe), StandardCharsets.UTF_8.name());

            Transport.send(message);
            LOGGER.info("Email d'affectation envoye a " + enseignant.getEmail());
        } catch (MessagingException e) {
            LOGGER.log(Level.WARNING, "Erreur SMTP lors de l'envoi de l'email d'affectation.", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la preparation de l'email d'affectation.", e);
        }
    }

    public boolean isConfigured() {
        return enabled
                && !username.isBlank()
                && !appPassword.isBlank()
                && !host.isBlank()
                && port > 0;
    }

    private Properties buildMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.ssl.trust", host);
        return props;
    }

    private String buildCoursAttribueBody(User enseignant, Cours cours, Classe classe) {
        String nomClasse = classe.getNomClasse() != null ? classe.getNomClasse() : "Classe non renseignee";
        String filiere = classe.getFiliere() != null && classe.getFiliere().getNom() != null
                ? classe.getFiliere().getNom()
                : "Filiere non renseignee";
        String volume = cours.getVolumeHoraire() != null ? cours.getVolumeHoraire() + " heures" : "Non renseigne";

        return """
                Bonjour %s,

                Vous venez d'etre affecte au cours suivant :

                - Intitule : %s
                - Code : %s
                - Volume horaire : %s
                - Classe : %s
                - Filiere : %s

                Merci de vous connecter a l'application pour consulter les details de votre affectation.

                Equipe Cahier de texte
                """.formatted(
                enseignant.getNomComplet(),
                safe(cours.getIntitule()),
                safe(cours.getCode()),
                volume,
                nomClasse,
                filiere
        );
    }

    private void validateEmailRequest(User enseignant, Cours cours, Classe classe) {
        if (enseignant == null || enseignant.getEmail() == null || enseignant.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email de l'enseignant est requis pour l'envoi.");
        }

        if (cours == null) {
            throw new IllegalArgumentException("Le cours est requis pour l'envoi de l'email.");
        }

        if (classe == null) {
            throw new IllegalArgumentException("La classe est requise pour l'envoi de l'email.");
        }

        try {
            InternetAddress address = new InternetAddress(enseignant.getEmail());
            address.validate();
        } catch (MessagingException e) {
            throw new IllegalArgumentException("L'adresse email de l'enseignant est invalide.", e);
        }
    }

    private static Properties loadLocalProperties() {
        Properties props = new Properties();

        if (!Files.exists(LOCAL_MAIL_CONFIG_PATH)) {
            return props;
        }

        try (InputStream inputStream = Files.newInputStream(LOCAL_MAIL_CONFIG_PATH)) {
            props.load(inputStream);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Impossible de lire la configuration mail locale.", e);
        }

        return props;
    }

    private static String getConfigValue(Properties localProperties, String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String localValue = localProperties.getProperty(key);
        return localValue == null || localValue.isBlank() ? defaultValue : localValue;
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "Non renseigne" : value;
    }
}
