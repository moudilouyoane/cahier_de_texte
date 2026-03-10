package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.Affectation;
import yoanemoudilou.cahiertexte.repository.AffectationRepository;
import yoanemoudilou.cahiertexte.repository.impl.AffectationRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service métier lié aux affectations.
 */
public class AffectationService {

    private final AffectationRepository affectationRepository;

    public AffectationService() {
        this(new AffectationRepositoryImpl());
    }

    public AffectationService(AffectationRepository affectationRepository) {
        this.affectationRepository = affectationRepository;
    }

    public Affectation creerAffectation(Affectation affectation) {
        validateAffectation(affectation);

        try {
            boolean alreadyExists = affectationRepository.existsByEnseignantIdAndCoursId(
                    affectation.getEnseignantId(),
                    affectation.getCoursId()
            );

            if (alreadyExists) {
                throw new IllegalArgumentException("Cette affectation existe déjà.");
            }

            return affectationRepository.save(affectation);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'affectation.", e);
        }
    }

    public boolean modifierAffectation(Affectation affectation) {
        if (affectation == null || affectation.getId() == null) {
            throw new IllegalArgumentException("L'affectation ou son id est invalide.");
        }

        validateAffectation(affectation);

        try {
            Optional<Affectation> existing = affectationRepository.findByEnseignantIdAndCoursId(
                    affectation.getEnseignantId(),
                    affectation.getCoursId()
            );

            if (existing.isPresent() && !existing.get().getId().equals(affectation.getId())) {
                throw new IllegalArgumentException("Une autre affectation identique existe déjà.");
            }

            return affectationRepository.update(affectation);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de l'affectation.", e);
        }
    }

    public boolean supprimerAffectation(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de l'affectation est requis.");
        }

        try {
            return affectationRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'affectation.", e);
        }
    }

    public Optional<Affectation> getAffectationById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de l'affectation est requis.");
        }

        try {
            return affectationRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'affectation.", e);
        }
    }

    public List<Affectation> getAllAffectations() {
        try {
            return affectationRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des affectations.", e);
        }
    }

    public List<Affectation> getAffectationsByEnseignantId(Integer enseignantId) {
        if (enseignantId == null) {
            throw new IllegalArgumentException("L'id de l'enseignant est requis.");
        }

        try {
            return affectationRepository.findByEnseignantId(enseignantId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des affectations par enseignant.", e);
        }
    }

    public List<Affectation> getAffectationsByCoursId(Integer coursId) {
        if (coursId == null) {
            throw new IllegalArgumentException("L'id du cours est requis.");
        }

        try {
            return affectationRepository.findByCoursId(coursId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des affectations par cours.", e);
        }
    }

    public boolean existeAffectation(Integer enseignantId, Integer coursId) {
        if (enseignantId == null || coursId == null) {
            throw new IllegalArgumentException("Les ids enseignant et cours sont requis.");
        }

        try {
            return affectationRepository.existsByEnseignantIdAndCoursId(enseignantId, coursId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de l'affectation.", e);
        }
    }

    private void validateAffectation(Affectation affectation) {
        if (affectation == null) {
            throw new IllegalArgumentException("L'affectation est requise.");
        }

        if (affectation.getEnseignantId() == null) {
            throw new IllegalArgumentException("L'enseignant est requis.");
        }

        if (affectation.getCoursId() == null) {
            throw new IllegalArgumentException("Le cours est requis.");
        }
    }
}
