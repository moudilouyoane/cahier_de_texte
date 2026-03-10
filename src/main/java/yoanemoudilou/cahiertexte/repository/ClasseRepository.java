package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.Classe;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données des classes.
 */
public interface ClasseRepository {

    Classe save(Classe classe) throws SQLException;

    boolean update(Classe classe) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    Optional<Classe> findById(Integer id) throws SQLException;

    Optional<Classe> findByNomClasseAndNiveau(String nomClasse, String niveau) throws SQLException;

    List<Classe> findAll() throws SQLException;

    List<Classe> findByFiliereId(Integer filiereId) throws SQLException;

    List<Classe> findByNiveau(String niveau) throws SQLException;
}