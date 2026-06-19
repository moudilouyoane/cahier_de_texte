package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.NotificationService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.utils.DateUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur du dashboard enseignant.
 */
public class EnseignantDashboardController {

    @FXML
    private Label bienvenuLabel;

    @FXML
    private Label mesCoursCountLabel;

    @FXML
    private Label mesSeancesCountLabel;

    @FXML
    private Label seancesEnAttenteCountLabel;

    @FXML
    private Label seancesValideesCountLabel;

    @FXML
    private Label seancesRejeteesCountLabel;

    @FXML
    private Label mesCoursKpiLabel;

    @FXML
    private Label mesSeancesKpiLabel;

    @FXML
    private Label seancesEnAttenteKpiLabel;

    @FXML
    private Label seancesValideesKpiLabel;

    @FXML
    private Label seancesRejeteesKpiLabel;

    @FXML
    private Label activiteTotalHeaderLabel;

    @FXML
    private Label activiteValideesLabel;

    @FXML
    private Label activiteEnAttenteLabel;

    @FXML
    private Label activiteRejeteesLabel;

    @FXML
    private Label activiteTotalLabel;

    @FXML
    private Region activiteValideesBar;

    @FXML
    private Region activiteEnAttenteBar;

    @FXML
    private Region activiteRejeteesBar;

    @FXML
    private Region activiteTotalBar;

    @FXML
    private TableView<Cours> mesCoursTable;

    @FXML
    private TableColumn<Cours, String> coursCodeColumn;

    @FXML
    private TableColumn<Cours, String> coursIntituleColumn;

    @FXML
    private TableColumn<Cours, String> coursClasseColumn;

    @FXML
    private TableColumn<Cours, Integer> coursVolumeColumn;
    @FXML
    private TableColumn<Cours, String> coursProgressColumn;

    @FXML
    private TableView<Seance> dernieresSeancesTable;

    @FXML
    private TableColumn<Seance, String> seanceDateColumn;

    @FXML
    private TableColumn<Seance, String> seanceHeureColumn;

    @FXML
    private TableColumn<Seance, String> seanceCoursColumn;

    @FXML
    private TableColumn<Seance, String> seanceStatutColumn;

    @FXML
    private TableColumn<Seance, String> seanceContenuColumn;

    @FXML
    private TableColumn<Seance, String> seanceObservationsColumn;

    @FXML
    private Label notificationsCountLabel;

    @FXML
    private ListView<String> notificationsListView;

    @FXML
    private VBox notificationsPanel;

    @FXML
    private StackPane contentContainer;

    @FXML
    private ScrollPane dashboardContent;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final CoursService coursService = new CoursService();
    private final ClasseService classeService = new ClasseService();
    private final NotificationService notificationService = new NotificationService();
    private final SeanceService seanceService = new SeanceService();
    private final Map<Integer, String> coursLabels = new HashMap<>();
    private final Map<Integer, String> classeLabels = new HashMap<>();
    private List<Cours> coursAffiches = List.of();
    private TableView<Cours> coursViewTable;

    @FXML
    private void initialize() {
        configurerTables();
        chargerInfos();
        afficherDashboard();
    }

    @FXML
    private void handleNouvelleSeance(ActionEvent event) {
        afficherModule("/yoanemoudilou/cahiertexte/view/seance.fxml");
    }

    @FXML
    private void handleVoirMesSeances(ActionEvent event) {
        afficherModule("/yoanemoudilou/cahiertexte/view/seance.fxml");
    }

    @FXML
    private void handleVoirMesCours(ActionEvent event) {
        chargerInfos();
        afficherDansContenu(creerVueCours());
    }

    @FXML
    private void handleRafraichir() {
        chargerInfos();
        afficherDashboard();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }

    @FXML
    private void handleToggleNotifications() {
        if (notificationsPanel == null) {
            return;
        }

        if (contentContainer != null && !contentContainer.getChildren().contains(notificationsPanel)) {
            contentContainer.getChildren().add(notificationsPanel);
        }
        boolean show = !notificationsPanel.isVisible();
        notificationsPanel.setVisible(show);
        notificationsPanel.setManaged(show);
    }

