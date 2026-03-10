package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.repository.CoursRepository;
import yoanemoudilou.cahiertexte.repository.impl.CoursRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service métier lié aux cours.
 */
public class CoursService {

    private final CoursRepository coursRepository;

    public CoursService() {
        this(new CoursRepositoryImpl());
    }

    public CoursService(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    public Cours creerCours(Cours cours) {
        validateCours(cours);

        try {
            cours.setCode(normalizeCode(cours.getCode()));

            Optional<Cours> existing = coursRepository.findByCode(cours.getCode());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Un cours avec ce code existe déjà.");
            }

            return coursRepository.save(cours);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du cours.", e);
        }
    }

    public boolean modifierCours(Cours cours) {
        if (cours == null || cours.getId() == null) {
            throw new IllegalArgumentException("Le cours ou son id est invalide.");
        }

        validateCours(cours);

        try {
            cours.setCode(normalizeCode(cours.getCode()));

            Optional<Cours> existing = coursRepository.findByCode(cours.getCode());
            if (existing.isPresent() && !existing.get().getId().equals(cours.getId())) {
                throw new IllegalArgumentException("Ce code de cours est déjà utilisé.");
            }

            return coursRepository.update(cours);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du cours.", e);
        }
    }

    public boolean supprimerCours(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id du cours est requis.");
        }

        try {
            return coursRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du cours.", e);
        }
    }

    public Optional<Cours> getCoursById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id du cours est requis.");
        }

        try {
            return coursRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du cours.", e);
        }
    }

    public Optional<Cours> getCoursByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code du cours est requis.");
        }

        try {
            return coursRepository.findByCode(normalizeCode(code));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du cours par code.", e);
        }
    }

    public List<Cours> getAllCours() {
        try {
            return coursRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des cours.", e);
        }
    }

    public List<Cours> getCoursByClasseId(Integer classeId) {
        if (classeId == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }

        try {
            return coursRepository.findByClasseId(classeId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des cours par classe.", e);
        }
    }

    public List<Cours> getCoursByFiliereId(Integer filiereId) {
        if (filiereId == null) {
            throw new IllegalArgumentException("L'id de la filière est requis.");
        }

        try {
            return coursRepository.findByFiliereId(filiereId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des cours par filière.", e);
        }
    }

    public List<Cours> getCoursByEnseignantId(Integer enseignantId) {
        if (enseignantId == null) {
            throw new IllegalArgumentException("L'id de l'enseignant est requis.");
        }

        try {
            return coursRepository.findByEnseignantId(enseignantId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des cours par enseignant.", e);
        }
    }

    private void validateCours(Cours cours) {
        if (cours == null) {
            throw new IllegalArgumentException("Le cours est requis.");
        }

        if (cours.getCode() == null || cours.getCode().isBlank()) {
            throw new IllegalArgumentException("Le code du cours est requis.");
        }

        if (cours.getIntitule() == null || cours.getIntitule().isBlank()) {
            throw new IllegalArgumentException("L'intitulé du cours est requis.");
        }

        if (cours.getVolumeHoraire() == null || cours.getVolumeHoraire() <= 0) {
            throw new IllegalArgumentException("Le volume horaire doit être supérieur à 0.");
        }

        if (cours.getClasseId() == null) {
            throw new IllegalArgumentException("La classe du cours est requise.");
        }
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }
}