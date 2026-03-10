package yoanemoudilou.cahiertexte.ui.dashboard;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ResponsableDashboardController {

    @FXML private Label utilisateurLabel;
    @FXML private Label scopeLabel;
    @FXML private Label totalClassesLabel;
    @FXML private Label totalCoursLabel;
    @FXML private Label totalSeancesLabel;
    @FXML private Label pendingSeancesLabel;
    @FXML private Label valideesSeancesLabel;
    @FXML private Label rejeteesSeancesLabel;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
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
    private void handleCours(ActionEvent event) {
        ouvrirVue(event, "/view/cours.fxml", "Cours");
    }

    @FXML
    private void handleSeances(ActionEvent event) {
        ouvrirVue(event, "/view/seances.fxml", "Séances");
    }

    @FXML
    private void handleValidation(ActionEvent event) {
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
            User currentUser = sessionManager.getUtilisateurConnecte();

            if (utilisateurLabel != null) {
                utilisateurLabel.setText(currentUser != null ? currentUser.getNomComplet() : "");
            }

            /*
             * Limitation actuelle du modèle :
             * on n'a pas encore de relation directe entre RESPONSABLE_CLASSE et une classe précise.
             * Donc ce dashboard affiche pour l'instant une vue globale de supervision.
             */
            if (scopeLabel != null) {
                scopeLabel.setText("Vue actuelle : supervision globale (liaison responsable -> classe à ajouter plus tard)");
            }

            if (totalClassesLabel != null) {
                totalClassesLabel.setText(String.valueOf(classeService.getAllClasses().size()));
            }
            if (totalCoursLabel != null) {
                totalCoursLabel.setText(String.valueOf(coursService.getAllCours().size()));
            }

            var seances = seanceService.getAllSeances();

            if (totalSeancesLabel != null) {
                totalSeancesLabel.setText(String.valueOf(seances.size()));
            }
            if (pendingSeancesLabel != null) {
                pendingSeancesLabel.setText(String.valueOf(
                        seances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count()
                ));
            }
            if (valideesSeancesLabel != null) {
                valideesSeancesLabel.setText(String.valueOf(
                        seances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count()
                ));
            }
            if (rejeteesSeancesLabel != null) {
                rejeteesSeancesLabel.setText(String.valueOf(
                        seances.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count()
                ));
            }

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard responsable.", e);
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
