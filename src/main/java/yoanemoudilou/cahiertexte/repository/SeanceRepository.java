package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données des séances.
 */
public interface SeanceRepository {

    Seance save(Seance seance) throws SQLException;

    boolean update(Seance seance) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    Optional<Seance> findById(Integer id) throws SQLException;

    List<Seance> findAll() throws SQLException;

    List<Seance> findByCoursId(Integer coursId) throws SQLException;

    List<Seance> findByCahierTexteId(Integer cahierTexteId) throws SQLException;

    List<Seance> findByEnseignantId(Integer enseignantId) throws SQLException;

    List<Seance> findByClasseId(Integer classeId) throws SQLException;

    List<Seance> findByStatut(StatutSeance statut) throws SQLException;

    List<Seance> findByDateBetween(LocalDate dateDebut, LocalDate dateFin) throws SQLException;

    boolean updateStatut(Integer seanceId, StatutSeance statut, String commentaireValidation) throws SQLException;
}
