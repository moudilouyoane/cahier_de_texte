package yoanemoudilou.cahiertexte.model;

/**
 * Classe de base représentant un utilisateur du système.
 */
public class User {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;
    private boolean valide;
    private boolean actif;

    /**
     * Constructeur vide.
     */
    public User() {
    }

    /**
     * Constructeur sans identifiant.
     */
    public User(String nom, String prenom, String email, String motDePasse,
                Role role, boolean valide, boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.valide = valide;
        this.actif = actif;
    }

    /**
     * Constructeur complet.
     */
    public User(Integer id, String nom, String prenom, String email, String motDePasse,
                Role role, boolean valide, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.valide = valide;
        this.actif = actif;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /**
     * Retourne le nom complet de l'utilisateur.
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", valide=" + valide +
                ", actif=" + actif +
                '}';
    }
}
