package yoanemoudilou.cahiertexte.ui.reports;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.DateUtils;
import yoanemoudilou.cahiertexte.utils.ExcelGenerator;
import yoanemoudilou.cahiertexte.utils.PdfGenerator;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportController {

    @FXML private TableView<Seance> seancesTable;
    @FXML private TableColumn<Seance, Integer> idColumn;
    @FXML private TableColumn<Seance, String> dateColumn;
    @FXML private TableColumn<Seance, String> heureColumn;
    @FXML private TableColumn<Seance, String> coursColumn;
    @FXML private TableColumn<Seance, String> enseignantColumn;
    @FXML private TableColumn<Seance, String> statutColumn;
    @FXML private TableColumn<Seance, Integer> dureeColumn;

    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<StatutSeance> statutComboBox;
    @FXML private Label totalLabel;

    private final SeanceService seanceService = new SeanceService();
    private final CoursService coursService = new CoursService();
    private final UserService userService = new UserService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private final Map<Integer, String> coursParId = new HashMap<>();
    private final Map<Integer, String> enseignantsParId = new HashMap<>();

    @FXML
    private void initialize() {
        configurerTable();
        chargerFiltres();
        chargerReferences();
        chargerPreview();
    }

    @FXML
    private void handleFiltrer() {
        try {
            LocalDate debut = dateDebutPicker != null ? dateDebutPicker.getValue() : null;
            LocalDate fin = dateFinPicker != null ? dateFinPicker.getValue() : null;

            if (debut != null && fin != null && debut.isAfter(fin)) {
                AlertUtils.showWarning("Période invalide", null,
                        "La date de début doit être antérieure ou égale à la date de fin.");
                return;
            }

            chargerPreview();
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'appliquer les filtres.", e);
        }
    }

    @FXML
    private void handleResetFiltres() {
        if (dateDebutPicker != null) {
            dateDebutPicker.setValue(null);
        }
        if (dateFinPicker != null) {
            dateFinPicker.setValue(null);
        }
        if (statutComboBox != null) {
            statutComboBox.setValue(null);
        }
        chargerPreview();
    }

    @FXML
    private void handleExporterPdf() {
        exporter(true);
    }

    @FXML
    private void handleExporterExcel() {
        exporter(false);
    }

    private void configurerTable() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(DateUtils.formatDate(data.getValue().getDateSeance())));
        }
        if (heureColumn != null) {
            heureColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(DateUtils.formatTime(data.getValue().getHeureSeance())));
        }
        if (coursColumn != null) {
            coursColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(coursParId.getOrDefault(data.getValue().getCoursId(), "")));
        }
        if (enseignantColumn != null) {
            enseignantColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(enseignantsParId.getOrDefault(data.getValue().getEnseignantId(), "")));
        }
        if (statutColumn != null) {
            statutColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(data.getValue().getStatut() != null ? data.getValue().getStatut().name() : ""));
        }
        if (dureeColumn != null) {
            dureeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getDuree()));
        }
    }

    private void chargerFiltres() {
        if (statutComboBox != null) {
            statutComboBox.setItems(FXCollections.observableArrayList(StatutSeance.values()));
        }
    }

    private void chargerReferences() {
        coursParId.clear();
        enseignantsParId.clear();

        for (Cours cours : coursService.getAllCours()) {
            if (cours.getId() != null) {
                coursParId.put(cours.getId(), cours.getCode() + " - " + cours.getIntitule());
            }
        }

        for (User user : userService.getUtilisateursByRole(Role.ENSEIGNANT)) {
            if (user.getId() != null) {
                enseignantsParId.put(user.getId(), user.getNomComplet());
            }
        }
    }

    private void chargerPreview() {
        List<Seance> seances = getSeancesFiltrees();

        if (seancesTable != null) {
            seancesTable.setItems(FXCollections.observableArrayList(seances));
        }
        if (totalLabel != null) {
            totalLabel.setText("Total : " + seances.size());
        }
    }

    private List<Seance> getSeancesFiltrees() {
        User currentUser = sessionManager.getUtilisateurConnecte();

        List<Seance> base = (currentUser != null
                && currentUser.getRole() == Role.ENSEIGNANT
                && currentUser.getId() != null)
                ? seanceService.getSeancesByEnseignantId(currentUser.getId())
                : seanceService.getAllSeances();

        LocalDate debut = dateDebutPicker != null ? dateDebutPicker.getValue() : null;
        LocalDate fin = dateFinPicker != null ? dateFinPicker.getValue() : null;
        StatutSeance statut = statutComboBox != null ? statutComboBox.getValue() : null;

        return base.stream()
                .filter(s -> debut == null || (s.getDateSeance() != null && !s.getDateSeance().isBefore(debut)))
                .filter(s -> fin == null || (s.getDateSeance() != null && !s.getDateSeance().isAfter(fin)))
                .filter(s -> statut == null || s.getStatut() == statut)
                .toList();
    }

    private void exporter(boolean pdf) {
        try {
            List<Seance> seances = getSeancesFiltrees();

            if (seances.isEmpty()) {
                AlertUtils.showWarning("Export impossible", null, "Aucune séance à exporter.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle(pdf ? "Exporter en PDF" : "Exporter en Excel");
            chooser.setInitialFileName(pdf ? "rapport-seances.pdf" : "rapport-seances.xlsx");
            chooser.getExtensionFilters().add(
                    pdf
                            ? new FileChooser.ExtensionFilter("PDF", "*.pdf")
                            : new FileChooser.ExtensionFilter("Excel", "*.xlsx")
            );

            Window window = seancesTable != null && seancesTable.getScene() != null
                    ? seancesTable.getScene().getWindow()
                    : null;

            File file = pdf ? chooser.showSaveDialog(window) : chooser.showSaveDialog(window);

            if (file == null) {
                return;
            }

            String titre = "Rapport des séances";

            if (pdf) {
                PdfGenerator.genererFicheSeances(file.getAbsolutePath(), titre, seances, coursParId, enseignantsParId);
            } else {
                ExcelGenerator.genererFicheSeances(file.getAbsolutePath(), titre, seances, coursParId, enseignantsParId);
            }

            AlertUtils.showInformation("Succès", "Export terminé",
                    "Le rapport a été généré avec succès.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'exporter le rapport.", e);
        }
    }
}