    private void configurerTables() {
        if (coursCodeColumn != null) {
            coursCodeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));
        }
        if (coursIntituleColumn != null) {
            coursIntituleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIntitule()));
        }
        if (coursClasseColumn != null) {
            coursClasseColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    classeLabels.getOrDefault(data.getValue().getClasseId(), "Classe #" + data.getValue().getClasseId()))
            );
        }
        if (coursVolumeColumn != null) {
            coursVolumeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getVolumeHoraire()));
        }
        if (coursProgressColumn != null) {
            coursProgressColumn.setCellValueFactory(data -> {
                return new ReadOnlyStringWrapper(formatProgressionCours(data.getValue()));
            });
        }
        if (seanceDateColumn != null) {
            seanceDateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatDate(data.getValue().getDateSeance()))
            );
        }
        if (seanceHeureColumn != null) {
            seanceHeureColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatTime(data.getValue().getHeureSeance()))
            );
        }
        if (seanceCoursColumn != null) {
            seanceCoursColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    coursLabels.getOrDefault(data.getValue().getCoursId(), "Cours #" + data.getValue().getCoursId()))
            );
        }
        if (seanceStatutColumn != null) {
            seanceStatutColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getStatut() != null ? data.getValue().getStatut().name() : "")
            );
        }
        if (seanceContenuColumn != null) {
            seanceContenuColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContenu()));
        }
        if (seanceObservationsColumn != null) {
            seanceObservationsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getObservations()));
        }
    }

    private void chargerInfos() {
        try {
            User currentUser = sessionManager.getUtilisateurConnecte();

            if (currentUser == null || currentUser.getId() == null) {
                return;
            }

            List<Cours> mesCours = coursService.getCoursByEnseignantId(currentUser.getId());
            List<Seance> mesSeances = seanceService.getSeancesByEnseignantId(currentUser.getId());
            coursAffiches = mesCours;

            classeLabels.clear();
            for (Classe classe : classeService.getAllClasses()) {
                if (classe.getId() != null) {
                    classeLabels.put(classe.getId(), classe.getNomClasse() + " - " + classe.getNiveau());
                }
            }

            coursLabels.clear();
            for (Cours cours : mesCours) {
                if (cours.getId() != null) {
                    coursLabels.put(cours.getId(), cours.getCode() + " - " + cours.getIntitule());
                }
            }

            setLabel(bienvenuLabel, "Bienvenue, " + currentUser.getNomComplet());
            setLabel(mesCoursCountLabel, String.valueOf(mesCours.size()));
            setLabel(mesSeancesCountLabel, String.valueOf(mesSeances.size()));
            long seancesEnAttente = mesSeances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count();
            long seancesValidees = mesSeances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
            long seancesRejetees = mesSeances.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count();

            setCompteursSeances(mesCours.size(), mesSeances.size(), seancesEnAttente, seancesValidees, seancesRejetees);

            if (mesCoursTable != null) {
                mesCoursTable.setItems(FXCollections.observableArrayList(mesCours));
            }
            if (coursViewTable != null) {
                coursViewTable.setItems(FXCollections.observableArrayList(mesCours));
            }

            if (dernieresSeancesTable != null) {
                List<Seance> recentes = mesSeances.stream()
                        .sorted(Comparator
                                .comparing(Seance::getDateSeance, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Seance::getHeureSeance, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(4)
                        .toList();
                dernieresSeancesTable.setItems(FXCollections.observableArrayList(recentes));
            }

            chargerNotifications(currentUser);
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard enseignant.", e);
        }
    }

    private void chargerNotifications(User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        var notifications = notificationService.getNotificationsPourUtilisateur(currentUser.getId(), 6);
        if (notificationsListView != null) {
            notificationsListView.setItems(FXCollections.observableArrayList(
                    notifications.stream()
                            .map(n -> n.getTitre() + " - " + n.getMessage())
                            .toList()
            ));
        }
        setLabel(notificationsCountLabel, String.valueOf(notificationService.countNotificationsNonLues(currentUser.getId())));
    }

    @FXML
    private void handleMarquerToutCommeLu(ActionEvent event) {
        User currentUser = sessionManager.getUtilisateurConnecte();
        if (currentUser != null && currentUser.getId() != null) {
            notificationService.marquerToutesCommeLues(currentUser.getId());
            chargerNotifications(currentUser);
            setLabel(notificationsCountLabel, String.valueOf(notificationService.countNotificationsNonLues(currentUser.getId())));
        }
    }

    private void afficherDashboard() {
        if (contentContainer != null && dashboardContent != null) {
            afficherDansContenu(dashboardContent);
        }
    }

    private void afficherModule(String fxmlPath) {
        if (contentContainer == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            masquerBoutonsRetour(root);
            afficherDansContenu(root);
            chargerInfos();
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le module.", e);
        }
    }

    private Parent creerVueCours() {
        VBox page = new VBox(16);
        page.getStyleClass().add("page-content");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Mes cours");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size:20px;");
        Label subtitle = new Label("Cours assignes et progression des volumes horaires");
        subtitle.getStyleClass().add("section-subtitle");
        titleBox.getChildren().setAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button rafraichirButton = new Button("Rafraichir");
        rafraichirButton.getStyleClass().add("button-soft");
        rafraichirButton.setOnAction(event -> {
            chargerInfos();
            if (coursViewTable != null) {
                coursViewTable.setItems(FXCollections.observableArrayList(coursAffiches));
            }
        });

        Button dashboardButton = new Button("Tableau de bord");
        dashboardButton.getStyleClass().add("button");
        dashboardButton.setOnAction(event -> afficherDashboard());
        header.getChildren().setAll(titleBox, spacer, rafraichirButton, dashboardButton);

        HBox stats = new HBox(12);
        stats.getChildren().setAll(
                creerStatCours("Cours assignes", String.valueOf(coursAffiches.size())),
                creerStatCours("Volume total", calculerVolumeTotalCours() + " h"),
                creerStatCours("Progression moyenne", calculerProgressionMoyenne() + "%")
        );

        coursViewTable = new TableView<>();
        coursViewTable.setPrefHeight(520);
        coursViewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        coursViewTable.setStyle("-fx-background-color:transparent; -fx-border-color:#e8eef6; -fx-border-radius:10; -fx-background-radius:10;");

        TableColumn<Cours, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));

        TableColumn<Cours, String> intituleColumn = new TableColumn<>("Intitule");
        intituleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIntitule()));

        TableColumn<Cours, String> classeColumn = new TableColumn<>("Classe");
        classeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                classeLabels.getOrDefault(data.getValue().getClasseId(), "Classe #" + data.getValue().getClasseId()))
        );

        TableColumn<Cours, Integer> volumeColumn = new TableColumn<>("Volume");
        volumeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getVolumeHoraire()));

        TableColumn<Cours, String> progressionColumn = new TableColumn<>("Progression");
        progressionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatProgressionCours(data.getValue())));

        coursViewTable.getColumns().clear();
        coursViewTable.getColumns().add(codeColumn);
        coursViewTable.getColumns().add(intituleColumn);
        coursViewTable.getColumns().add(classeColumn);
        coursViewTable.getColumns().add(volumeColumn);
        coursViewTable.getColumns().add(progressionColumn);
        coursViewTable.setItems(FXCollections.observableArrayList(coursAffiches));

        VBox tableCard = new VBox(12);
        tableCard.getStyleClass().add("surface-card");
        tableCard.getChildren().setAll(coursViewTable);
        VBox.setVgrow(coursViewTable, javafx.scene.layout.Priority.ALWAYS);

        page.getChildren().setAll(header, stats, tableCard);

        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scroll");
        return scrollPane;
    }

    private VBox creerStatCours(String titre, String valeur) {
        VBox stat = new VBox(4);
        stat.getStyleClass().add("summary-stat");
        stat.setPadding(new Insets(14));
        HBox.setHgrow(stat, javafx.scene.layout.Priority.ALWAYS);

        Label valueLabel = new Label(valeur);
        valueLabel.getStyleClass().add("metric-value");
        valueLabel.setStyle("-fx-font-size:24px;");
        Label titleLabel = new Label(titre);
        titleLabel.getStyleClass().add("metric-caption");
        stat.getChildren().setAll(valueLabel, titleLabel);
        return stat;
    }

    private void setCompteursSeances(int totalCours, int totalSeances, long enAttente, long validees, long rejetees) {
        String totalCoursText = String.valueOf(totalCours);
        String totalSeancesText = String.valueOf(totalSeances);
        String enAttenteText = String.valueOf(enAttente);
        String valideesText = String.valueOf(validees);
        String rejeteesText = String.valueOf(rejetees);

        setLabel(mesCoursKpiLabel, totalCoursText);
        setLabel(mesSeancesKpiLabel, totalSeancesText);
        setLabel(seancesEnAttenteKpiLabel, enAttenteText);
        setLabel(seancesValideesKpiLabel, valideesText);
        setLabel(seancesRejeteesKpiLabel, rejeteesText);

        setLabel(mesSeancesCountLabel, totalSeancesText);
        setLabel(seancesEnAttenteCountLabel, enAttenteText);
        setLabel(seancesValideesCountLabel, valideesText);
        setLabel(seancesRejeteesCountLabel, rejeteesText);

        setLabel(activiteTotalHeaderLabel, totalSeancesText);
        setLabel(activiteValideesLabel, valideesText);
        setLabel(activiteEnAttenteLabel, enAttenteText);
        setLabel(activiteRejeteesLabel, rejeteesText);
        setLabel(activiteTotalLabel, totalSeancesText);

        ajusterBarre(activiteValideesBar, ratio(validees, totalSeances));
        ajusterBarre(activiteEnAttenteBar, ratio(enAttente, totalSeances));
        ajusterBarre(activiteRejeteesBar, ratio(rejetees, totalSeances));
        ajusterBarre(activiteTotalBar, totalSeances > 0 ? 1 : 0);
    }

    private double ratio(long value, long total) {
        if (value <= 0 || total <= 0) {
            return 0;
        }
        return Math.max(0.06, Math.min(1, (double) value / total));
    }

    private void ajusterBarre(Region bar, double ratio) {
        if (bar == null) {
            return;
        }

        bar.setMinWidth(0);
        bar.setMaxWidth(Region.USE_PREF_SIZE);
        bar.prefWidthProperty().unbind();
        if (bar.getParent() instanceof Region track) {
            bar.prefWidthProperty().bind(track.widthProperty().multiply(ratio));
        } else {
            bar.setPrefWidth(0);
        }
    }

    private int calculerVolumeTotalCours() {
        return coursAffiches.stream()
                .mapToInt(cours -> cours.getVolumeHoraire() != null ? cours.getVolumeHoraire() : 0)
                .sum();
    }

    private int calculerProgressionMoyenne() {
        if (coursAffiches.isEmpty()) {
            return 0;
        }

        return (int) Math.round(coursAffiches.stream()
                .mapToInt(this::calculerProgressionCours)
                .average()
                .orElse(0));
    }

    private String formatProgressionCours(Cours cours) {
        return calculerProgressionCours(cours) + "%";
    }

    private int calculerProgressionCours(Cours cours) {
        if (cours == null || cours.getId() == null) {
            return 0;
        }

        int volume = cours.getVolumeHoraire() != null ? cours.getVolumeHoraire() : 0;
        int minutes = seanceService.getSeancesByCoursId(cours.getId())
                .stream()
                .mapToInt(s -> s.getDuree() != null ? s.getDuree() : 0)
                .sum();
        int heures = Math.round(minutes / 60.0f);
        return volume > 0 ? (int) Math.round((double) heures / volume * 100) : 0;
    }

    private void afficherDansContenu(Parent root) {
        if (contentContainer == null || root == null) {
            return;
        }

        if (notificationsPanel != null) {
            notificationsPanel.setVisible(false);
            notificationsPanel.setManaged(false);
            contentContainer.getChildren().setAll(root, notificationsPanel);
        } else {
            contentContainer.getChildren().setAll(root);
        }
    }

    private void masquerBoutonsRetour(Parent root) {
        if (root == null) {
            return;
        }

        if (root instanceof javafx.scene.layout.BorderPane borderPane) {
            borderPane.setLeft(null);
        }

        for (var node : root.lookupAll(".module-back-button")) {
            node.setManaged(false);
            node.setVisible(false);
        }

        for (var node : root.lookupAll(".top-bar")) {
            if (node instanceof javafx.scene.layout.Pane pane) {
                for (var child : pane.getChildren()) {
                    if (child instanceof javafx.scene.control.Button button
                            && (button.getStyleClass().contains("module-back-button")
                            || "Dashboard".equals(button.getText())
                            || (button.getText() != null && button.getText().contains("Dashboard")))) {
                        button.setManaged(false);
                        button.setVisible(false);
                    }
                }
            }
        }
    }

    private void setLabel(Label label, String value) {
        if (label != null) {
            label.setText(value != null ? value : "");
        }
    }
}
