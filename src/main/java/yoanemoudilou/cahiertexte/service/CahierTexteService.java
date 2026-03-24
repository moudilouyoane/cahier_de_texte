package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.CahierTexte;
import yoanemoudilou.cahiertexte.model.Semestre;
import yoanemoudilou.cahiertexte.repository.CahierTexteRepository;
import yoanemoudilou.cahiertexte.repository.impl.CahierTexteRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CahierTexteService {

    private final CahierTexteRepository cahierTexteRepository;

    public CahierTexteService() {
        this(new CahierTexteRepositoryImpl());
    }

    public CahierTexteService(CahierTexteRepository cahierTexteRepository) {
        this.cahierTexteRepository = cahierTexteRepository;
    }

    public CahierTexte obtenirOuCreerPourClasseEtDate(Integer classeId, LocalDate dateSeance) {
        if (classeId == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }
        if (dateSeance == null) {
            throw new IllegalArgumentException("La date de seance est requise.");
        }

        String anneeScolaire = calculerAnneeScolaire(dateSeance);
        Semestre semestre = calculerSemestre(dateSeance);

        try {
            Optional<CahierTexte> existant = cahierTexteRepository.findByClasseAndPeriode(classeId, anneeScolaire, semestre);
            if (existant.isPresent()) {
                return existant.get();
            }

            return cahierTexteRepository.save(
                    new CahierTexte(classeId, anneeScolaire, semestre, LocalDateTime.now())
            );
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la gestion du cahier de texte.", e);
        }
    }

    public Optional<CahierTexte> getCahierTexteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id du cahier de texte est requis.");
        }

        try {
            return cahierTexteRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du cahier de texte.", e);
        }
    }

    public List<CahierTexte> getCahiersByClasseId(Integer classeId) {
        if (classeId == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }

        try {
            return cahierTexteRepository.findByClasseId(classeId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des cahiers de texte.", e);
        }
    }

    public List<CahierTexte> getAllCahiers() {
        try {
            return cahierTexteRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des cahiers de texte.", e);
        }
    }

    public String calculerAnneeScolaire(LocalDate date) {
        int year = date.getYear();
        if (date.getMonthValue() >= 9) {
            return year + "-" + (year + 1);
        }
        return (year - 1) + "-" + year;
    }

    public Semestre calculerSemestre(LocalDate date) {
        int month = date.getMonthValue();
        return (month >= 9 || month <= 1) ? Semestre.SEMESTRE_1 : Semestre.SEMESTRE_2;
    }
}
