package yoanemoudilou.cahiertexte.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Utilitaire pour l'affichage des alertes JavaFX.
 * À utiliser une fois que l'application JavaFX est démarrée.
 */
public final class AlertUtils {

    private AlertUtils() {
        // Empêche l'instanciation.
    }

    public static void showInformation(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void showWarning(String title, String header, String content) {
        showAlert(Alert.AlertType.WARNING, title, header, content);
    }

    public static void showError(String title, String header, String content) {
        showAlert(Alert.AlertType.ERROR, title, header, content);
    }

    public static boolean showConfirmation(String title, String header, String content) {
        return runOnFxThreadAndWait(() -> {
            Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, header, content);
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        });
    }

    public static void showException(String title, String header, Throwable throwable) {
        runOnFxThreadAndWait(() -> {
            Alert alert = createAlert(Alert.AlertType.ERROR, title, header,
                    throwable != null ? throwable.getMessage() : "Une erreur est survenue.");

            StringWriter stringWriter = new StringWriter();
            if (throwable != null) {
                throwable.printStackTrace(new PrintWriter(stringWriter));
            }

            TextArea textArea = new TextArea(stringWriter.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);

            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane content = new GridPane();
            content.setMaxWidth(Double.MAX_VALUE);
            content.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(content);
            alert.showAndWait();

            return null;
        });
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String content) {
        runOnFxThreadAndWait(() -> {
            Alert alert = createAlert(type, title, header, content);
            alert.showAndWait();
            return null;
        });
    }

    private static Alert createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title != null ? title : "");
        alert.setHeaderText(header);
        alert.setContentText(content != null ? content : "");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert;
    }

    private static <T> T runOnFxThreadAndWait(Supplier<T> action) {
        if (Platform.isFxApplicationThread()) {
            return action.get();
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<RuntimeException> error = new AtomicReference<>();

        try {
            Platform.runLater(() -> {
                try {
                    result.set(action.get());
                } catch (RuntimeException e) {
                    error.set(e);
                } finally {
                    latch.countDown();
                }
            });

            latch.await();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Le toolkit JavaFX n'est pas initialisé.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompu pendant l'affichage de l'alerte.", e);
        }

        if (error.get() != null) {
            throw error.get();
        }

        return result.get();
    }
}