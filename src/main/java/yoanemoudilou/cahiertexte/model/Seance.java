package yoanemoudilou.cahiertexte.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente une séance de cours enregistrée dans un cahier de texte.
 */
public class Seance {

    private Integer id;
    private Integer cahierTexteId;
    private Integer coursId;
    private Integer enseignantId;
    private LocalDate dateSeance;
    private LocalTime heureSeance;
    private Integer duree;
    private String contenu;
    private String observations;
    private StatutSeance statut;
    private String commentaireValidation;

    public Seance() {
        this.statut = StatutSeance.EN_ATTENTE;
    }

    public Seance(Integer coursId, Integer enseignantId, LocalDate dateSeance, LocalTime heureSeance,
                  Integer duree, String contenu, String observations, StatutSeance statut, String commentaireValidation) {
        this(null, coursId, enseignantId, dateSeance, heureSeance, duree, contenu, observations, statut, commentaireValidation);
    }

    public Seance(Integer cahierTexteId, Integer coursId, Integer enseignantId, LocalDate dateSeance, LocalTime heureSeance,
                  Integer duree, String contenu, String observations, StatutSeance statut, String commentaireValidation) {
        this.cahierTexteId = cahierTexteId;
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

    public Seance(Integer id, Integer cahierTexteId, Integer coursId, Integer enseignantId, LocalDate dateSeance,
                  LocalTime heureSeance, Integer duree, String contenu, String observations,
                  StatutSeance statut, String commentaireValidation) {
        this.id = id;
        this.cahierTexteId = cahierTexteId;
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

    public Integer getCahierTexteId() {
        return cahierTexteId;
    }

    public void setCahierTexteId(Integer cahierTexteId) {
        this.cahierTexteId = cahierTexteId;
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
                ", cahierTexteId=" + cahierTexteId +
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
