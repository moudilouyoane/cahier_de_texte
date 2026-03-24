package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.CahierTexte;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.repository.SeanceRepository;
import yoanemoudilou.cahiertexte.repository.impl.SeanceRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service métier lié aux séances.
 */
public class SeanceService {

    private final SeanceRepository seanceRepository;
    private final CoursService coursService;
    private final CahierTexteService cahierTexteService;

    public SeanceService() {
        this(new SeanceRepositoryImpl(), new CoursService(), new CahierTexteService());
    }

    public SeanceService(SeanceRepository seanceRepository) {
        this(seanceRepository, new CoursService(), new CahierTexteService());
    }

    public SeanceService(SeanceRepository seanceRepository, CoursService coursService, CahierTexteService cahierTexteService) {
        this.seanceRepository = seanceRepository;
        this.coursService = coursService;
        this.cahierTexteService = cahierTexteService;
    }

    public Seance creerSeance(Seance seance) {
        validateSeance(seance);

        try {
            rattacherAuCahierTexte(seance);

            if (seance.getStatut() == null) {
                seance.setStatut(StatutSeance.EN_ATTENTE);
            }

            return seanceRepository.save(seance);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de la seance.", e);
        }
    }

    public boolean modifierSeance(Seance seance) {
        if (seance == null || seance.getId() == null) {
            throw new IllegalArgumentException("La seance ou son id est invalide.");
        }

        validateSeance(seance);

        try {
            rattacherAuCahierTexte(seance);

            if (seance.getStatut() == null) {
                seance.setStatut(StatutSeance.EN_ATTENTE);
            }

            return seanceRepository.update(seance);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la seance.", e);
        }
    }

    public boolean supprimerSeance(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la seance est requis.");
        }

        try {
            return seanceRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la seance.", e);
        }
    }

    public Optional<Seance> getSeanceById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la seance est requis.");
        }

        try {
            return seanceRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la seance.", e);
        }
    }

    public List<Seance> getAllSeances() {
        try {
            return seanceRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances.", e);
        }
    }

    public List<Seance> getSeancesByCoursId(Integer coursId) {
        if (coursId == null) {
            throw new IllegalArgumentException("L'id du cours est requis.");
        }

        try {
            return seanceRepository.findByCoursId(coursId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances par cours.", e);
        }
    }

    public List<Seance> getSeancesByCahierTexteId(Integer cahierTexteId) {
        if (cahierTexteId == null) {
            throw new IllegalArgumentException("L'id du cahier de texte est requis.");
        }

        try {
            return seanceRepository.findByCahierTexteId(cahierTexteId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances par cahier de texte.", e);
        }
    }

    public List<Seance> getSeancesByEnseignantId(Integer enseignantId) {
        if (enseignantId == null) {
            throw new IllegalArgumentException("L'id de l'enseignant est requis.");
        }

        try {
            return seanceRepository.findByEnseignantId(enseignantId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances par enseignant.", e);
        }
    }

    public List<Seance> getSeancesByClasseId(Integer classeId) {
        if (classeId == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }

        try {
            return seanceRepository.findByClasseId(classeId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances par classe.", e);
        }
    }

    public List<Seance> getSeancesByStatut(StatutSeance statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis.");
        }

        try {
            return seanceRepository.findByStatut(statut);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances par statut.", e);
        }
    }

    public List<Seance> getSeancesParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de debut et de fin sont requises.");
        }

        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de debut doit etre anterieure ou egale a la date de fin.");
        }

        try {
            return seanceRepository.findByDateBetween(dateDebut, dateFin);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des seances par periode.", e);
        }
    }

    public boolean validerSeance(Integer seanceId, String commentaireValidation) {
        return updateStatutSeance(seanceId, StatutSeance.VALIDEE, commentaireValidation);
    }

    public boolean rejeterSeance(Integer seanceId, String commentaireValidation) {
        return updateStatutSeance(seanceId, StatutSeance.REJETEE, commentaireValidation);
    }

    public boolean remettreEnAttente(Integer seanceId, String commentaireValidation) {
        return updateStatutSeance(seanceId, StatutSeance.EN_ATTENTE, commentaireValidation);
    }

    public boolean updateStatutSeance(Integer seanceId, StatutSeance statut, String commentaireValidation) {
        if (seanceId == null) {
            throw new IllegalArgumentException("L'id de la seance est requis.");
        }

        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis.");
        }

        try {
            return seanceRepository.updateStatut(seanceId, statut, commentaireValidation);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour du statut de la seance.", e);
        }
    }

    private void validateSeance(Seance seance) {
        if (seance == null) {
            throw new IllegalArgumentException("La seance est requise.");
        }

        if (seance.getCoursId() == null) {
            throw new IllegalArgumentException("Le cours est requis.");
        }

        if (seance.getEnseignantId() == null) {
            throw new IllegalArgumentException("L'enseignant est requis.");
        }

        if (seance.getDateSeance() == null) {
            throw new IllegalArgumentException("La date de seance est requise.");
        }

        if (seance.getHeureSeance() == null) {
            throw new IllegalArgumentException("L'heure de seance est requise.");
        }

        if (seance.getDuree() == null || seance.getDuree() <= 0) {
            throw new IllegalArgumentException("La duree doit etre superieure a 0.");
        }

        if (seance.getContenu() == null || seance.getContenu().isBlank()) {
            throw new IllegalArgumentException("Le contenu de la seance est requis.");
        }
    }

    private void rattacherAuCahierTexte(Seance seance) {
        Cours cours = coursService.getCoursById(seance.getCoursId())
                .orElseThrow(() -> new IllegalArgumentException("Cours introuvable pour la seance."));

        if (cours.getClasseId() == null) {
            throw new IllegalArgumentException("Le cours doit etre associe a une classe.");
        }

        CahierTexte cahierTexte = cahierTexteService.obtenirOuCreerPourClasseEtDate(cours.getClasseId(), seance.getDateSeance());
        seance.setCahierTexteId(cahierTexte.getId());
    }
}
