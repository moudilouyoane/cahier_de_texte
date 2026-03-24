package yoanemoudilou.cahiertexte.model;

import java.time.LocalDateTime;

/**
 * Représente un cahier de texte rattaché à une classe et une période.
 */
public class CahierTexte {

    private Integer id;
    private Integer classeId;
    private String anneeScolaire;
    private Semestre semestre;
    private LocalDateTime dateCreation;

    public CahierTexte() {
    }

    public CahierTexte(Integer classeId, String anneeScolaire, Semestre semestre, LocalDateTime dateCreation) {
        this.classeId = classeId;
        this.anneeScolaire = anneeScolaire;
        this.semestre = semestre;
        this.dateCreation = dateCreation;
    }

    public CahierTexte(Integer id, Integer classeId, String anneeScolaire, Semestre semestre, LocalDateTime dateCreation) {
        this.id = id;
        this.classeId = classeId;
        this.anneeScolaire = anneeScolaire;
        this.semestre = semestre;
        this.dateCreation = dateCreation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClasseId() {
        return classeId;
    }

    public void setClasseId(Integer classeId) {
        this.classeId = classeId;
    }

    public String getAnneeScolaire() {
        return anneeScolaire;
    }

    public void setAnneeScolaire(String anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
