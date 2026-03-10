package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.Affectation;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données des affectations enseignant-cours.
 */
public interface AffectationRepository {

    Affectation save(Affectation affectation) throws SQLException;

    boolean update(Affectation affectation) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    Optional<Affectation> findById(Integer id) throws SQLException;

    Optional<Affectation> findByEnseignantIdAndCoursId(Integer enseignantId, Integer coursId) throws SQLException;

    List<Affectation> findAll() throws SQLException;

    List<Affectation> findByEnseignantId(Integer enseignantId) throws SQLException;

    List<Affectation> findByCoursId(Integer coursId) throws SQLException;

    boolean existsByEnseignantIdAndCoursId(Integer enseignantId, Integer coursId) throws SQLException;
}
