package yoanemoudilou.cahiertexte.model;

/**
 * Représente un responsable de classe.
 * Cette classe hérite de User et fixe automatiquement le rôle RESPONSABLE_CLASSE.
 */
public class ResponsableClasse extends User {

    private Classe classe;

    /**
     * Constructeur vide.
     */
    public ResponsableClasse() {
        super();
        setRole(Role.RESPONSABLE_CLASSE);
    }

    /**
     * Constructeur sans identifiant.
     */
    public ResponsableClasse(String nom, String prenom, String email, String motDePasse,
                             boolean valide, boolean actif) {
        super(nom, prenom, email, motDePasse, Role.RESPONSABLE_CLASSE, valide, actif);
    }

    /**
     * Constructeur complet.
     */
    public ResponsableClasse(Integer id, String nom, String prenom, String email, String motDePasse,
                             boolean valide, boolean actif) {
        super(id, nom, prenom, email, motDePasse, Role.RESPONSABLE_CLASSE, valide, actif);
    }

    public ResponsableClasse(String nom, String prenom, String email, String motDePasse,
                             boolean valide, boolean actif, Classe classe) {
        this(nom, prenom, email, motDePasse, valide, actif);
        this.classe = classe;
    }

    public ResponsableClasse(Integer id, String nom, String prenom, String email, String motDePasse,
                             boolean valide, boolean actif, Classe classe) {
        this(id, nom, prenom, email, motDePasse, valide, actif);
        this.classe = classe;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Integer getClasseId() {
        return classe != null ? classe.getId() : null;
    }

    @Override
    public String toString() {
        return "ResponsableClasse{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", valide=" + isValide() +
                ", actif=" + isActif() +
                ", classe=" + (classe != null ? classe.getNomClasse() + " - " + classe.getNiveau() : null) +
                '}';
    }
}
