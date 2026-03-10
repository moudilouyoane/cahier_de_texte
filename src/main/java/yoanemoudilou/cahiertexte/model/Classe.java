package yoanemoudilou.cahiertexte.model;

/**
 * Représente une classe universitaire.
 */
public class Classe {

    private Integer id;
    private String nomClasse;
    private String niveau;
    private Filiere filiere;

    /**
     * Constructeur vide.
     */
    public Classe() {
    }

    /**
     * Constructeur sans identifiant.
     */
    public Classe(String nomClasse, String niveau, Filiere filiere) {
        this.nomClasse = nomClasse;
        this.niveau = niveau;
        this.filiere = filiere;
    }

    /**
     * Constructeur complet.
     */
    public Classe(Integer id, String nomClasse, String niveau, Filiere filiere) {
        this.id = id;
        this.nomClasse = nomClasse;
        this.niveau = niveau;
        this.filiere = filiere;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomClasse() {
        return nomClasse;
    }

    public void setNomClasse(String nomClasse) {
        this.nomClasse = nomClasse;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    @Override
    public String toString() {
        return "Classe{" +
                "id=" + id +
                ", nomClasse='" + nomClasse + '\'' +
                ", niveau='" + niveau + '\'' +
                ", filiere=" + (filiere != null ? filiere.getNom() : null) +
                '}';
    }
}
