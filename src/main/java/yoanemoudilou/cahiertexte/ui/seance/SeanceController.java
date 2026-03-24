package yoanemoudilou.cahiertexte.ui.seance;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.NotificationService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.utils.DateUtils;
import javafx.event.ActionEvent;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur de gestion des séances.
 */
public class SeanceController {

    @FXML
    private TableView<Seance> seancesTable;

    @FXML
    private TableColumn<Seance, Integer> idColumn;

    @FXML
    private TableColumn<Seance, String> dateColumn;

    @FXML
    private TableColumn<Seance, String> heureColumn;

    @FXML
    private TableColumn<Seance, Integer> dureeColumn;

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
    private ComboBox<Cours> coursComboBox;

    @FXML
    private ComboBox<User> enseignantComboBox;

    @FXML
    private DatePicker dateSeancePicker;

    @FXML
    private TextField heureField;

    @FXML
    private TextField dureeField;

    @FXML
    private TextArea contenuArea;

    @FXML
    private TextArea observationsArea;

    @FXML
    private Label sessionInfoLabel;

    private final SeanceService seanceService = new SeanceService();
    private final CoursService coursService = new CoursService();
    private final UserService userService = new UserService();
    private final NotificationService notificationService = new NotificationService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private final Map<Integer, String> coursLabels = new HashMap<>();
    private final Map<Integer, String> enseignantsLabels = new HashMap<>();

    private Seance selectedSeance;

    @FXML
    private void initialize() {
        configurerTable();
        configurerComboBoxes();
        chargerReferences();
        configurerUtilisateurConnecte();
        chargerSeances();
        ecouterSelectionTable();
    }

    @FXML
    private void handleNouveau() {
        selectedSeance = null;
        viderFormulaire();
        configurerUtilisateurConnecte();
    }

    @FXML
    private void handleEnregistrer() {
        try {
            Seance seance = construireSeanceDepuisFormulaire();

            if (selectedSeance == null || selectedSeance.getId() == null) {
                Seance saved = seanceService.creerSeance(seance);
                notifierResponsableSiEnseignant(saved);
                AlertUtils.showInformation("Succès", "Création réussie", "Séance créée avec succès.");
            } else {
                seance.setId(selectedSeance.getId());
                seance.setStatut(selectedSeance.getStatut());
                seance.setCommentaireValidation(selectedSeance.getCommentaireValidation());

                seanceService.modifierSeance(seance);
                notifierResponsableSiEnseignant(seance);
                AlertUtils.showInformation("Succès", "Modification réussie", "Séance modifiée avec succès.");
            }

            chargerSeances();
            viderFormulaire();
            configurerUtilisateurConnecte();
            selectedSeance = null;

        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Saisie invalide", null, "La durée doit être un nombre entier.");
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'enregistrer la séance.", e);
        }
    }

    @FXML
    private void handleSupprimer() {
        try {
            if (selectedSeance == null || selectedSeance.getId() == null) {
                AlertUtils.showWarning("Sélection requise", null, "Sélectionne une séance à supprimer.");
                return;
            }

            boolean confirmer = AlertUtils.showConfirmation(
                    "Confirmation",
                    "Suppression de séance",
                    "Veux-tu vraiment supprimer cette séance ?"
            );

            if (!confirmer) {
                return;
            }

            seanceService.supprimerSeance(selectedSeance.getId());
            chargerSeances();
            viderFormulaire();
            configurerUtilisateurConnecte();
            selectedSeance = null;

            AlertUtils.showInformation("Succès", "Suppression réussie", "Séance supprimée avec succès.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de supprimer la séance.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerReferences();
        configurerUtilisateurConnecte();
        chargerSeances();
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
            dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatDate(data.getValue().getDateSeance()))
            );
        }

