package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.FiliereService;
import yoanemoudilou.cahiertexte.service.NotificationService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

/**
 * Controleur du dashboard chef de departement.
 */
public class AdminDashboardController {

    private static final String USER_VIEW = "/yoanemoudilou/cahiertexte/view/user.fxml";
    private static final String ENSEIGNANT_VIEW = "/yoanemoudilou/cahiertexte/view/enseignant-admin.fxml";
    private static final String FILIERE_VIEW = "/yoanemoudilou/cahiertexte/view/filiere.fxml";
    private static final String CLASSE_VIEW = "/yoanemoudilou/cahiertexte/view/classe.fxml";
    private static final String COURS_VIEW = "/yoanemoudilou/cahiertexte/view/cours.fxml";
    private static final String CAHIER_VIEW = "/yoanemoudilou/cahiertexte/view/cahier.fxml";
    private static final String VALIDATION_VIEW = "/yoanemoudilou/cahiertexte/view/validation.fxml";
    private static final String STATS_VIEW = "/yoanemoudilou/cahiertexte/view/stats.fxml";
    private static final String REPORT_VIEW = "/yoanemoudilou/cahiertexte/view/report.fxml";
    private static final String NOTIFICATIONS_VIEW = "/yoanemoudilou/cahiertexte/view/notifications.fxml";

    @FXML
    private Label bienvenuLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label totalUtilisateursLabel;

    @FXML
    private Label totalEnseignantsLabel;

    @FXML
    private Label totalResponsablesLabel;

    @FXML
    private Label utilisateursEnAttenteLabel;

    @FXML
    private Label totalFilieresLabel;

    @FXML
    private Label totalClassesLabel;

    @FXML
    private Label totalCoursLabel;

    @FXML
    private Label totalSeancesLabel;

    @FXML
    private Label seancesEnAttenteLabel;

    @FXML
    private Label seancesValideesLabel;

    @FXML
    private Label seancesRejeteesLabel;

    @FXML
    private Label notificationsCountLabel;

    @FXML
    private StackPane contentContainer;

    @FXML
    private ScrollPane dashboardContent;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final FiliereService filiereService = new FiliereService();
    private final ClasseService classeService = new ClasseService();
    private final CoursService coursService = new CoursService();
    private final NotificationService notificationService = new NotificationService();
    private final SeanceService seanceService = new SeanceService();

    @FXML
    private void initialize() {
        chargerInfos();
        afficherDashboard();
    }

    @FXML
    private void handleRafraichir() {
        chargerInfos();
        afficherDashboard();
    }

    @FXML
    private void handleUsers(ActionEvent event) {
        afficherModule(USER_VIEW);
    }

    @FXML
    private void handleEnseignants(ActionEvent event) {
        afficherModule(ENSEIGNANT_VIEW);
    }

    @FXML
    private void handleOuvrirFilieres(ActionEvent event) {
        afficherModule(FILIERE_VIEW);
    }

    @FXML
    private void handleOuvrirClasses(ActionEvent event) {
        afficherModule(CLASSE_VIEW);
    }

    @FXML
    private void handleCours(ActionEvent event) {
        afficherModule(COURS_VIEW);
    }

    @FXML
    private void handleOuvrirCahiers(ActionEvent event) {
        afficherModule(CAHIER_VIEW);
    }

    @FXML
    private void handleValidationSeances(ActionEvent event) {
        afficherModule(VALIDATION_VIEW);
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        afficherModule(STATS_VIEW);
    }

    @FXML
    private void handleRapports(ActionEvent event) {
        afficherModule(REPORT_VIEW);
    }

    @FXML
    private void handleNotifications(ActionEvent event) {
        afficherModule(NOTIFICATIONS_VIEW);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }

    private void chargerInfos() {
        try {
            User user = sessionManager.getUtilisateurConnecte();
            var seances = seanceService.getAllSeances();

            setLabel(bienvenuLabel, user != null ? "Bienvenue, " + user.getNomComplet() : "Bienvenue");
            setLabel(roleLabel, user != null && user.getRole() != null ? user.getRole().name() : "");
            setLabel(totalUtilisateursLabel, String.valueOf(userService.getAllUtilisateurs().size()));
            setLabel(totalEnseignantsLabel, String.valueOf(userService.getUtilisateursByRole(Role.ENSEIGNANT).size()));
            setLabel(totalResponsablesLabel, String.valueOf(userService.getUtilisateursByRole(Role.RESPONSABLE_CLASSE).size()));
            setLabel(utilisateursEnAttenteLabel, String.valueOf(userService.getUtilisateursEnAttenteValidation().size()));
            setLabel(totalFilieresLabel, String.valueOf(filiereService.getAllFilieres().size()));
            setLabel(totalClassesLabel, String.valueOf(classeService.getAllClasses().size()));
            setLabel(totalCoursLabel, String.valueOf(coursService.getAllCours().size()));
            setLabel(totalSeancesLabel, String.valueOf(seances.size()));
            setLabel(seancesEnAttenteLabel, String.valueOf(
                    seances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count()
            ));
            setLabel(seancesValideesLabel, String.valueOf(
                    seances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count()
            ));
            setLabel(seancesRejeteesLabel, String.valueOf(
                    seances.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count()
            ));
            chargerNotifications(user);
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard admin.", e);
        }
    }

    private void chargerNotifications(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        setLabel(notificationsCountLabel, String.valueOf(notificationService.countNotificationsNonLues(user.getId())));
    }

    private void setLabel(Label label, String value) {
        if (label != null) {
            label.setText(value != null ? value : "");
        }
    }

    private void afficherDashboard() {
        if (contentContainer != null && dashboardContent != null) {
            contentContainer.getChildren().setAll(dashboardContent);
        }
    }

    private void afficherModule(String fxmlPath) {
        if (contentContainer == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (loader.getController() instanceof NotificationsCenterController notificationsController) {
                notificationsController.setAfterRefresh(this::chargerInfos);
            }
            masquerBoutonsRetour(root);
            contentContainer.getChildren().setAll(root);
            chargerInfos();
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le module.", e);
        }
    }

    private void masquerBoutonsRetour(Parent root) {
        if (root == null) {
            return;
        }

        for (var node : root.lookupAll(".module-back-button")) {
            node.setManaged(false);
            node.setVisible(false);
        }

        for (var node : root.lookupAll(".top-bar")) {
            if (node instanceof javafx.scene.layout.Pane pane) {
                for (var child : pane.getChildren()) {
                    if (child instanceof Button button && button.getStyleClass().contains("module-back-button")) {
                        button.setManaged(false);
                        button.setVisible(false);
                    }
                }
            }
        }
    }
}
