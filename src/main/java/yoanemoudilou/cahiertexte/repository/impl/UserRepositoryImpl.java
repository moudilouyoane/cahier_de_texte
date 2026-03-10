package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.ChefDepartement;
import yoanemoudilou.cahiertexte.model.Enseignant;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private static final String INSERT_SQL =
            "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, valide, actif) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, role = ?, valide = ?, actif = ? " +
                    "WHERE id = ?";

    @Override
    public User save(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getMotDePasse());
            ps.setString(5, user.getRole() != null ? user.getRole().name() : null);
            ps.setBoolean(6, user.isValide());
            ps.setBoolean(7, user.isActif());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

            return user;
        }
    }

    @Override
    public boolean update(User user) throws SQLException {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("L'utilisateur ou son id est invalide.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPrenom());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getMotDePasse());
            ps.setString(5, user.getRole() != null ? user.getRole().name() : null);
            ps.setBoolean(6, user.isValide());
            ps.setBoolean(7, user.isActif());
            ps.setInt(8, user.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<User> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM utilisateurs ORDER BY nom, prenom";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }

        return users;
    }

    @Override
    public List<User> findByRole(Role role) throws SQLException {
        List<User> users = new ArrayList<>();

        if (role == null) {
            return users;
        }

        String sql = "SELECT * FROM utilisateurs WHERE role = ? ORDER BY nom, prenom";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, role.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }

        return users;
    }

    @Override
    public List<User> findPendingValidation() throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE valide = FALSE ORDER BY nom, prenom";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }

        return users;
    }

    @Override
    public boolean updateValidationStatus(Integer userId, boolean valide) throws SQLException {
        String sql = "UPDATE utilisateurs SET valide = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, valide);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateActiveStatus(Integer userId, boolean actif) throws SQLException {
        String sql = "UPDATE utilisateurs SET actif = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, actif);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        Role role = parseRole(rs.getString("role"));
        User user = createUserInstance(role);

        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setRole(role);
        user.setValide(rs.getBoolean("valide"));
        user.setActif(rs.getBoolean("actif"));

        return user;
    }

    private User createUserInstance(Role role) {
        if (role == null) {
            return new User();
        }

        return switch (role) {
            case ENSEIGNANT -> new Enseignant();
            case RESPONSABLE_CLASSE -> new ResponsableClasse();
            case CHEF_DEPARTEMENT -> new ChefDepartement();
        };
    }

    private Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