        if (heureColumn != null) {
            heureColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatTime(data.getValue().getHeureSeance()))
            );
        }

        if (dureeColumn != null) {
            dureeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getDuree()));
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

    private void configurerComboBoxes() {
        if (coursComboBox != null) {
            coursComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Cours cours) {
                    return cours == null ? "" : cours.getCode() + " - " + cours.getIntitule();
                }

                @Override
                public Cours fromString(String string) {
                    return null;
                }
            });
        }

        if (enseignantComboBox != null) {
            enseignantComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(User user) {
                    return user == null ? "" : user.getNomComplet();
                }

                @Override
                public User fromString(String string) {
                    return null;
                }
            });
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

        for (User enseignant : enseignants) {
            if (enseignant.getId() != null) {
                enseignantsLabels.put(enseignant.getId(), enseignant.getNomComplet());
            }
        }

        if (coursComboBox != null) {
            coursComboBox.setItems(FXCollections.observableArrayList(cours));
        }

        if (enseignantComboBox != null) {
            enseignantComboBox.setItems(FXCollections.observableArrayList(enseignants));
        }
    }

    private void configurerUtilisateurConnecte() {
        User currentUser = sessionManager.getUtilisateurConnecte();

        if (currentUser != null && currentUser.getRole() == Role.ENSEIGNANT) {
            if (enseignantComboBox != null) {
                enseignantComboBox.setValue(currentUser);
                enseignantComboBox.setDisable(true);
            }
            if (sessionInfoLabel != null) {
                sessionInfoLabel.setText("Saisie pour l'enseignant connecté : " + currentUser.getNomComplet());
            }
        } else {
            if (enseignantComboBox != null) {
                enseignantComboBox.setDisable(false);
            }
            if (sessionInfoLabel != null) {
                sessionInfoLabel.setText("");
            }
        }
    }

    private void chargerSeances() {
        User currentUser = sessionManager.getUtilisateurConnecte();

        List<Seance> seances;
        if (currentUser != null && currentUser.getRole() == Role.ENSEIGNANT && currentUser.getId() != null) {
            seances = seanceService.getSeancesByEnseignantId(currentUser.getId());
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
                remplirFormulaire(selectedSeance);
            });
        }
    }

    private void remplirFormulaire(Seance seance) {
        if (seance == null) {
            return;
        }

        if (coursComboBox != null) {
            coursComboBox.getItems().stream()
                    .filter(c -> c.getId() != null && c.getId().equals(seance.getCoursId()))
                    .findFirst()
                    .ifPresent(coursComboBox::setValue);
        }

        if (enseignantComboBox != null) {
            enseignantComboBox.getItems().stream()
                    .filter(u -> u.getId() != null && u.getId().equals(seance.getEnseignantId()))
                    .findFirst()
                    .ifPresent(enseignantComboBox::setValue);
        }

        if (dateSeancePicker != null) {
            dateSeancePicker.setValue(seance.getDateSeance());
        }

        if (heureField != null) {
            heureField.setText(DateUtils.formatTime(seance.getHeureSeance()));
        }

        if (dureeField != null) {
            dureeField.setText(seance.getDuree() != null ? String.valueOf(seance.getDuree()) : "");
        }

        if (contenuArea != null) {
            contenuArea.setText(seance.getContenu());
        }

        if (observationsArea != null) {
            observationsArea.setText(seance.getObservations());
        }
    }

    private void viderFormulaire() {
        if (coursComboBox != null) {
            coursComboBox.setValue(null);
        }
        if (enseignantComboBox != null) {
            enseignantComboBox.setValue(null);
        }
        if (dateSeancePicker != null) {
            dateSeancePicker.setValue(null);
        }
        if (heureField != null) {
            heureField.clear();
        }
        if (dureeField != null) {
            dureeField.clear();
        }
        if (contenuArea != null) {
            contenuArea.clear();
        }
        if (observationsArea != null) {
            observationsArea.clear();
        }
    }

    private Seance construireSeanceDepuisFormulaire() {
        Cours cours = coursComboBox != null ? coursComboBox.getValue() : null;
        User enseignant = enseignantComboBox != null ? enseignantComboBox.getValue() : null;

        Integer duree = dureeField != null && !dureeField.getText().isBlank()
                ? Integer.parseInt(dureeField.getText().trim())
                : null;

        return new Seance(
                cours != null ? cours.getId() : null,
                enseignant != null ? enseignant.getId() : null,
                dateSeancePicker != null ? dateSeancePicker.getValue() : null,
                DateUtils.parseTime(heureField != null ? heureField.getText() : null),
                duree,
                contenuArea != null ? contenuArea.getText() : null,
                observationsArea != null ? observationsArea.getText() : null,
                null,
                null
        );
    }

    private String getCoursLabel(Integer coursId) {
        return coursId != null ? coursLabels.getOrDefault(coursId, "Cours #" + coursId) : "";
    }

    private String getEnseignantLabel(Integer enseignantId) {
        return enseignantId != null ? enseignantsLabels.getOrDefault(enseignantId, "Enseignant #" + enseignantId) : "";
    }

    private void notifierResponsableSiEnseignant(Seance seance) {
        User currentUser = sessionManager.getUtilisateurConnecte();
        if (currentUser != null
                && currentUser.getRole() == Role.ENSEIGNANT
                && seance != null
                && (seance.getStatut() == null || seance.getStatut() == yoanemoudilou.cahiertexte.model.StatutSeance.EN_ATTENTE)) {
            notificationService.notifierResponsablePourVerification(seance);
        }
    }
}
