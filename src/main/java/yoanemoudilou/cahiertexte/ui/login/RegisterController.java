package yoanemoudilou.cahiertexte.ui.login;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import yoanemoudilou.cahiertexte.model.ChefDepartement;
import yoanemoudilou.cahiertexte.model.Enseignant;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

/**
 * ContrÃ´leur de l'Ã©cran d'inscription.
 */
public class RegisterController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        if (roleComboBox != null) {
            roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        }

        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            if (!passwordsMatch()) {
                setMessage("Les mots de passe ne correspondent pas.");
                return;
            }

            authService.inscrire(buildUserFromForm());
            LoginController.showInfoMessageOnNextDisplay(
                    "Inscription envoyÃ©e. Connecte-toi aprÃ¨s validation de ton compte."
            );
            AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");

        } catch (Exception e) {
            setMessage(e.getMessage() != null ? e.getMessage() : "Erreur lors de l'inscription.");
            AlertUtils.showException(
                    "Erreur",
                    "Impossible de finaliser l'inscription.",
                    e
            );
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }

    private boolean passwordsMatch() {
        String password = passwordField != null ? passwordField.getText() : null;
        String confirmation = confirmPasswordField != null ? confirmPasswordField.getText() : null;
        return password != null && password.equals(confirmation);
    }

    private User buildUserFromForm() {
        Role role = roleComboBox != null ? roleComboBox.getValue() : null;

        User user = switch (role) {
            case ENSEIGNANT -> new Enseignant();
            case RESPONSABLE_CLASSE -> new ResponsableClasse();
            case CHEF_DEPARTEMENT -> new ChefDepartement();
            case null -> throw new IllegalArgumentException("Le rÃ´le est requis.");
        };

        user.setNom(nomField != null ? nomField.getText() : null);
        user.setPrenom(prenomField != null ? prenomField.getText() : null);
        user.setEmail(emailField != null ? emailField.getText() : null);
        user.setMotDePasse(passwordField != null ? passwordField.getText() : null);
        user.setRole(role);
        user.setValide(false);
        user.setActif(true);

        return user;
    }

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message != null ? message : "");
        }
    }
}
