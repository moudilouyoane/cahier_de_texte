package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.CahierTexte;
import yoanemoudilou.cahiertexte.model.Semestre;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CahierTexteRepository {

    CahierTexte save(CahierTexte cahierTexte) throws SQLException;

    Optional<CahierTexte> findById(Integer id) throws SQLException;

    Optional<CahierTexte> findByClasseAndPeriode(Integer classeId, String anneeScolaire, Semestre semestre) throws SQLException;

    List<CahierTexte> findByClasseId(Integer classeId) throws SQLException;

    List<CahierTexte> findAll() throws SQLException;
}
