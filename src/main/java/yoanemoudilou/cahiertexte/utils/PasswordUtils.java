package yoanemoudilou.cahiertexte.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour le hashage et la vérification des mots de passe avec jBCrypt.
 */
public final class PasswordUtils {

    private static final int DEFAULT_LOG_ROUNDS = 12;

    private PasswordUtils() {
        // Empêche l'instanciation.
    }

    /**
     * Hash un mot de passe avec le nombre de tours par défaut.
     */
    public static String hashPassword(String plainPassword) {
        return hashPassword(plainPassword, DEFAULT_LOG_ROUNDS);
    }

    /**
     * Hash un mot de passe avec un nombre de tours personnalisé.
     */
    public static String hashPassword(String plainPassword, int logRounds) {
        validatePlainPassword(plainPassword);

        if (logRounds < 4 || logRounds > 31) {
            throw new IllegalArgumentException("Le nombre de tours BCrypt doit être compris entre 4 et 31.");
        }

        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(logRounds));
    }

    /**
     * Vérifie si un mot de passe brut correspond à son hash BCrypt.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            return false;
        }

        if (hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Vérifie si une chaîne semble être un hash BCrypt valide.
     */
    public static boolean isBCryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]?\\$\\d\\d\\$[./A-Za-z0-9]{53}$");
    }

    /**
     * Indique si un hash devrait être régénéré avec la configuration actuelle.
     */
    public static boolean needsRehash(String hashedPassword) {
        if (!isBCryptHash(hashedPassword)) {
            return true;
        }

        try {
            String[] parts = hashedPassword.split("\\$");
            int rounds = Integer.parseInt(parts[2]);
            return rounds < DEFAULT_LOG_ROUNDS;
        } catch (Exception e) {
            return true;
        }
    }

    private static void validatePlainPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
    }
}