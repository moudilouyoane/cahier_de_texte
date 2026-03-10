package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.repository.ClasseRepository;
import yoanemoudilou.cahiertexte.repository.impl.ClasseRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service métier lié aux classes.
 */
public class ClasseService {

    private final ClasseRepository classeRepository;

    public ClasseService() {
        this(new ClasseRepositoryImpl());
    }

    public ClasseService(ClasseRepository classeRepository) {
        this.classeRepository = classeRepository;
    }

    public Classe creerClasse(Classe classe) {
        validateClasse(classe);

        try {
            Optional<Classe> existing = classeRepository.findByNomClasseAndNiveau(
                    classe.getNomClasse().trim(),
                    classe.getNiveau().trim()
            );

            if (existing.isPresent()) {
                throw new IllegalArgumentException("Une classe avec ce nom et ce niveau existe déjà.");
            }

            classe.setNomClasse(classe.getNomClasse().trim());
            classe.setNiveau(classe.getNiveau().trim());

            return classeRepository.save(classe);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la classe.", e);
        }
    }

    public boolean modifierClasse(Classe classe) {
        if (classe == null || classe.getId() == null) {
            throw new IllegalArgumentException("La classe ou son id est invalide.");
        }

        validateClasse(classe);

        try {
            Optional<Classe> existing = classeRepository.findByNomClasseAndNiveau(
                    classe.getNomClasse().trim(),
                    classe.getNiveau().trim()
            );

            if (existing.isPresent() && !existing.get().getId().equals(classe.getId())) {
                throw new IllegalArgumentException("Une autre classe avec ce nom et ce niveau existe déjà.");
            }

            classe.setNomClasse(classe.getNomClasse().trim());
            classe.setNiveau(classe.getNiveau().trim());

            return classeRepository.update(classe);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la classe.", e);
        }
    }

    public boolean supprimerClasse(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }

        try {
            return classeRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la classe.", e);
        }
    }

    public Optional<Classe> getClasseById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("L'id de la classe est requis.");
        }

        try {
            return classeRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la classe.", e);
        }
    }

    public List<Classe> getAllClasses() {
        try {
            return classeRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des classes.", e);
        }
    }

    public List<Classe> getClassesByFiliereId(Integer filiereId) {
        if (filiereId == null) {
            throw new IllegalArgumentException("L'id de la filière est requis.");
        }

        try {
            return classeRepository.findByFiliereId(filiereId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des classes par filière.", e);
        }
    }

    public List<Classe> getClassesByNiveau(String niveau) {
        if (niveau == null || niveau.isBlank()) {
            throw new IllegalArgumentException("Le niveau est requis.");
        }

        try {
            return classeRepository.findByNiveau(niveau.trim());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des classes par niveau.", e);
        }
    }

    private void validateClasse(Classe classe) {
        if (classe == null) {
            throw new IllegalArgumentException("La classe est requise.");
        }

        if (classe.getNomClasse() == null || classe.getNomClasse().isBlank()) {
            throw new IllegalArgumentException("Le nom de la classe est requis.");
        }

        if (classe.getNiveau() == null || classe.getNiveau().isBlank()) {
            throw new IllegalArgumentException("Le niveau est requis.");
        }

        if (classe.getFiliere() == null || classe.getFiliere().getId() == null) {
            throw new IllegalArgumentException("La filière de la classe est requise.");
        }
    }
}
