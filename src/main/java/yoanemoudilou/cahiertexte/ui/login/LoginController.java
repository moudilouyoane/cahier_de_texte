package yoanemoudilou.cahiertexte.ui.login;

import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

/**
 * Contrôleur de l'écran de connexion.
 */
public class LoginController {

    private static String pendingInfoMessage;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText(consumePendingInfoMessage());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            String email = emailField != null ? emailField.getText() : null;
            String motDePasse = passwordField != null ? passwordField.getText() : null;

            boolean success = authService.login(email, motDePasse);

            if (!success) {
                setMessage("Email ou mot de passe invalide, ou compte non actif/non validé.");
                return;
            }

            AppNavigator.goToDashboardForCurrentUser();

        } catch (Exception e) {
            setMessage("Erreur lors de la connexion.");
            AlertUtils.showException(
                    "Erreur",
                    "Une erreur est survenue pendant la connexion",
                    e
            );
        }
    }

    @FXML
    private void handleOpenRegistration(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/register.fxml", "Inscription");
    }

    @FXML
    private void handleQuitter() {
        AppNavigator.getPrimaryStage().close();
    }

    public static void showInfoMessageOnNextDisplay(String message) {
        pendingInfoMessage = message;
    }

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message != null ? message : "");
        }
    }

    private String consumePendingInfoMessage() {
        String message = pendingInfoMessage;
        pendingInfoMessage = null;
        return message != null ? message : "";
    }
}
