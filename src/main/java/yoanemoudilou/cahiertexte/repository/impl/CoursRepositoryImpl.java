package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.repository.CoursRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoursRepositoryImpl implements CoursRepository {

    private static final String INSERT_SQL =
            "INSERT INTO cours (code, intitule, volume_horaire, classe_id) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE cours SET code = ?, intitule = ?, volume_horaire = ?, classe_id = ? WHERE id = ?";

    @Override
    public Cours save(Cours cours) throws SQLException {
        if (cours == null) {
            throw new IllegalArgumentException("Le cours ne peut pas être null.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cours.getCode());
            ps.setString(2, cours.getIntitule());
            ps.setObject(3, cours.getVolumeHoraire());
            ps.setObject(4, cours.getClasseId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cours.setId(rs.getInt(1));
                }
            }

            return cours;
        }
    }

    @Override
    public boolean update(Cours cours) throws SQLException {
        if (cours == null || cours.getId() == null) {
            throw new IllegalArgumentException("Le cours ou son id est invalide.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, cours.getCode());
            ps.setString(2, cours.getIntitule());
            ps.setObject(3, cours.getVolumeHoraire());
            ps.setObject(4, cours.getClasseId());
            ps.setInt(5, cours.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM cours WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Cours> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM cours WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCours(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Cours> findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM cours WHERE code = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCours(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Cours> findAll() throws SQLException {
        String sql = "SELECT * FROM cours ORDER BY intitule";
        List<Cours> coursList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                coursList.add(mapCours(rs));
            }
        }

        return coursList;
    }

    @Override
    public List<Cours> findByClasseId(Integer classeId) throws SQLException {
        String sql = "SELECT * FROM cours WHERE classe_id = ? ORDER BY intitule";
        List<Cours> coursList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, classeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coursList.add(mapCours(rs));
                }
            }
        }

        return coursList;
    }

    @Override
    public List<Cours> findByFiliereId(Integer filiereId) throws SQLException {
        String sql =
                "SELECT c.* " +
                        "FROM cours c " +
                        "INNER JOIN classes cl ON c.classe_id = cl.id " +
                        "WHERE cl.filiere_id = ? " +
                        "ORDER BY c.intitule";

        List<Cours> coursList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, filiereId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coursList.add(mapCours(rs));
                }
            }
        }

        return coursList;
    }

    @Override
    public List<Cours> findByEnseignantId(Integer enseignantId) throws SQLException {
        String sql =
                "SELECT DISTINCT c.* " +
                        "FROM cours c " +
                        "INNER JOIN affectations a ON c.id = a.cours_id " +
                        "WHERE a.enseignant_id = ? " +
                        "ORDER BY c.intitule";

        List<Cours> coursList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, enseignantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coursList.add(mapCours(rs));
                }
            }
        }

        return coursList;
    }

    private Cours mapCours(ResultSet rs) throws SQLException {
        return new Cours(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("intitule"),
                rs.getObject("volume_horaire", Integer.class),
                rs.getObject("classe_id", Integer.class)
        );
    }
}
