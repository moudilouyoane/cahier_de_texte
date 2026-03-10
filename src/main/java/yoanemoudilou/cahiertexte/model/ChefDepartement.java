package yoanemoudilou.cahiertexte.model;

/**
 * Représente un chef de département ou responsable de filière.
 * Cette classe hérite de User et fixe automatiquement le rôle CHEF_DEPARTEMENT.
 */
public class ChefDepartement extends User {

    /**
     * Constructeur vide.
     */
    public ChefDepartement() {
        super();
        setRole(Role.CHEF_DEPARTEMENT);
    }

    /**
     * Constructeur sans identifiant.
     */
    public ChefDepartement(String nom, String prenom, String email, String motDePasse,
                           boolean valide, boolean actif) {
        super(nom, prenom, email, motDePasse, Role.CHEF_DEPARTEMENT, valide, actif);
    }

    /**
     * Constructeur complet.
     */
    public ChefDepartement(Integer id, String nom, String prenom, String email, String motDePasse,
                           boolean valide, boolean actif) {
        super(id, nom, prenom, email, motDePasse, Role.CHEF_DEPARTEMENT, valide, actif);
    }

    @Override
    public String toString() {
        return "ChefDepartement{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", valide=" + isValide() +
                ", actif=" + isActif() +
                '}';
    }
}