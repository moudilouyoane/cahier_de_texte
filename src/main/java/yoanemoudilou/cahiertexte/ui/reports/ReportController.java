package yoanemoudilou.cahiertexte.ui.reports;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Filiere;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.FiliereService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.utils.DateUtils;
import yoanemoudilou.cahiertexte.utils.ExcelGenerator;
import yoanemoudilou.cahiertexte.utils.PdfGenerator;

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
    @FXML private ComboBox<ExportTarget> exportTypeComboBox;
    @FXML private Label totalLabel;

    private final SeanceService seanceService = new SeanceService();
    private final CoursService coursService = new CoursService();
    private final ClasseService classeService = new ClasseService();
    private final FiliereService filiereService = new FiliereService();
    private final UserService userService = new UserService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private final Map<Integer, String> coursParId = new HashMap<>();
    private final Map<Integer, String> enseignantsParId = new HashMap<>();
    private final Map<Integer, Classe> classesParId = new HashMap<>();

    @FXML
    private void initialize() {
        configurerTable();
        chargerFiltres();
        chargerTypesExport();
        chargerReferences();
        chargerPreview();
    }

    @FXML
    private void handleFiltrer() {
        try {
            LocalDate debut = dateDebutPicker != null ? dateDebutPicker.getValue() : null;
            LocalDate fin = dateFinPicker != null ? dateFinPicker.getValue() : null;

            if (debut != null && fin != null && debut.isAfter(fin)) {
                AlertUtils.showWarning("Periode invalide", null,
                        "La date de debut doit etre anterieure ou egale a la date de fin.");
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

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        AppNavigator.goToDashboardForCurrentUser();
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

    private void chargerTypesExport() {
        if (exportTypeComboBox != null) {
            exportTypeComboBox.setItems(FXCollections.observableArrayList(ExportTarget.values()));
            exportTypeComboBox.setValue(ExportTarget.FILIERES);
        }
    }

    private void chargerReferences() {
        coursParId.clear();
        enseignantsParId.clear();
        classesParId.clear();

        for (Classe classe : classeService.getAllClasses()) {
            if (classe.getId() != null) {
                classesParId.put(classe.getId(), classe);
            }
        }

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
            ExportTarget target = exportTypeComboBox != null ? exportTypeComboBox.getValue() : ExportTarget.FILIERES;
            if (target == null) {
                AlertUtils.showWarning("Export impossible", null, "Choisis une liste a exporter.");
                return;
            }

            chargerReferences();

            if (!aDesDonneesAExporter(target)) {
                AlertUtils.showWarning("Export impossible", null,
                        "Aucune donnee a exporter pour : " + target.getLabel() + ".");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle((pdf ? "Exporter en PDF : " : "Exporter en Excel : ") + target.getLabel());
            chooser.setInitialFileName(target.getFileNameBase() + (pdf ? ".pdf" : ".xlsx"));
            chooser.getExtensionFilters().add(
                    pdf
                            ? new FileChooser.ExtensionFilter("PDF", "*.pdf")
                            : new FileChooser.ExtensionFilter("Excel", "*.xlsx")
            );

            Window window = seancesTable != null && seancesTable.getScene() != null
                    ? seancesTable.getScene().getWindow()
                    : null;

            File file = chooser.showSaveDialog(window);

            if (file == null) {
                return;
            }

            file = ensureExtension(file, pdf ? ".pdf" : ".xlsx");
            genererExport(file, pdf, target);

            AlertUtils.showInformation("Succes", "Export termine",
                    "Le fichier a ete genere avec succes.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'exporter le rapport.", e);
        }
    }

    private boolean aDesDonneesAExporter(ExportTarget target) {
        return switch (target) {
            case FILIERES -> !filiereService.getAllFilieres().isEmpty();
            case COURS -> !coursService.getAllCours().isEmpty();
            case CLASSES -> !classeService.getAllClasses().isEmpty();
            case ENSEIGNANTS -> !userService.getUtilisateursByRole(Role.ENSEIGNANT).isEmpty();
            case SEANCES -> !getSeancesFiltrees().isEmpty();
        };
    }

    private void genererExport(File file, boolean pdf, ExportTarget target) throws Exception {
        String filePath = file.getAbsolutePath();

        switch (target) {
            case FILIERES -> {
                List<Filiere> filieres = filiereService.getAllFilieres();
                if (pdf) {
                    PdfGenerator.genererListeFilieres(filePath, target.getTitle(), filieres);
                } else {
                    ExcelGenerator.genererListeFilieres(filePath, target.getTitle(), filieres);
                }
            }
            case COURS -> {
                List<Cours> cours = coursService.getAllCours();
                if (pdf) {
                    PdfGenerator.genererListeCours(filePath, target.getTitle(), cours, classesParId);
                } else {
                    ExcelGenerator.genererListeCours(filePath, target.getTitle(), cours, classesParId);
                }
            }
            case CLASSES -> {
                List<Classe> classes = classeService.getAllClasses();
                if (pdf) {
                    PdfGenerator.genererListeClasses(filePath, target.getTitle(), classes);
                } else {
                    ExcelGenerator.genererListeClasses(filePath, target.getTitle(), classes);
                }
            }
            case ENSEIGNANTS -> {
                List<User> enseignants = userService.getUtilisateursByRole(Role.ENSEIGNANT);
                if (pdf) {
                    PdfGenerator.genererListeEnseignants(filePath, target.getTitle(), enseignants);
                } else {
                    ExcelGenerator.genererListeEnseignants(filePath, target.getTitle(), enseignants);
                }
            }
            case SEANCES -> {
                List<Seance> seances = getSeancesFiltrees();
                if (pdf) {
                    PdfGenerator.genererFicheSeances(filePath, target.getTitle(), seances, coursParId, enseignantsParId);
                } else {
                    ExcelGenerator.genererFicheSeances(filePath, target.getTitle(), seances, coursParId, enseignantsParId);
                }
            }
        }
    }

    private File ensureExtension(File file, String extension) {
        String path = file.getAbsolutePath();
        if (path.toLowerCase().endsWith(extension)) {
            return file;
        }

        return new File(path + extension);
    }

    private enum ExportTarget {
        FILIERES("Filieres", "Liste des filieres", "liste-filieres"),
        COURS("Cours", "Liste des cours", "liste-cours"),
        CLASSES("Classes", "Liste des classes", "liste-classes"),
        ENSEIGNANTS("Enseignants", "Liste des enseignants", "liste-enseignants"),
        SEANCES("Seances filtrees", "Rapport des seances", "rapport-seances");

        private final String label;
        private final String title;
        private final String fileNameBase;

        ExportTarget(String label, String title, String fileNameBase) {
            this.label = label;
            this.title = title;
            this.fileNameBase = fileNameBase;
        }

        private String getLabel() {
            return label;
        }

        private String getTitle() {
            return title;
        }

        private String getFileNameBase() {
            return fileNameBase;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
