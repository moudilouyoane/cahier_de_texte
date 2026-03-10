package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.Filiere;
import yoanemoudilou.cahiertexte.repository.FiliereRepository;
import yoanemoudilou.cahiertexte.repository.impl.FiliereRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service métier lié aux filières.
 */
public class FiliereService {

    private final FiliereRepository filiereRepository;

    public FiliereService() {
        this(new FiliereRepositoryImpl());
    }

    public FiliereService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    public Filiere creerFiliere(Filiere filiere) {
        validateFiliere(filiere);

        try {
            filiere.setCode(normalizeCode(filiere.getCode()));

            if (filiereRepository.existsByCode(filiere.getCode())) {
                throw new IllegalArgumentException("Une filière avec ce code existe déjà.");
            }

            return filiereRepository.save(filiere);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la filière.", e);
        }
    }

    public boolean modifierFiliere(Filiere filiere) {
        if (filiere == null || filiere.getId() == null) {
            throw new IllegalArgumentException("La filière ou son id est invalide.");
        }

        validateFiliere(filiere);

        try {
            filiere.setCode(normalizeCode(filiere.getCode()));

            Optional<Filiere> existing = filiereRepository.findByCode(filiere.getCode());
            if (existing.isPresent() && !existing.get().getId().equals(filiere.getId())) {
                throw new IllegalArgumentException("Ce code de filière est déjà utilisé.");
            }

            return filiereRepository.update(filiere);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la filière.", e);
        }
    }

    public boolean supprimerFiliere(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la filière est requis.");
        }

        try {
            return filiereRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la filière.", e);
        }
    }

    public Optional<Filiere> getFiliereById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la filière est requis.");
        }

        try {
            return filiereRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la filière.", e);
        }
    }

    public Optional<Filiere> getFiliereByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code de la filière est requis.");
        }

        try {
            return filiereRepository.findByCode(normalizeCode(code));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la filière par code.", e);
        }
    }

    public List<Filiere> getAllFilieres() {
        try {
            return filiereRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des filières.", e);
        }
    }

    private void validateFiliere(Filiere filiere) {
        if (filiere == null) {
            throw new IllegalArgumentException("La filière est requise.");
        }

        if (filiere.getCode() == null || filiere.getCode().isBlank()) {
            throw new IllegalArgumentException("Le code de la filière est requis.");
        }

        if (filiere.getNom() == null || filiere.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la filière est requis.");
        }
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }
}
