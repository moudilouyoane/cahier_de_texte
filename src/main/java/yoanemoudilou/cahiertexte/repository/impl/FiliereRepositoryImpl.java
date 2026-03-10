package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.Filiere;
import yoanemoudilou.cahiertexte.repository.FiliereRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FiliereRepositoryImpl implements FiliereRepository {

    private static final String INSERT_SQL =
            "INSERT INTO filieres (code, nom) VALUES (?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE filieres SET code = ?, nom = ? WHERE id = ?";

    @Override
    public Filiere save(Filiere filiere) throws SQLException {
        if (filiere == null) {
            throw new IllegalArgumentException("La filière ne peut pas être null.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, filiere.getCode());
            ps.setString(2, filiere.getNom());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    filiere.setId(rs.getInt(1));
                }
            }

            return filiere;
        }
    }

    @Override
    public boolean update(Filiere filiere) throws SQLException {
        if (filiere == null || filiere.getId() == null) {
            throw new IllegalArgumentException("La filière ou son id est invalide.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, filiere.getCode());
            ps.setString(2, filiere.getNom());
            ps.setInt(3, filiere.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM filieres WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Filiere> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM filieres WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFiliere(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Filiere> findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM filieres WHERE code = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFiliere(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Filiere> findAll() throws SQLException {
        String sql = "SELECT * FROM filieres ORDER BY code";
        List<Filiere> filieres = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                filieres.add(mapFiliere(rs));
            }
        }

        return filieres;
    }

    @Override
    public boolean existsByCode(String code) throws SQLException {
        String sql = "SELECT 1 FROM filieres WHERE code = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Filiere mapFiliere(ResultSet rs) throws SQLException {
        return new Filiere(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("nom")
        );
    }
}
