package yoanemoudilou.cahiertexte.model;

/**
 * Représente un cours dispensé dans une classe.
 */
public class Cours {

    private Integer id;
    private String code;
    private String intitule;
    private Integer volumeHoraire;
    private Integer classeId;

    /**
     * Constructeur vide.
     */
    public Cours() {
    }

    /**
     * Constructeur sans identifiant.
     */
    public Cours(String code, String intitule, Integer volumeHoraire, Integer classeId) {
        this.code = code;
        this.intitule = intitule;
        this.volumeHoraire = volumeHoraire;
        this.classeId = classeId;
    }

    /**
     * Constructeur complet.
     */
    public Cours(Integer id, String code, String intitule, Integer volumeHoraire, Integer classeId) {
        this.id = id;
        this.code = code;
        this.intitule = intitule;
        this.volumeHoraire = volumeHoraire;
        this.classeId = classeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public Integer getVolumeHoraire() {
        return volumeHoraire;
    }

    public void setVolumeHoraire(Integer volumeHoraire) {
        this.volumeHoraire = volumeHoraire;
    }

    public Integer getClasseId() {
        return classeId;
    }

    public void setClasseId(Integer classeId) {
        this.classeId = classeId;
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", intitule='" + intitule + '\'' +
                ", volumeHoraire=" + volumeHoraire +
                ", classeId=" + classeId +
                '}';
    }
}