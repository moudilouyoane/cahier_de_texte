package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.repository.UserRepository;
import yoanemoudilou.cahiertexte.repository.impl.UserRepositoryImpl;
import yoanemoudilou.cahiertexte.utils.PasswordUtils;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service d'authentification.
 */
public class AuthService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public AuthService() {
        this(new UserRepositoryImpl(), SessionManager.getInstance());
    }

    public AuthService(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * Authentifie un utilisateur.
     */
    public Optional<User> authenticate(String email, String motDePasse) {
        validateCredentials(email, motDePasse);

        try {
            Optional<User> optionalUser = userRepository.findByEmail(normalizeEmail(email));

            if (optionalUser.isEmpty()) {
                return Optional.empty();
            }

            User user = optionalUser.get();

            if (!user.isActif() || !user.isValide()) {
                return Optional.empty();
            }

            if (!PasswordUtils.verifyPassword(motDePasse, user.getMotDePasse())) {
                return Optional.empty();
            }

            if (PasswordUtils.needsRehash(user.getMotDePasse())) {
                user.setMotDePasse(PasswordUtils.hashPassword(motDePasse));
                userRepository.update(user);
            }

            return Optional.of(user);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'authentification de l'utilisateur.", e);
        }
    }

    /**
     * Connecte un utilisateur et ouvre sa session.
     */
    public boolean login(String email, String motDePasse) {
        Optional<User> optionalUser = authenticate(email, motDePasse);

        if (optionalUser.isPresent()) {
            sessionManager.ouvrirSession(optionalUser.get());
            return true;
        }

        return false;
    }

    /**
     * Déconnecte l'utilisateur courant.
     */
    public void logout() {
        sessionManager.fermerSession();
    }

    /**
     * Inscrit un nouvel utilisateur.
     */
    public User inscrire(User user) {
        validateUserForRegistration(user);

        try {
            String normalizedEmail = normalizeEmail(user.getEmail());
            user.setEmail(normalizedEmail);

            if (userRepository.findByEmail(normalizedEmail).isPresent()) {
                throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
            }

            user.setMotDePasse(PasswordUtils.hashPassword(user.getMotDePasse()));
            return userRepository.save(user);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'inscription de l'utilisateur.", e);
        }
    }

    /**
     * Change le mot de passe d'un utilisateur.
     */
    public boolean changerMotDePasse(Integer userId, String ancienMotDePasse, String nouveauMotDePasse) {
        if (userId == null) {
            throw new IllegalArgumentException("L'identifiant utilisateur est requis.");
        }

        if (ancienMotDePasse == null || ancienMotDePasse.isBlank()) {
            throw new IllegalArgumentException("L'ancien mot de passe est requis.");
        }

        if (nouveauMotDePasse == null || nouveauMotDePasse.isBlank()) {
            throw new IllegalArgumentException("Le nouveau mot de passe est requis.");
        }

        try {
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isEmpty()) {
                return false;
            }

            User user = optionalUser.get();

            if (!PasswordUtils.verifyPassword(ancienMotDePasse, user.getMotDePasse())) {
                return false;
            }

            user.setMotDePasse(PasswordUtils.hashPassword(nouveauMotDePasse));
            return userRepository.update(user);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du changement de mot de passe.", e);
        }
    }

    /**
     * Retourne l'utilisateur actuellement connecté.
     */
    public Optional<User> getUtilisateurConnecte() {
        return Optional.ofNullable(sessionManager.getUtilisateurConnecte());
    }

    private void validateCredentials(String email, String motDePasse) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est requis.");
        }

        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est requis.");
        }
    }

    private void validateUserForRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur est requis.");
        }

        if (user.getNom() == null || user.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom est requis.");
        }

        if (user.getPrenom() == null || user.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le prénom est requis.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est requis.");
        }

        if (user.getMotDePasse() == null || user.getMotDePasse().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est requis.");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("Le rôle est requis.");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}