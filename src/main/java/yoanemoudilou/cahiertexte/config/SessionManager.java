package yoanemoudilou.cahiertexte.config;

import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;

/**
 * Gère la session utilisateur en mémoire.
 * Cette classe permet de :
 * - stocker l'utilisateur connecté ;
 * - vérifier son rôle ;
 * - fermer proprement la session.
 */
public final class SessionManager {

    private static SessionManager instance;

    private User utilisateurConnecte;

    private SessionManager() {
        // Empêche l'instanciation externe.
    }

    /**
     * Retourne l'unique instance du gestionnaire de session.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Ouvre une session avec l'utilisateur connecté.
     */
    public void ouvrirSession(User utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    /**
     * Ferme la session courante.
     */
    public void fermerSession() {
        this.utilisateurConnecte = null;
    }

    /**
     * Retourne l'utilisateur connecté.
     */
    public User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    /**
     * Retourne l'identifiant de l'utilisateur connecté.
     */
    public Integer getUtilisateurId() {
        return utilisateurConnecte != null ? utilisateurConnecte.getId() : null;
    }

    /**
     * Indique si un utilisateur est connecté.
     */
    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    /**
     * Vérifie si l'utilisateur connecté possède le rôle donné.
     */
    public boolean aLeRole(Role role) {
        return estConnecte() && utilisateurConnecte.getRole() == role;
    }

    /**
     * Vérifie si l'utilisateur connecté est un chef de département.
     */
    public boolean estChefDepartement() {
        return aLeRole(Role.CHEF_DEPARTEMENT);
    }

    /**
     * Vérifie si l'utilisateur connecté est un enseignant.
     */
    public boolean estEnseignant() {
        return aLeRole(Role.ENSEIGNANT);
    }

    /**
     * Vérifie si l'utilisateur connecté est un responsable de classe.
     */
    public boolean estResponsableClasse() {
        return aLeRole(Role.RESPONSABLE_CLASSE);
    }

    /**
     * Retourne le nom complet de l'utilisateur connecté.
     */
    public String getNomCompletUtilisateur() {
        return estConnecte() ? utilisateurConnecte.getNomComplet() : "";
    }
}
