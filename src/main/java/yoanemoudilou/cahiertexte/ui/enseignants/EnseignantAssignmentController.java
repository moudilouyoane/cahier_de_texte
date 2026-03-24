package yoanemoudilou.cahiertexte.ui.enseignants;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import yoanemoudilou.cahiertexte.model.Affectation;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AffectationService;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

import java.util.HashMap;
import java.util.Map;

/**
 * Ecran admin dedie a l'attribution des cours aux enseignants.
 */
public class EnseignantAssignmentController {

    @FXML
    private TableView<User> enseignantsTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nomCompletColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, Boolean> valideColumn;

    @FXML
    private TableColumn<User, Boolean> actifColumn;

    @FXML
    private TableView<Cours> affectationsTable;

    @FXML
    private TableColumn<Cours, String> codeCoursColumn;

    @FXML
    private TableColumn<Cours, String> intituleCoursColumn;

    @FXML
    private TableColumn<Cours, Integer> volumeColumn;

    @FXML
    private TableColumn<Cours, String> classeCoursColumn;

    @FXML
    private ComboBox<Cours> coursComboBox;

    @FXML
    private TextField classeField;

    @FXML
    private TextField filiereField;

    @FXML
    private TextField volumeHoraireField;

    private final UserService userService = new UserService();
    private final CoursService coursService = new CoursService();
    private final ClasseService classeService = new ClasseService();
    private final AffectationService affectationService = new AffectationService();

    private final Map<Integer, Classe> classesById = new HashMap<>();

    private User selectedEnseignant;

    @FXML
    private void initialize() {
        configurerTableEnseignants();
        configurerTableAffectations();
        configurerCoursComboBox();
        ecouterSelectionEnseignant();
        ecouterSelectionCours();
        chargerClasses();
        chargerEnseignants();
        chargerCoursDisponibles();
        viderApercuCours();
    }

    @FXML
    private void handleAttribuerCours() {
        try {
            if (selectedEnseignant == null || selectedEnseignant.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne un enseignant.");
                return;
            }

            Cours cours = coursComboBox != null ? coursComboBox.getValue() : null;
            if (cours == null || cours.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne un cours a attribuer.");
                return;
            }

            affectationService.creerAffectation(new Affectation(selectedEnseignant.getId(), cours.getId()));
            chargerAffectationsEnseignant(selectedEnseignant);
            if (coursComboBox != null) {
                coursComboBox.setValue(null);
            }
            viderApercuCours();

            AlertUtils.showInformation(
                    "Succes",
                    "Attribution reussie",
                    "Le cours a ete attribue a l'enseignant. Les notifications ont ete declenchees."
            );
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'attribuer le cours a l'enseignant.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerClasses();
        chargerEnseignants();
        chargerCoursDisponibles();
        chargerAffectationsEnseignant(selectedEnseignant);
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        AppNavigator.goToDashboardForCurrentUser();
    }

    private void configurerTableEnseignants() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }
        if (nomCompletColumn != null) {
            nomCompletColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNomComplet()));
        }
        if (emailColumn != null) {
            emailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        }
        if (valideColumn != null) {
            valideColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().isValide()));
        }
        if (actifColumn != null) {
            actifColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().isActif()));
        }
    }

    private void configurerTableAffectations() {
        if (codeCoursColumn != null) {
            codeCoursColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));
        }
        if (intituleCoursColumn != null) {
            intituleCoursColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIntitule()));
        }
        if (volumeColumn != null) {
            volumeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getVolumeHoraire()));
        }
        if (classeCoursColumn != null) {
            classeCoursColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(formatClasseLabel(classesById.get(data.getValue().getClasseId())))
            );
        }
    }

    private void configurerCoursComboBox() {
        if (coursComboBox != null) {
            coursComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Cours cours) {
                    if (cours == null) {
                        return "";
                    }
                    return cours.getIntitule() + " - " + formatClasseLabel(classesById.get(cours.getClasseId()));
                }

                @Override
                public Cours fromString(String string) {
                    return null;
                }
            });
        }
    }

    private void ecouterSelectionEnseignant() {
        if (enseignantsTable != null) {
            enseignantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedEnseignant = newValue;
                chargerAffectationsEnseignant(newValue);
            });
        }
    }

    private void ecouterSelectionCours() {
        if (coursComboBox != null) {
            coursComboBox.valueProperty().addListener((obs, oldValue, newValue) -> remplirApercuCours(newValue));
        }
    }

    private void chargerClasses() {
        classesById.clear();
        for (Classe classe : classeService.getAllClasses()) {
            if (classe.getId() != null) {
                classesById.put(classe.getId(), classe);
            }
        }
    }

    private void chargerEnseignants() {
        if (enseignantsTable != null) {
            enseignantsTable.setItems(FXCollections.observableArrayList(
                    userService.getUtilisateursByRole(Role.ENSEIGNANT)
            ));
        }
    }

    private void chargerCoursDisponibles() {
        if (coursComboBox != null) {
            coursComboBox.setItems(FXCollections.observableArrayList(coursService.getAllCours()));
        }
    }

    private void chargerAffectationsEnseignant(User enseignant) {
        if (affectationsTable == null) {
            return;
        }

        if (enseignant == null || enseignant.getId() == null) {
            affectationsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        affectationsTable.setItems(FXCollections.observableArrayList(
                coursService.getCoursByEnseignantId(enseignant.getId())
        ));
    }

    private void remplirApercuCours(Cours cours) {
        if (cours == null) {
            viderApercuCours();
            return;
        }

        Classe classe = classesById.get(cours.getClasseId());

        if (classeField != null) {
            classeField.setText(classe != null && classe.getNomClasse() != null ? classe.getNomClasse() : "");
        }
        if (filiereField != null) {
            filiereField.setText(
                    classe != null && classe.getFiliere() != null && classe.getFiliere().getNom() != null
                            ? classe.getFiliere().getNom()
                            : ""
            );
        }
        if (volumeHoraireField != null) {
            volumeHoraireField.setText(cours.getVolumeHoraire() != null ? String.valueOf(cours.getVolumeHoraire()) : "");
        }
    }

    private void viderApercuCours() {
        if (classeField != null) {
            classeField.clear();
        }
        if (filiereField != null) {
            filiereField.clear();
        }
        if (volumeHoraireField != null) {
            volumeHoraireField.clear();
        }
    }

    private String formatClasseLabel(Classe classe) {
        if (classe == null) {
            return "";
        }

        String filiereNom = classe.getFiliere() != null ? classe.getFiliere().getNom() : "";
        return filiereNom == null || filiereNom.isBlank()
                ? classe.getNomClasse()
                : classe.getNomClasse() + " - " + filiereNom;
    }
}
