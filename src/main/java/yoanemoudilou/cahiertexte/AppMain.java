package yoanemoudilou.cahiertexte;

import yoanemoudilou.cahiertexte.utils.AppNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppNavigator.init(primaryStage);
        AppNavigator.goToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
