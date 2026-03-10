package yoanemoudilou.cahiertexte.service;

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

    public SeanceService() {
        this(new SeanceRepositoryImpl());
    }

    public SeanceService(SeanceRepository seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    public Seance creerSeance(Seance seance) {
        validateSeance(seance);

        try {
            if (seance.getStatut() == null) {
                seance.setStatut(StatutSeance.EN_ATTENTE);
            }

            return seanceRepository.save(seance);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la séance.", e);
        }
    }

    public boolean modifierSeance(Seance seance) {
        if (seance == null || seance.getId() == null) {
            throw new IllegalArgumentException("La séance ou son id est invalide.");
        }

        validateSeance(seance);

        try {
            if (seance.getStatut() == null) {
                seance.setStatut(StatutSeance.EN_ATTENTE);
            }

            return seanceRepository.update(seance);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la séance.", e);
        }
    }

    public boolean supprimerSeance(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la séance est requis.");
        }

        try {
            return seanceRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la séance.", e);
        }
    }

    public Optional<Seance> getSeanceById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la séance est requis.");
        }

        try {
            return seanceRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la séance.", e);
        }
    }

    public List<Seance> getAllSeances() {
        try {
            return seanceRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des séances.", e);
        }
    }

    public List<Seance> getSeancesByCoursId(Integer coursId) {
        if (coursId == null) {
            throw new IllegalArgumentException("L'id du cours est requis.");
        }

        try {
            return seanceRepository.findByCoursId(coursId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des séances par cours.", e);
        }
    }

    public List<Seance> getSeancesByEnseignantId(Integer enseignantId) {
        if (enseignantId == null) {
            throw new IllegalArgumentException("L'id de l'enseignant est requis.");
        }

        try {
            return seanceRepository.findByEnseignantId(enseignantId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des séances par enseignant.", e);
        }
    }

    public List<Seance> getSeancesByClasseId(Integer classeId) {
        if (classeId == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }

        try {
            return seanceRepository.findByClasseId(classeId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des séances par classe.", e);
        }
    }

    public List<Seance> getSeancesByStatut(StatutSeance statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis.");
        }

        try {
            return seanceRepository.findByStatut(statut);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des séances par statut.", e);
        }
    }

    public List<Seance> getSeancesParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont requises.");
        }

        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure ou égale à la date de fin.");
        }

        try {
            return seanceRepository.findByDateBetween(dateDebut, dateFin);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des séances par période.", e);
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
            throw new IllegalArgumentException("L'id de la séance est requis.");
        }

        if (statut == null) {
            throw new IllegalArgumentException("Le statut est requis.");
        }

        try {
            return seanceRepository.updateStatut(seanceId, statut, commentaireValidation);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la séance.", e);
        }
    }

    private void validateSeance(Seance seance) {
        if (seance == null) {
            throw new IllegalArgumentException("La séance est requise.");
        }

        if (seance.getCoursId() == null) {
            throw new IllegalArgumentException("Le cours est requis.");
        }

        if (seance.getEnseignantId() == null) {
            throw new IllegalArgumentException("L'enseignant est requis.");
        }

        if (seance.getDateSeance() == null) {
            throw new IllegalArgumentException("La date de séance est requise.");
        }

        if (seance.getHeureSeance() == null) {
            throw new IllegalArgumentException("L'heure de séance est requise.");
        }

        if (seance.getDuree() == null || seance.getDuree() <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à 0.");
        }

        if (seance.getContenu() == null || seance.getContenu().isBlank()) {
            throw new IllegalArgumentException("Le contenu de la séance est requis.");
        }
    }
}
