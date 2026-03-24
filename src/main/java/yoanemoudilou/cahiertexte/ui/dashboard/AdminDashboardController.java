package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    private ListView<String> notificationsListView;

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
    }

    @FXML
    private void handleRafraichir() {
        chargerInfos();
    }

    @FXML
    private void handleUsers(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/user.fxml", "Gestion des utilisateurs");
    }

    @FXML
    private void handleEnseignants(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/enseignant-admin.fxml", "Enseignants");
    }

    @FXML
    private void handleOuvrirFilieres(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/filiere.fxml", "Gestion des filieres");
    }

    @FXML
    private void handleOuvrirClasses(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/classe.fxml", "Gestion des classes");
    }

    @FXML
    private void handleCours(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/cours.fxml", "Gestion des cours");
    }

    @FXML
    private void handleOuvrirCahiers(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/cahier.fxml", "Cahiers de texte");
    }

    @FXML
    private void handleValidationSeances(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/validation.fxml", "Validation des seances");
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/stats.fxml", "Statistiques");
    }

    @FXML
    private void handleRapports(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/report.fxml", "Rapports");
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

        var notifications = notificationService.getNotificationsPourUtilisateur(user.getId(), 8);

        if (notificationsListView != null) {
            notificationsListView.setItems(FXCollections.observableArrayList(
                    notifications.stream()
                            .map(n -> n.getTitre() + " - " + n.getMessage())
                            .toList()
            ));
        }

        setLabel(notificationsCountLabel, String.valueOf(notificationService.countNotificationsNonLues(user.getId())));
        notificationService.marquerToutesCommeLues(user.getId());
    }

    private void setLabel(Label label, String value) {
        if (label != null) {
            label.setText(value != null ? value : "");
        }
    }
}
