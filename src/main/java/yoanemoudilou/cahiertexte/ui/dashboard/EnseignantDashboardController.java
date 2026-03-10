package yoanemoudilou.cahiertexte.ui.dashboard;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AffectationService;
import yoanemoudilou.cahiertexte.service.AuthService;
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

public class EnseignantDashboardController {

    @FXML private Label utilisateurLabel;
    @FXML private Label totalCoursLabel;
    @FXML private Label totalAffectationsLabel;
    @FXML private Label totalSeancesLabel;
    @FXML private Label pendingSeancesLabel;
    @FXML private Label valideesSeancesLabel;
    @FXML private Label rejeteesSeancesLabel;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final CoursService coursService = new CoursService();
    private final AffectationService affectationService = new AffectationService();
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
    private void handleMesSeances(ActionEvent event) {
        ouvrirVue(event, "/view/seances.fxml", "Mes séances");
    }

    @FXML
    private void handleRapports(ActionEvent event) {
        ouvrirVue(event, "/view/reports.fxml", "Mes rapports");
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        ouvrirVue(event, "/view/statistiques.fxml", "Mes statistiques");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        ouvrirVue(event, "/view/login.fxml", "Connexion");
    }

    private void chargerInfos() {
        try {
            User currentUser = sessionManager.getUtilisateurConnecte();

            if (currentUser == null || currentUser.getId() == null) {
                return;
            }

            if (utilisateurLabel != null) {
                utilisateurLabel.setText(currentUser.getNomComplet());
            }
            if (totalCoursLabel != null) {
                totalCoursLabel.setText(String.valueOf(coursService.getCoursByEnseignantId(currentUser.getId()).size()));
            }
            if (totalAffectationsLabel != null) {
                totalAffectationsLabel.setText(String.valueOf(
                        affectationService.getAffectationsByEnseignantId(currentUser.getId()).size()
                ));
            }

            var seances = seanceService.getSeancesByEnseignantId(currentUser.getId());

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
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard enseignant.", e);
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
