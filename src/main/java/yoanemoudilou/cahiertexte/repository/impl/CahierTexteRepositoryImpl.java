package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.CahierTexte;
import yoanemoudilou.cahiertexte.model.Semestre;
import yoanemoudilou.cahiertexte.repository.CahierTexteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CahierTexteRepositoryImpl implements CahierTexteRepository {

    private static final String INSERT_SQL =
            "INSERT INTO cahiers_texte (classe_id, annee_scolaire, semestre, date_creation) VALUES (?, ?, ?, ?)";

    @Override
    public CahierTexte save(CahierTexte cahierTexte) throws SQLException {
        if (cahierTexte == null) {
            throw new IllegalArgumentException("Le cahier de texte est requis.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, cahierTexte.getClasseId());
            ps.setString(2, cahierTexte.getAnneeScolaire());
            ps.setString(3, cahierTexte.getSemestre() != null ? cahierTexte.getSemestre().name() : null);
            ps.setTimestamp(4, cahierTexte.getDateCreation() != null ? Timestamp.valueOf(cahierTexte.getDateCreation()) : null);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cahierTexte.setId(rs.getInt(1));
                }
            }

            return cahierTexte;
        }
    }

    @Override
    public Optional<CahierTexte> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM cahiers_texte WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCahierTexte(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<CahierTexte> findByClasseAndPeriode(Integer classeId, String anneeScolaire, Semestre semestre) throws SQLException {
        String sql = "SELECT * FROM cahiers_texte WHERE classe_id = ? AND annee_scolaire = ? AND semestre = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, classeId);
            ps.setString(2, anneeScolaire);
            ps.setString(3, semestre != null ? semestre.name() : null);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCahierTexte(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<CahierTexte> findByClasseId(Integer classeId) throws SQLException {
        String sql = "SELECT * FROM cahiers_texte WHERE classe_id = ? ORDER BY annee_scolaire DESC, semestre DESC";
        List<CahierTexte> cahiers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, classeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cahiers.add(mapCahierTexte(rs));
                }
            }
        }

        return cahiers;
    }

    @Override
    public List<CahierTexte> findAll() throws SQLException {
        String sql = "SELECT * FROM cahiers_texte ORDER BY annee_scolaire DESC, semestre DESC, id DESC";
        List<CahierTexte> cahiers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cahiers.add(mapCahierTexte(rs));
            }
        }

        return cahiers;
    }

    private CahierTexte mapCahierTexte(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("date_creation");

        return new CahierTexte(
                rs.getInt("id"),
                rs.getObject("classe_id", Integer.class),
                rs.getString("annee_scolaire"),
                parseSemestre(rs.getString("semestre")),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }

    private Semestre parseSemestre(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Semestre.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
