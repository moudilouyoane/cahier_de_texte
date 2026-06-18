package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ResponsableProgressionController implements Initializable {

    @FXML private FlowPane cardsContainer;
    private final CoursService coursService   = new CoursService();
    private final SeanceService seanceService = new SeanceService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    // ── Initialisation ────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerProgression();
    }

    // ── Chargement des données ─────────────────────────────────────
    private void chargerProgression() {
        cardsContainer.getChildren().clear();

        // Si l'utilisateur est un responsable de classe, ne charger que les cours
        // liés à sa classe. Sinon charger tous les cours (cas admin/dev).
        var utilisateur = sessionManager.getUtilisateurConnecte();
        List<Cours> cours;
        if (utilisateur instanceof ResponsableClasse responsable && responsable.getClasseId() != null) {
            cours = coursService.getCoursByClasseId(responsable.getClasseId());
        } else {
            cours = coursService.getAllCours();
        }
        if (cours == null || cours.isEmpty()) {
            Label vide = new Label("Aucun cours disponible.");
            vide.getStyleClass().add("page-subtitle");
            cardsContainer.getChildren().add(vide);
            return;
        }

        for (Cours c : cours) {
            cardsContainer.getChildren().add(buildCard(c));
        }
    }

    // ── Construction d'une carte de progression ────────────────────
    private VBox buildCard(Cours cours) {

        int volumeTotal  = cours.getVolumeHoraire();              // ex. 30 h
        int heuresRealisMinutes = seanceService.getSeancesByCoursId(cours.getId())
                .stream()
                .mapToInt(s -> s.getDuree() != null ? s.getDuree() : 0)
                .sum();
        int heuresRealis = Math.round(heuresRealisMinutes / 60.0f);  // Convertir minutes en heures

        // Clamp pour ne pas dépasser 100 %
        int heuresSafe = Math.min(heuresRealis, volumeTotal);
        double ratio   = (volumeTotal > 0) ? (double) heuresSafe / volumeTotal : 0.0;
        int pct        = (int) Math.round(ratio * 100);

        // ── Conteneur principal (surface-card) ─────────────────────
        VBox card = new VBox(12);
        card.getStyleClass().add("surface-card");
        card.setPrefWidth(320);

        // ── En-tête : icône + nom du cours ─────────────────────────
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        // Icône colorée
        Label icon = new Label("📚");
        icon.getStyleClass().addAll("card-icon-box", "card-icon-box-blue");
        icon.setStyle("-fx-font-size:18px;");

        VBox titreBox = new VBox(2);
        Label nomCours = new Label(cours.getIntitule());
        nomCours.getStyleClass().add("section-title");
        nomCours.setWrapText(true);

        Label matiere = new Label(cours.getCode() != null ? cours.getCode() : "");
        matiere.getStyleClass().add("metric-caption");

        titreBox.getChildren().addAll(nomCours, matiere);
        HBox.setHgrow(titreBox, Priority.ALWAYS);
        header.getChildren().addAll(icon, titreBox);

        // ── Séparateur léger ────────────────────────────────────────
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color:#eaf0f7;");

        // ── Ligne volume horaire ────────────────────────────────────
        HBox volumeLine = buildInfoRow("🕐 Volume horaire", volumeTotal + " h");
        HBox realisLine = buildInfoRow("✅ Heures réalisées", heuresRealis + " h");

        // ── Barre de progression ────────────────────────────────────
        VBox progressSection = buildProgressBar(ratio, pct);

        // ── Badge état ──────────────────────────────────────────────
        Label badge = buildBadge(pct);

        // ── Assemblage ──────────────────────────────────────────────
        card.getChildren().addAll(
                header,
                sep,
                volumeLine,
                realisLine,
                progressSection,
                badge
        );

        return card;
    }

    // ── Ligne info (label + valeur) ────────────────────────────────
    private HBox buildInfoRow(String libelle, String valeur) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(libelle);
        lbl.getStyleClass().add("section-subtitle");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label val = new Label(valeur);
        val.getStyleClass().add("section-title");
        val.setStyle("-fx-font-size:13px;");

        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    // ── Barre de progression custom ────────────────────────────────
    private VBox buildProgressBar(double ratio, int pct) {
        VBox box = new VBox(6);

        // Étiquette pourcentage
        HBox pctRow = new HBox();
        pctRow.setAlignment(Pos.CENTER_LEFT);

        Label pctLabel = new Label(pct + "%");
        pctLabel.setStyle("-fx-font-size:11px; -fx-font-weight:700;");
        // Couleur selon avancement
        if (pct >= 100) {
            pctLabel.getStyleClass().add("metric-value-success");
            pctLabel.setStyle("-fx-font-size:11px; -fx-font-weight:700;");
        } else if (pct >= 60) {
            pctLabel.getStyleClass().add("trend-up");
        } else if (pct >= 30) {
            pctLabel.getStyleClass().add("trend-alert");
        } else {
            pctLabel.getStyleClass().add("trend-alert");
        }

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        pctRow.getChildren().addAll(pctLabel, sp);

        // Track (fond)
        StackPane trackStack = new StackPane();

        Rectangle track = new Rectangle();
        track.setHeight(10);
        track.setArcWidth(999);
        track.setArcHeight(999);
        track.setFill(Color.web("#edf2f8"));
        track.widthProperty().bind(trackStack.widthProperty());

        // Fill (avancement)
        Rectangle fill = new Rectangle();
        fill.setHeight(10);
        fill.setArcWidth(999);
        fill.setArcHeight(999);
        fill.widthProperty().bind(
                trackStack.widthProperty().multiply(Math.min(ratio, 1.0))
        );

        // Couleur du fill selon avancement
        if (pct >= 100) {
            fill.setFill(Color.web("#198754"));
        } else if (pct >= 60) {
            fill.setFill(Color.web("#2f6fad"));
        } else if (pct >= 30) {
            fill.setFill(Color.web("#cb8616"));
        } else {
            fill.setFill(Color.web("#c0392b"));
        }

        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        trackStack.getChildren().addAll(track, fill);

        box.getChildren().addAll(pctRow, trackStack);
        return box;
    }

    // ── Badge d'état ────────────────────────────────────────────────
    private Label buildBadge(int pct) {
        Label badge = new Label();
        badge.getStyleClass().add("info-chip");

        if (pct >= 100) {
            badge.setText("✅ Terminé");
            badge.setStyle(
                    "-fx-background-color:#e6f7ee; -fx-text-fill:#198754;"
                            + "-fx-background-radius:999; -fx-padding:4 12 4 12;"
                            + "-fx-font-size:11px; -fx-font-weight:700;");
        } else if (pct >= 60) {
            badge.setText("📘 En bonne voie");
            badge.setStyle(
                    "-fx-background-color:#e9f1ff; -fx-text-fill:#2f6fad;"
                            + "-fx-background-radius:999; -fx-padding:4 12 4 12;"
                            + "-fx-font-size:11px; -fx-font-weight:700;");
        } else if (pct >= 30) {
            badge.setText("⚠️ En cours");
            badge.setStyle(
                    "-fx-background-color:#fff4dc; -fx-text-fill:#cb8616;"
                            + "-fx-background-radius:999; -fx-padding:4 12 4 12;"
                            + "-fx-font-size:11px; -fx-font-weight:700;");
        } else {
            badge.setText("🔴 Peu avancé");
            badge.setStyle(
                    "-fx-background-color:#ffeaea; -fx-text-fill:#c0392b;"
                            + "-fx-background-radius:999; -fx-padding:4 12 4 12;"
                            + "-fx-font-size:11px; -fx-font-weight:700;");
        }

        return badge;
    }

    // ── Navigation retour ──────────────────────────────────────────
    @FXML
    private void handleRetourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/yoanemoudilou/cahiertexte/ui/dashboard/ResponsableDashboard.fxml"
                    )
            );
            Parent root  = loader.load();
            Stage  stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOuvrirValidation(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/validation.fxml", "Validation de seances");
    }

    @FXML
    private void handleOuvrirCahierTexte(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/cahier.fxml", "Cahier de texte");
    }

    @FXML
    private void handleToggleNotifications(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/notifications.fxml", "Notifications");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }
}