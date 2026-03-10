package yoanemoudilou.cahiertexte.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente une séance de cours enregistrée dans le cahier de texte.
 */
public class Seance {

    private Integer id;
    private Integer coursId;
    private Integer enseignantId;
    private LocalDate dateSeance;
    private LocalTime heureSeance;
    private Integer duree;
    private String contenu;
    private String observations;
    private StatutSeance statut;
    private String commentaireValidation;

    /**
     * Constructeur vide.
     */
    public Seance() {
        this.statut = StatutSeance.EN_ATTENTE;
    }

    /**
     * Constructeur sans identifiant.
     */
    public Seance(Integer coursId, Integer enseignantId, LocalDate dateSeance, LocalTime heureSeance,
                  Integer duree, String contenu, String observations,
                  StatutSeance statut, String commentaireValidation) {
        this.coursId = coursId;
        this.enseignantId = enseignantId;
        this.dateSeance = dateSeance;
        this.heureSeance = heureSeance;
        this.duree = duree;
        this.contenu = contenu;
        this.observations = observations;
        this.statut = statut;
        this.commentaireValidation = commentaireValidation;
    }

    /**
     * Constructeur complet.
     */
    public Seance(Integer id, Integer coursId, Integer enseignantId, LocalDate dateSeance,
                  LocalTime heureSeance, Integer duree, String contenu,
                  String observations, StatutSeance statut, String commentaireValidation) {
        this.id = id;
        this.coursId = coursId;
        this.enseignantId = enseignantId;
        this.dateSeance = dateSeance;
        this.heureSeance = heureSeance;
        this.duree = duree;
        this.contenu = contenu;
        this.observations = observations;
        this.statut = statut;
        this.commentaireValidation = commentaireValidation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoursId() {
        return coursId;
    }

    public void setCoursId(Integer coursId) {
        this.coursId = coursId;
    }

    public Integer getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Integer enseignantId) {
        this.enseignantId = enseignantId;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public LocalTime getHeureSeance() {
        return heureSeance;
    }

    public void setHeureSeance(LocalTime heureSeance) {
        this.heureSeance = heureSeance;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public StatutSeance getStatut() {
        return statut;
    }

    public void setStatut(StatutSeance statut) {
        this.statut = statut;
    }

    public String getCommentaireValidation() {
        return commentaireValidation;
    }

    public void setCommentaireValidation(String commentaireValidation) {
        this.commentaireValidation = commentaireValidation;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "id=" + id +
                ", coursId=" + coursId +
                ", enseignantId=" + enseignantId +
                ", dateSeance=" + dateSeance +
                ", heureSeance=" + heureSeance +
                ", duree=" + duree +
                ", contenu='" + contenu + '\'' +
                ", observations='" + observations + '\'' +
                ", statut=" + statut +
                ", commentaireValidation='" + commentaireValidation + '\'' +
                '}';
    }
}
