package yoanemoudilou.cahiertexte.ui.validation;

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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur de validation des séances.
 */
public class ValidationSeanceController {

    @FXML
    private TableView<Seance> seancesTable;

    @FXML
    private TableColumn<Seance, Integer> idColumn;

    @FXML
    private TableColumn<Seance, String> dateColumn;

    @FXML
    private TableColumn<Seance, String> heureColumn;

    @FXML
    private TableColumn<Seance, String> coursColumn;

    @FXML
    private TableColumn<Seance, String> enseignantColumn;

    @FXML
    private TableColumn<Seance, String> statutColumn;

    @FXML
    private TableColumn<Seance, String> contenuColumn;

    @FXML
    private TableColumn<Seance, String> observationsColumn;

    @FXML
    private ComboBox<StatutSeance> filtreStatutComboBox;

    @FXML
    private TextArea commentaireValidationArea;

    private final SeanceService seanceService = new SeanceService();
    private final CoursService coursService = new CoursService();
    private final UserService userService = new UserService();

    private final Map<Integer, String> coursLabels = new HashMap<>();
    private final Map<Integer, String> enseignantsLabels = new HashMap<>();

    private Seance selectedSeance;

    @FXML
    private void initialize() {
        configurerTable();
        chargerFiltreStatut();
        chargerReferences();
        chargerSeances();
        ecouterSelectionTable();
    }

    @FXML
    private void handleFiltrer() {
        chargerSeances();
    }

    @FXML
    private void handleRafraichir() {
        chargerReferences();
        chargerSeances();
    }

    @FXML
    private void handleValider() {
        mettreAJourStatut(StatutSeance.VALIDEE, "Séance validée avec succès.");
    }

    @FXML
    private void handleRejeter() {
        mettreAJourStatut(StatutSeance.REJETEE, "Séance rejetée avec succès.");
    }

    @FXML
    private void handleRemettreEnAttente() {
        mettreAJourStatut(StatutSeance.EN_ATTENTE, "Séance remise en attente avec succès.");
    }

    private void configurerTable() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }

        if (dateColumn != null) {
            dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatDate(data.getValue().getDateSeance()))
            );
        }

        if (heureColumn != null) {
            heureColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatTime(data.getValue().getHeureSeance()))
            );
        }

        if (coursColumn != null) {
            coursColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    getCoursLabel(data.getValue().getCoursId()))
            );
        }

        if (enseignantColumn != null) {
            enseignantColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    getEnseignantLabel(data.getValue().getEnseignantId()))
            );
        }

        if (statutColumn != null) {
            statutColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getStatut() != null ? data.getValue().getStatut().name() : "")
            );
        }

        if (contenuColumn != null) {
            contenuColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContenu()));
        }

        if (observationsColumn != null) {
            observationsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getObservations()));
        }
    }

    private void chargerFiltreStatut() {
        if (filtreStatutComboBox != null) {
            filtreStatutComboBox.setItems(FXCollections.observableArrayList(StatutSeance.values()));
            filtreStatutComboBox.setValue(StatutSeance.EN_ATTENTE);
        }
    }

    private void chargerReferences() {
        List<Cours> cours = coursService.getAllCours();
        List<User> enseignants = userService.getUtilisateursByRole(Role.ENSEIGNANT);

        coursLabels.clear();
        enseignantsLabels.clear();

        for (Cours c : cours) {
            if (c.getId() != null) {
                coursLabels.put(c.getId(), c.getCode() + " - " + c.getIntitule());
            }
        }

        for (User u : enseignants) {
            if (u.getId() != null) {
                enseignantsLabels.put(u.getId(), u.getNomComplet());
            }
        }
    }

    private void chargerSeances() {
        List<Seance> seances;

        if (filtreStatutComboBox != null && filtreStatutComboBox.getValue() != null) {
            seances = seanceService.getSeancesByStatut(filtreStatutComboBox.getValue());
        } else {
            seances = seanceService.getAllSeances();
        }

        if (seancesTable != null) {
            seancesTable.setItems(FXCollections.observableArrayList(seances));
        }
    }

    private void ecouterSelectionTable() {
        if (seancesTable != null) {
            seancesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedSeance = newValue;

                if (commentaireValidationArea != null) {
                    commentaireValidationArea.setText(
                            newValue != null && newValue.getCommentaireValidation() != null
                                    ? newValue.getCommentaireValidation()
                                    : ""
                    );
                }
            });
        }
    }

    private void mettreAJourStatut(StatutSeance statut, String successMessage) {
        try {
            if (selectedSeance == null || selectedSeance.getId() == null) {
                AlertUtils.showWarning("Sélection requise", null, "Sélectionne une séance à traiter.");
                return;
            }

            String commentaire = commentaireValidationArea != null
                    ? commentaireValidationArea.getText()
                    : null;

            seanceService.updateStatutSeance(selectedSeance.getId(), statut, commentaire);
            chargerSeances();

            AlertUtils.showInformation("Succès", "Mise à jour réussie", successMessage);

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de mettre à jour le statut de la séance.", e);
        }
    }

    private String getCoursLabel(Integer coursId) {
        return coursId != null ? coursLabels.getOrDefault(coursId, "Cours #" + coursId) : "";
    }

    private String getEnseignantLabel(Integer enseignantId) {
        return enseignantId != null ? enseignantsLabels.getOrDefault(enseignantId, "Enseignant #" + enseignantId) : "";
    }
}
