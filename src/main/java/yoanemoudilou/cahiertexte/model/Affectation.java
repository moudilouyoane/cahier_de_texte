package yoanemoudilou.cahiertexte.model;

/**
 * Représente l'affectation d'un cours à un enseignant.
 */
public class Affectation {

    private Integer id;
    private Integer enseignantId;
    private Integer coursId;

    /**
     * Constructeur vide.
     */
    public Affectation() {
    }

    /**
     * Constructeur sans identifiant.
     */
    public Affectation(Integer enseignantId, Integer coursId) {
        this.enseignantId = enseignantId;
        this.coursId = coursId;
    }

    /**
     * Constructeur complet.
     */
    public Affectation(Integer id, Integer enseignantId, Integer coursId) {
        this.id = id;
        this.enseignantId = enseignantId;
        this.coursId = coursId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Integer enseignantId) {
        this.enseignantId = enseignantId;
    }

    public Integer getCoursId() {
        return coursId;
    }

    public void setCoursId(Integer coursId) {
        this.coursId = coursId;
    }

    @Override
    public String toString() {
        return "Affectation{" +
                "id=" + id +
                ", enseignantId=" + enseignantId +
                ", coursId=" + coursId +
                '}';
    }
}

