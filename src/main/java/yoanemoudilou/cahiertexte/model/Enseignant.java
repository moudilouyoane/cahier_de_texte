package yoanemoudilou.cahiertexte.model;

/**
 * Représente un enseignant.
 * Cette classe hérite de User et fixe automatiquement le rôle ENSEIGNANT.
 */
public class Enseignant extends User {

    /**
     * Constructeur vide.
     */
    public Enseignant() {
        super();
        setRole(Role.ENSEIGNANT);
    }

    /**
     * Constructeur sans identifiant.
     */
    public Enseignant(String nom, String prenom, String email, String motDePasse,
                      boolean valide, boolean actif) {
        super(nom, prenom, email, motDePasse, Role.ENSEIGNANT, valide, actif);
    }

    /**
     * Constructeur complet.
     */
    public Enseignant(Integer id, String nom, String prenom, String email, String motDePasse,
                      boolean valide, boolean actif) {
        super(id, nom, prenom, email, motDePasse, Role.ENSEIGNANT, valide, actif);
    }

    @Override
    public String toString() {
        return "Enseignant{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", valide=" + isValide() +
                ", actif=" + isActif() +
                '}';
    }
}