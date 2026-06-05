package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.NotificationService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.DateUtils;

/**
 * Controleur du centre de notifications integre au shell admin.
 */
public class NotificationsCenterController {

    @FXML
    private Label totalNotificationsLabel;

    @FXML
    private Label notificationsNonLuesLabel;

    @FXML
    private ListView<String> notificationsListView;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final NotificationService notificationService = new NotificationService();

    private Runnable afterRefresh;

    @FXML
    private void initialize() {
        chargerNotifications();
    }

    @FXML
    private void handleRafraichir() {
        chargerNotifications();
    }

    @FXML
    private void handleMarquerToutCommeLu() {
        try {
            User user = sessionManager.getUtilisateurConnecte();
            if (user == null || user.getId() == null) {
                return;
            }

            notificationService.marquerToutesCommeLues(user.getId());
            chargerNotifications();
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de mettre a jour les notifications.", e);
        }
    }

    void setAfterRefresh(Runnable afterRefresh) {
        this.afterRefresh = afterRefresh;
    }

    private void chargerNotifications() {
        try {
            User user = sessionManager.getUtilisateurConnecte();
            if (user == null || user.getId() == null) {
                setLabel(totalNotificationsLabel, "0");
                setLabel(notificationsNonLuesLabel, "0");
                if (notificationsListView != null) {
                    notificationsListView.setItems(FXCollections.observableArrayList());
                }
                runAfterRefresh();
                return;
            }

            long nonLues = notificationService.countNotificationsNonLues(user.getId());
            var notifications = notificationService.getNotificationsPourUtilisateur(user.getId(), 50);

            if (notificationsListView != null) {
                notificationsListView.setItems(FXCollections.observableArrayList(
                        notifications.stream()
                                .map(notification -> formatNotification(notification.getTitre(),
                                        notification.getMessage(),
                                        notification.getDateCreation()))
                                .toList()
                ));
            }

            setLabel(totalNotificationsLabel, String.valueOf(notifications.size()));
            setLabel(notificationsNonLuesLabel, String.valueOf(nonLues));
            runAfterRefresh();
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger les notifications.", e);
        }
    }

    private String formatNotification(String titre, String message, java.time.LocalDateTime dateCreation) {
        String horodatage = DateUtils.formatDateTime(dateCreation);
        String safeTitre = titre != null ? titre : "Notification";
        String safeMessage = message != null ? message : "";
        return horodatage + " | " + safeTitre + " - " + safeMessage;
    }

    private void setLabel(Label label, String value) {
        if (label != null) {
            label.setText(value != null ? value : "");
        }
    }

    private void runAfterRefresh() {
        if (afterRefresh != null) {
            afterRefresh.run();
        }
    }
}
