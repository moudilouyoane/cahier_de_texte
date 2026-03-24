package yoanemoudilou.cahiertexte.model;

import java.time.LocalDateTime;

/**
 * Représente une notification interne de l'application.
 */
public class NotificationApp {

    private Integer id;
    private Integer destinataireId;
    private String titre;
    private String message;
    private boolean lue;
    private LocalDateTime dateCreation;

    public NotificationApp() {
    }

    public NotificationApp(Integer destinataireId, String titre, String message, boolean lue, LocalDateTime dateCreation) {
        this.destinataireId = destinataireId;
        this.titre = titre;
        this.message = message;
        this.lue = lue;
        this.dateCreation = dateCreation;
    }

    public NotificationApp(Integer id, Integer destinataireId, String titre, String message, boolean lue, LocalDateTime dateCreation) {
        this.id = id;
        this.destinataireId = destinataireId;
        this.titre = titre;
        this.message = message;
        this.lue = lue;
        this.dateCreation = dateCreation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(Integer destinataireId) {
        this.destinataireId = destinataireId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLue() {
        return lue;
    }

    public void setLue(boolean lue) {
        this.lue = lue;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
