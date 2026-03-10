package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.repository.UserRepository;
import yoanemoudilou.cahiertexte.repository.impl.UserRepositoryImpl;
import yoanemoudilou.cahiertexte.utils.PasswordUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service métier lié aux utilisateurs.
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this(new UserRepositoryImpl());
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User creerUtilisateur(User user) {
        validateUser(user);

        try {
            String normalizedEmail = normalizeEmail(user.getEmail());
            user.setEmail(normalizedEmail);

            Optional<User> existing = userRepository.findByEmail(normalizedEmail);
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
            }

            if (!PasswordUtils.isBCryptHash(user.getMotDePasse())) {
                user.setMotDePasse(PasswordUtils.hashPassword(user.getMotDePasse()));
            }

            return userRepository.save(user);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'utilisateur.", e);
        }
    }

    public boolean modifierUtilisateur(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("L'utilisateur ou son id est invalide.");
        }

        validateUserWithoutId(user);

        try {
            Optional<User> existingById = userRepository.findById(user.getId());
            if (existingById.isEmpty()) {
                throw new IllegalArgumentException("Utilisateur introuvable.");
            }

            String normalizedEmail = normalizeEmail(user.getEmail());
            user.setEmail(normalizedEmail);

            Optional<User> existingByEmail = userRepository.findByEmail(normalizedEmail);
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
            }

            if (user.getMotDePasse() == null || user.getMotDePasse().isBlank()) {
                user.setMotDePasse(existingById.get().getMotDePasse());
            } else if (!PasswordUtils.isBCryptHash(user.getMotDePasse())) {
                user.setMotDePasse(PasswordUtils.hashPassword(user.getMotDePasse()));
            }

            return userRepository.update(user);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de l'utilisateur.", e);
        }
    }

    public boolean supprimerUtilisateur(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return userRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur.", e);
        }
    }

    public Optional<User> getUtilisateurById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return userRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur.", e);
        }
    }

    public Optional<User> getUtilisateurByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est requis.");
        }

        try {
            return userRepository.findByEmail(normalizeEmail(email));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur par email.", e);
        }
    }

    public List<User> getAllUtilisateurs() {
        try {
            return userRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs.", e);
        }
    }

    public List<User> getUtilisateursByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Le rôle est requis.");
        }

        try {
            return userRepository.findByRole(role);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs par rôle.", e);
        }
    }

    public List<User> getUtilisateursEnAttenteValidation() {
        try {
            return userRepository.findPendingValidation();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs en attente.", e);
        }
    }

    public boolean validerUtilisateur(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return userRepository.updateValidationStatus(userId, true);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la validation de l'utilisateur.", e);
        }
    }

    public boolean rejeterValidationUtilisateur(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return userRepository.updateValidationStatus(userId, false);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de validation.", e);
        }
    }

    public boolean activerUtilisateur(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return userRepository.updateActiveStatus(userId, true);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'activation de l'utilisateur.", e);
        }
    }

    public boolean desactiverUtilisateur(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return userRepository.updateActiveStatus(userId, false);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la désactivation de l'utilisateur.", e);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur est requis.");
        }

        validateUserWithoutId(user);

        if (user.getMotDePasse() == null || user.getMotDePasse().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est requis.");
        }
    }

    private void validateUserWithoutId(User user) {
        if (user.getNom() == null || user.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom est requis.");
        }

        if (user.getPrenom() == null || user.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le prénom est requis.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est requis.");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("Le rôle est requis.");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
