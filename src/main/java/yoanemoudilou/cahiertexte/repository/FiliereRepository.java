package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.Filiere;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données des filières.
 */
public interface FiliereRepository {

    Filiere save(Filiere filiere) throws SQLException;

    boolean update(Filiere filiere) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    Optional<Filiere> findById(Integer id) throws SQLException;

    Optional<Filiere> findByCode(String code) throws SQLException;

    List<Filiere> findAll() throws SQLException;

    boolean existsByCode(String code) throws SQLException;
}