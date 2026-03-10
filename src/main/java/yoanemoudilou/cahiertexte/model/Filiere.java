package yoanemoudilou.cahiertexte.model;

/**
 * Représente une filière universitaire.
 */
public class Filiere {

    private Integer id;
    private String code;
    private String nom;

    /**
     * Constructeur vide.
     */
    public Filiere() {
    }

    /**
     * Constructeur sans identifiant.
     */
    public Filiere(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    /**
     * Constructeur complet.
     */
    public Filiere(Integer id, String code, String nom) {
        this.id = id;
        this.code = code;
        this.nom = nom;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Filiere{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}
