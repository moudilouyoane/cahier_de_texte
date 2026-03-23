package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

/**
 * Contrôleur du tableau de bord principal.
 */
public class DashboardController {

    @FXML
    private Label utilisateurLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label utilisateursCountLabel;

    @FXML
    private Label coursCountLabel;

    @FXML
    private Label seancesCountLabel;

    @FXML
    private Label validationsUtilisateursLabel;

    @FXML
    private Label seancesEnAttenteLabel;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final CoursService coursService = new CoursService();
    private final SeanceService seanceService = new SeanceService();

    @FXML
    private void initialize() {
        chargerUtilisateurConnecte();
        chargerStatistiques();
    }

    @FXML
    private void handleRafraichir() {
        chargerUtilisateurConnecte();
        chargerStatistiques();
    }

    @FXML
    private void handleOuvrirUtilisateurs(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/user.fxml", "Gestion des utilisateurs");
    }

    @FXML
    private void handleOuvrirCours(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/cours.fxml", "Gestion des cours");
    }

    @FXML
    private void handleOuvrirSeances(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/seance.fxml", "Gestion des séances");
    }

    @FXML
    private void handleOuvrirValidationSeances(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/validation.fxml", "Validation des séances");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            authService.logout();
            AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de fermer la session.", e);
        }
    }

    private void chargerUtilisateurConnecte() {
        try {
            User user = sessionManager.getUtilisateurConnecte();

            if (user != null) {
                if (utilisateurLabel != null) {
                    utilisateurLabel.setText(user.getNomComplet());
                }
                if (roleLabel != null) {
                    roleLabel.setText(user.getRole() != null ? user.getRole().name() : "");
                }
            } else {
                if (utilisateurLabel != null) {
                    utilisateurLabel.setText("Aucun utilisateur");
                }
                if (roleLabel != null) {
                    roleLabel.setText("");
                }
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger l'utilisateur connecté.", e);
        }
    }

    private void chargerStatistiques() {
        try {
            if (utilisateursCountLabel != null) {
                utilisateursCountLabel.setText(String.valueOf(userService.getAllUtilisateurs().size()));
            }

            if (coursCountLabel != null) {
                coursCountLabel.setText(String.valueOf(coursService.getAllCours().size()));
            }

            if (seancesCountLabel != null) {
                seancesCountLabel.setText(String.valueOf(seanceService.getAllSeances().size()));
            }

            if (validationsUtilisateursLabel != null) {
                validationsUtilisateursLabel.setText(
                        String.valueOf(userService.getUtilisateursEnAttenteValidation().size())
                );
            }

            if (seancesEnAttenteLabel != null) {
                seancesEnAttenteLabel.setText(
                        String.valueOf(seanceService.getSeancesByStatut(StatutSeance.EN_ATTENTE).size())
                );
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger les statistiques.", e);
        }
    }
}
