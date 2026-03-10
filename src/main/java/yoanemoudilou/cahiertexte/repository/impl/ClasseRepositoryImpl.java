package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Filiere;
import yoanemoudilou.cahiertexte.repository.ClasseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClasseRepositoryImpl implements ClasseRepository {

    private static final String BASE_SELECT =
            "SELECT c.id, c.nom_classe, c.niveau, " +
                    "f.id AS filiere_id, f.code AS filiere_code, f.nom AS filiere_nom " +
                    "FROM classes c " +
                    "LEFT JOIN filieres f ON c.filiere_id = f.id ";

    private static final String INSERT_SQL =
            "INSERT INTO classes (nom_classe, niveau, filiere_id) VALUES (?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE classes SET nom_classe = ?, niveau = ?, filiere_id = ? WHERE id = ?";

    @Override
    public Classe save(Classe classe) throws SQLException {
        if (classe == null) {
            throw new IllegalArgumentException("La classe ne peut pas être null.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, classe.getNomClasse());
            ps.setString(2, classe.getNiveau());

            if (classe.getFiliere() != null && classe.getFiliere().getId() != null) {
                ps.setInt(3, classe.getFiliere().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    classe.setId(rs.getInt(1));
                }
            }

            return classe;
        }
    }

    @Override
    public boolean update(Classe classe) throws SQLException {
        if (classe == null || classe.getId() == null) {
            throw new IllegalArgumentException("La classe ou son id est invalide.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, classe.getNomClasse());
            ps.setString(2, classe.getNiveau());

            if (classe.getFiliere() != null && classe.getFiliere().getId() != null) {
                ps.setInt(3, classe.getFiliere().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, classe.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM classes WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Classe> findById(Integer id) throws SQLException {
        String sql = BASE_SELECT + "WHERE c.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapClasse(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Classe> findByNomClasseAndNiveau(String nomClasse, String niveau) throws SQLException {
        String sql = BASE_SELECT + "WHERE c.nom_classe = ? AND c.niveau = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nomClasse);
            ps.setString(2, niveau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapClasse(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Classe> findAll() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY c.niveau, c.nom_classe";
        List<Classe> classes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                classes.add(mapClasse(rs));
            }
        }

        return classes;
    }

    @Override
    public List<Classe> findByFiliereId(Integer filiereId) throws SQLException {
        String sql = BASE_SELECT + "WHERE c.filiere_id = ? ORDER BY c.niveau, c.nom_classe";
        List<Classe> classes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, filiereId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapClasse(rs));
                }
            }
        }

        return classes;
    }

    @Override
    public List<Classe> findByNiveau(String niveau) throws SQLException {
        String sql = BASE_SELECT + "WHERE c.niveau = ? ORDER BY c.nom_classe";
        List<Classe> classes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, niveau);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapClasse(rs));
                }
            }
        }

        return classes;
    }

    private Classe mapClasse(ResultSet rs) throws SQLException {
        Integer filiereId = rs.getObject("filiere_id", Integer.class);
        Filiere filiere = null;

        if (filiereId != null) {
            filiere = new Filiere(
                    filiereId,
                    rs.getString("filiere_code"),
                    rs.getString("filiere_nom")
            );
        }

        return new Classe(
                rs.getInt("id"),
                rs.getString("nom_classe"),
                rs.getString("niveau"),
                filiere
        );
    }
}
