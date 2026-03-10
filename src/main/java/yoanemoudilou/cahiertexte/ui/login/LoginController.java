package yoanemoudilou.cahiertexte.ui.login;

import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

/**
 * Contrôleur de l'écran de connexion.
 */
public class LoginController {

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
            messageLabel.setText("");
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


            AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Tableau de bord");

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
    private void handleQuitter(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void ouvrirVue(ActionEvent event, String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (Exception e) {
            AlertUtils.showException(
                    "Navigation impossible",
                    "Impossible de charger la vue : " + fxmlPath,
                    e
            );
        }
    }

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message != null ? message : "");
        }
    }
}