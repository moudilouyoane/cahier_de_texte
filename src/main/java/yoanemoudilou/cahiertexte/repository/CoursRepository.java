package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.Cours;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données des cours.
 */
public interface CoursRepository {

    Cours save(Cours cours) throws SQLException;

    boolean update(Cours cours) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    Optional<Cours> findById(Integer id) throws SQLException;

    Optional<Cours> findByCode(String code) throws SQLException;

    List<Cours> findAll() throws SQLException;

    List<Cours> findByClasseId(Integer classeId) throws SQLException;

    List<Cours> findByFiliereId(Integer filiereId) throws SQLException;

    List<Cours> findByEnseignantId(Integer enseignantId) throws SQLException;
}