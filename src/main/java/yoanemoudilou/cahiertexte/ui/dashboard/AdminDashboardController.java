package yoanemoudilou.cahiertexte.ui.dashboard;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML private Label utilisateurLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalClassesLabel;
    @FXML private Label totalCoursLabel;
    @FXML private Label totalSeancesLabel;
    @FXML private Label pendingUsersLabel;
    @FXML private Label pendingSeancesLabel;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final ClasseService classeService = new ClasseService();
    private final CoursService coursService = new CoursService();
    private final SeanceService seanceService = new SeanceService();

    @FXML
    private void initialize() {
        chargerInfos();
    }

    @FXML
    private void handleRafraichir() {
        chargerInfos();
    }

    @FXML
    private void handleUsers(ActionEvent event) {
        ouvrirVue(event, "/view/users.fxml", "Gestion des utilisateurs");
    }

    @FXML
    private void handleCours(ActionEvent event) {
        ouvrirVue(event, "/view/cours.fxml", "Gestion des cours");
    }

    @FXML
    private void handleSeances(ActionEvent event) {
        ouvrirVue(event, "/view/seances.fxml", "Gestion des séances");
    }

    @FXML
    private void handleValidationSeances(ActionEvent event) {
        ouvrirVue(event, "/view/validation-seances.fxml", "Validation des séances");
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        ouvrirVue(event, "/view/statistiques.fxml", "Statistiques");
    }

    @FXML
    private void handleRapports(ActionEvent event) {
        ouvrirVue(event, "/view/reports.fxml", "Rapports");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        ouvrirVue(event, "/view/login.fxml", "Connexion");
    }

    private void chargerInfos() {
        try {
            User user = sessionManager.getUtilisateurConnecte();

            if (utilisateurLabel != null) {
                utilisateurLabel.setText(user != null ? user.getNomComplet() : "");
            }
            if (totalUsersLabel != null) {
                totalUsersLabel.setText(String.valueOf(userService.getAllUtilisateurs().size()));
            }
            if (totalClassesLabel != null) {
                totalClassesLabel.setText(String.valueOf(classeService.getAllClasses().size()));
            }
            if (totalCoursLabel != null) {
                totalCoursLabel.setText(String.valueOf(coursService.getAllCours().size()));
            }
            if (totalSeancesLabel != null) {
                totalSeancesLabel.setText(String.valueOf(seanceService.getAllSeances().size()));
            }
            if (pendingUsersLabel != null) {
                pendingUsersLabel.setText(String.valueOf(userService.getUtilisateursEnAttenteValidation().size()));
            }
            if (pendingSeancesLabel != null) {
                pendingSeancesLabel.setText(String.valueOf(
                        seanceService.getSeancesByStatut(StatutSeance.EN_ATTENTE).size()
                ));
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard admin.", e);
        }
    }

    private void ouvrirVue(ActionEvent event, String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            AlertUtils.showException("Navigation impossible", "Impossible de charger la vue : " + path, e);
        }
    }
}