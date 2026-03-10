package yoanemoudilou.cahiertexte.utils;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public final class AppNavigator {

    private static final String APP_CSS = "/yoanemoudilou/cahiertexte/css/style.css";
    private static Stage primaryStage;

    private AppNavigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);
    }

    public static void goToLogin() {
        navigate("/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }

    public static void goToDashboardForCurrentUser() {
        User user = SessionManager.getInstance().getUtilisateurConnecte();

        if (user == null || user.getRole() == null) {
            goToLogin();
            return;
        }

        if (user.getRole() == Role.CHEF_DEPARTEMENT) {
            navigate("/yoanemoudilou/cahiertexte/view/dashboard/admin.fxml", "Dashboard Admin");
        } else if (user.getRole() == Role.ENSEIGNANT) {
            navigate("/yoanemoudilou/cahiertexte/view/dashboard/enseignant.fxml", "Dashboard Enseignant");
        } else if (user.getRole() == Role.RESPONSABLE_CLASSE) {
            navigate("/yoanemoudilou/cahiertexte/view/dashboard/responsable.fxml", "Dashboard Responsable");
        }
    }

    public static void navigate(String fxmlPath, String title) {
        ensurePrimaryStage();
        Scene scene = loadScene(fxmlPath);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void navigate(ActionEvent event, String fxmlPath, String title) {
        Stage stage = extractStage(event);
        Scene scene = loadScene(fxmlPath);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        ensurePrimaryStage();
        return primaryStage;
    }

    private static Scene loadScene(String fxmlPath) {
        try {
            URL url = AppNavigator.class.getResource(fxmlPath);
            if (url == null) {
                throw new IllegalArgumentException("FXML introuvable : " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            URL cssUrl = AppNavigator.class.getResource(APP_CSS);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            return scene;

        } catch (Exception e) {
            throw new RuntimeException("Erreur de navigation vers : " + fxmlPath, e);
        }
    }

    private static Stage extractStage(ActionEvent event) {
        if (event != null && event.getSource() instanceof Node node) {
            return (Stage) node.getScene().getWindow();
        }
        return getPrimaryStage();
    }

    private static void ensurePrimaryStage() {
        if (primaryStage == null) {
            throw new IllegalStateException("AppNavigator non initialisé.");
        }
    }
}
