package yoanemoudilou.cahiertexte.ui.classes;

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
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Filiere;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.FiliereService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

import java.util.HashMap;
import java.util.Map;

/**
 * Controleur de gestion des classes.
 */
public class ClasseManagementController {

    @FXML
    private TableView<Classe> classesTable;

    @FXML
    private TableColumn<Classe, Integer> idColumn;

    @FXML
    private TableColumn<Classe, String> nomClasseColumn;

    @FXML
    private TableColumn<Classe, String> niveauColumn;

    @FXML
    private TableColumn<Classe, String> filiereColumn;

    @FXML
    private TextField nomClasseField;

    @FXML
    private TextField niveauField;

    @FXML
    private ComboBox<Filiere> filiereComboBox;

    private final ClasseService classeService = new ClasseService();
    private final FiliereService filiereService = new FiliereService();

    private final Map<Integer, Filiere> filieresById = new HashMap<>();

    private Classe selectedClasse;

    @FXML
    private void initialize() {
        configurerTable();
        configurerComboBox();
        chargerReferences();
        chargerClasses();
        ecouterSelectionTable();
        viderFormulaire();
    }

    @FXML
    private void handleNouveau() {
        selectedClasse = null;
        viderFormulaire();
    }

    @FXML
    private void handleEnregistrer() {
        try {
            Classe classe = construireClasseDepuisFormulaire();

            if (selectedClasse == null || selectedClasse.getId() == null) {
                classeService.creerClasse(classe);
                AlertUtils.showInformation("Succes", "Creation reussie", "Classe creee avec succes.");
            } else {
                classe.setId(selectedClasse.getId());
                classeService.modifierClasse(classe);
                AlertUtils.showInformation("Succes", "Modification reussie", "Classe modifiee avec succes.");
            }

            chargerClasses();
            viderFormulaire();
            selectedClasse = null;
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'enregistrer la classe.", e);
        }
    }

    @FXML
    private void handleSupprimer() {
        try {
            if (selectedClasse == null || selectedClasse.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne une classe a supprimer.");
                return;
            }

            boolean confirmer = AlertUtils.showConfirmation(
                    "Confirmation",
                    "Suppression de classe",
                    "Veux-tu vraiment supprimer cette classe ?"
            );

            if (!confirmer) {
                return;
            }

            classeService.supprimerClasse(selectedClasse.getId());
            chargerClasses();
            viderFormulaire();
            selectedClasse = null;

            AlertUtils.showInformation("Succes", "Suppression reussie", "Classe supprimee avec succes.");
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de supprimer la classe.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerReferences();
        chargerClasses();
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        AppNavigator.goToDashboardForCurrentUser();
    }

    private void configurerTable() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }
        if (nomClasseColumn != null) {
            nomClasseColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNomClasse()));
        }
        if (niveauColumn != null) {
            niveauColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNiveau()));
        }
        if (filiereColumn != null) {
            filiereColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getFiliere() != null ? data.getValue().getFiliere().getNom() : "")
            );
        }
    }

    private void configurerComboBox() {
        if (filiereComboBox != null) {
            filiereComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Filiere filiere) {
                    return filiere == null ? "" : filiere.getCode() + " - " + filiere.getNom();
                }

                @Override
                public Filiere fromString(String string) {
                    return null;
                }
            });
        }
    }

    private void chargerReferences() {
        filieresById.clear();
        var filieres = filiereService.getAllFilieres();
        for (Filiere filiere : filieres) {
            if (filiere.getId() != null) {
                filieresById.put(filiere.getId(), filiere);
            }
        }

        if (filiereComboBox != null) {
            filiereComboBox.setItems(FXCollections.observableArrayList(filieres));
        }
    }

    private void chargerClasses() {
        if (classesTable != null) {
            classesTable.setItems(FXCollections.observableArrayList(classeService.getAllClasses()));
        }
    }

    private void ecouterSelectionTable() {
        if (classesTable != null) {
            classesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedClasse = newValue;
                remplirFormulaire(selectedClasse);
            });
        }
    }

    private void remplirFormulaire(Classe classe) {
        if (classe == null) {
            return;
        }

        if (nomClasseField != null) {
            nomClasseField.setText(classe.getNomClasse());
        }
        if (niveauField != null) {
            niveauField.setText(classe.getNiveau());
        }
        if (filiereComboBox != null) {
            Filiere filiere = classe.getFiliere() != null && classe.getFiliere().getId() != null
                    ? filieresById.get(classe.getFiliere().getId())
                    : null;
            filiereComboBox.setValue(filiere != null ? filiere : classe.getFiliere());
        }
    }

    private void viderFormulaire() {
        if (nomClasseField != null) {
            nomClasseField.clear();
        }
        if (niveauField != null) {
            niveauField.clear();
        }
        if (filiereComboBox != null) {
            filiereComboBox.setValue(null);
        }
    }

    private Classe construireClasseDepuisFormulaire() {
        Filiere filiere = filiereComboBox != null ? filiereComboBox.getValue() : null;
        return new Classe(
                nomClasseField != null ? nomClasseField.getText() : null,
                niveauField != null ? niveauField.getText() : null,
                filiere
        );
    }
}
