package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.Affectation;
import yoanemoudilou.cahiertexte.repository.AffectationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AffectationRepositoryImpl implements AffectationRepository {

    private static final String INSERT_SQL =
            "INSERT INTO affectations (enseignant_id, cours_id) VALUES (?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE affectations SET enseignant_id = ?, cours_id = ? WHERE id = ?";

    @Override
    public Affectation save(Affectation affectation) throws SQLException {
        if (affectation == null) {
            throw new IllegalArgumentException("L'affectation ne peut pas être null.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, affectation.getEnseignantId());
            ps.setObject(2, affectation.getCoursId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    affectation.setId(rs.getInt(1));
                }
            }

            return affectation;
        }
    }

    @Override
    public boolean update(Affectation affectation) throws SQLException {
        if (affectation == null || affectation.getId() == null) {
            throw new IllegalArgumentException("L'affectation ou son id est invalide.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setObject(1, affectation.getEnseignantId());
            ps.setObject(2, affectation.getCoursId());
            ps.setInt(3, affectation.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM affectations WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Affectation> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM affectations WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAffectation(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Affectation> findByEnseignantIdAndCoursId(Integer enseignantId, Integer coursId) throws SQLException {
        String sql = "SELECT * FROM affectations WHERE enseignant_id = ? AND cours_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, enseignantId);
            ps.setInt(2, coursId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAffectation(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Affectation> findAll() throws SQLException {
        String sql = "SELECT * FROM affectations ORDER BY id DESC";
        List<Affectation> affectations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                affectations.add(mapAffectation(rs));
            }
        }

        return affectations;
    }

    @Override
    public List<Affectation> findByEnseignantId(Integer enseignantId) throws SQLException {
        String sql = "SELECT * FROM affectations WHERE enseignant_id = ? ORDER BY id DESC";
        List<Affectation> affectations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, enseignantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    affectations.add(mapAffectation(rs));
                }
            }
        }

        return affectations;
    }

    @Override
    public List<Affectation> findByCoursId(Integer coursId) throws SQLException {
        String sql = "SELECT * FROM affectations WHERE cours_id = ? ORDER BY id DESC";
        List<Affectation> affectations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, coursId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    affectations.add(mapAffectation(rs));
                }
            }
        }

        return affectations;
    }

    @Override
    public boolean existsByEnseignantIdAndCoursId(Integer enseignantId, Integer coursId) throws SQLException {
        String sql = "SELECT 1 FROM affectations WHERE enseignant_id = ? AND cours_id = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, enseignantId);
            ps.setInt(2, coursId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Affectation mapAffectation(ResultSet rs) throws SQLException {
        return new Affectation(
                rs.getInt("id"),
                rs.getObject("enseignant_id", Integer.class),
                rs.getObject("cours_id", Integer.class)
        );
    }
}
