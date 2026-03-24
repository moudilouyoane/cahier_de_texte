package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.repository.SeanceRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeanceRepositoryImpl implements SeanceRepository {

    private static final String INSERT_SQL =
            "INSERT INTO seances (cahier_texte_id, cours_id, enseignant_id, date_seance, heure_seance, duree, contenu, observations, statut, commentaire_validation) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE seances SET cahier_texte_id = ?, cours_id = ?, enseignant_id = ?, date_seance = ?, heure_seance = ?, duree = ?, contenu = ?, observations = ?, statut = ?, commentaire_validation = ? " +
                    "WHERE id = ?";

    @Override
    public Seance save(Seance seance) throws SQLException {
        if (seance == null) {
            throw new IllegalArgumentException("La seance ne peut pas etre null.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, seance.getCahierTexteId());
            ps.setObject(2, seance.getCoursId());
            ps.setObject(3, seance.getEnseignantId());
            ps.setDate(4, seance.getDateSeance() != null ? Date.valueOf(seance.getDateSeance()) : null);
            ps.setTime(5, seance.getHeureSeance() != null ? Time.valueOf(seance.getHeureSeance()) : null);
            ps.setObject(6, seance.getDuree());
            ps.setString(7, seance.getContenu());
            ps.setString(8, seance.getObservations());
            ps.setString(9, seance.getStatut() != null ? seance.getStatut().name() : StatutSeance.EN_ATTENTE.name());
            ps.setString(10, seance.getCommentaireValidation());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    seance.setId(rs.getInt(1));
                }
            }

            return seance;
        }
    }

    @Override
    public boolean update(Seance seance) throws SQLException {
        if (seance == null || seance.getId() == null) {
            throw new IllegalArgumentException("La seance ou son id est invalide.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setObject(1, seance.getCahierTexteId());
            ps.setObject(2, seance.getCoursId());
            ps.setObject(3, seance.getEnseignantId());
            ps.setDate(4, seance.getDateSeance() != null ? Date.valueOf(seance.getDateSeance()) : null);
            ps.setTime(5, seance.getHeureSeance() != null ? Time.valueOf(seance.getHeureSeance()) : null);
            ps.setObject(6, seance.getDuree());
            ps.setString(7, seance.getContenu());
            ps.setString(8, seance.getObservations());
            ps.setString(9, seance.getStatut() != null ? seance.getStatut().name() : StatutSeance.EN_ATTENTE.name());
            ps.setString(10, seance.getCommentaireValidation());
            ps.setInt(11, seance.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM seances WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Seance> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM seances WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSeance(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Seance> findAll() throws SQLException {
        String sql = "SELECT * FROM seances ORDER BY date_seance DESC, heure_seance DESC";
        List<Seance> seances = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                seances.add(mapSeance(rs));
            }
        }

        return seances;
    }

    @Override
    public List<Seance> findByCoursId(Integer coursId) throws SQLException {
        String sql = "SELECT * FROM seances WHERE cours_id = ? ORDER BY date_seance DESC, heure_seance DESC";
        List<Seance> seances = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, coursId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(mapSeance(rs));
                }
            }
        }

        return seances;
    }

    @Override
    public List<Seance> findByCahierTexteId(Integer cahierTexteId) throws SQLException {
        String sql = "SELECT * FROM seances WHERE cahier_texte_id = ? ORDER BY date_seance DESC, heure_seance DESC";
        List<Seance> seances = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, cahierTexteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(mapSeance(rs));
                }
            }
        }

        return seances;
    }

    @Override
    public List<Seance> findByEnseignantId(Integer enseignantId) throws SQLException {
        String sql = "SELECT * FROM seances WHERE enseignant_id = ? ORDER BY date_seance DESC, heure_seance DESC";
        List<Seance> seances = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, enseignantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(mapSeance(rs));
                }
            }
        }

        return seances;
    }

    @Override
    public List<Seance> findByClasseId(Integer classeId) throws SQLException {
        String sql =
                "SELECT s.* " +
                        "FROM seances s " +
                        "INNER JOIN cahiers_texte ct ON s.cahier_texte_id = ct.id " +
                        "WHERE ct.classe_id = ? " +
                        "ORDER BY s.date_seance DESC, s.heure_seance DESC";

        List<Seance> seances = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, classeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(mapSeance(rs));
                }
            }
        }

        return seances;
    }

    @Override
    public List<Seance> findByStatut(StatutSeance statut) throws SQLException {
        List<Seance> seances = new ArrayList<>();

        if (statut == null) {
            return seances;
        }

        String sql = "SELECT * FROM seances WHERE statut = ? ORDER BY date_seance DESC, heure_seance DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, statut.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(mapSeance(rs));
                }
            }
        }

        return seances;
    }

    @Override
    public List<Seance> findByDateBetween(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql =
                "SELECT * FROM seances WHERE date_seance BETWEEN ? AND ? " +
                        "ORDER BY date_seance ASC, heure_seance ASC";

        List<Seance> seances = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dateDebut));
            ps.setDate(2, Date.valueOf(dateFin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seances.add(mapSeance(rs));
                }
            }
        }

        return seances;
    }

    @Override
    public boolean updateStatut(Integer seanceId, StatutSeance statut, String commentaireValidation) throws SQLException {
        String sql = "UPDATE seances SET statut = ?, commentaire_validation = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, statut != null ? statut.name() : null);
            ps.setString(2, commentaireValidation);
            ps.setInt(3, seanceId);

            return ps.executeUpdate() > 0;
        }
    }

    private Seance mapSeance(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("date_seance");
        Time sqlTime = rs.getTime("heure_seance");

        return new Seance(
                rs.getInt("id"),
                rs.getObject("cahier_texte_id", Integer.class),
                rs.getObject("cours_id", Integer.class),
                rs.getObject("enseignant_id", Integer.class),
                sqlDate != null ? sqlDate.toLocalDate() : null,
                sqlTime != null ? sqlTime.toLocalTime() : null,
                rs.getObject("duree", Integer.class),
                rs.getString("contenu"),
                rs.getString("observations"),
                parseStatut(rs.getString("statut")),
                rs.getString("commentaire_validation")
        );
    }

    private StatutSeance parseStatut(String value) {
        if (value == null || value.isBlank()) {
            return StatutSeance.EN_ATTENTE;
        }

        try {
            return StatutSeance.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return StatutSeance.EN_ATTENTE;
        }
    }
}
