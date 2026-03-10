package yoanemoudilou.cahiertexte.ui.cours;

import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur de gestion des cours.
 */
public class CoursController {

    @FXML
    private TableView<Cours> coursTable;

    @FXML
    private TableColumn<Cours, Integer> idColumn;

    @FXML
    private TableColumn<Cours, String> codeColumn;

    @FXML
    private TableColumn<Cours, String> intituleColumn;

    @FXML
    private TableColumn<Cours, Integer> volumeHoraireColumn;

    @FXML
    private TableColumn<Cours, String> classeColumn;

    @FXML
    private TextField codeField;

    @FXML
    private TextField intituleField;

    @FXML
    private TextField volumeHoraireField;

    @FXML
    private ComboBox<Classe> classeComboBox;

    private final CoursService coursService = new CoursService();
    private final ClasseService classeService = new ClasseService();

    private final Map<Integer, Classe> classesById = new HashMap<>();

    private Cours selectedCours;

    @FXML
    private void initialize() {
        configurerTable();
        configurerComboBoxClasses();
        chargerClasses();
        chargerCours();
        ecouterSelectionTable();
    }

    @FXML
    private void handleNouveau() {
        selectedCours = null;
        viderFormulaire();
    }

    @FXML
    private void handleEnregistrer() {
        try {
            Cours cours = construireCoursDepuisFormulaire();

            if (selectedCours == null || selectedCours.getId() == null) {
                coursService.creerCours(cours);
                AlertUtils.showInformation("Succès", "Création réussie", "Cours créé avec succès.");
            } else {
                cours.setId(selectedCours.getId());
                coursService.modifierCours(cours);
                AlertUtils.showInformation("Succès", "Modification réussie", "Cours modifié avec succès.");
            }

            chargerCours();
            viderFormulaire();
            selectedCours = null;

        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Saisie invalide", null, "Le volume horaire doit être un nombre entier.");
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'enregistrer le cours.", e);
        }
    }

    @FXML
    private void handleSupprimer() {
        try {
            if (selectedCours == null || selectedCours.getId() == null) {
                AlertUtils.showWarning("Sélection requise", null, "Sélectionne un cours à supprimer.");
                return;
            }

            boolean confirmer = AlertUtils.showConfirmation(
                    "Confirmation",
                    "Suppression de cours",
                    "Veux-tu vraiment supprimer ce cours ?"
            );

            if (!confirmer) {
                return;
            }

            coursService.supprimerCours(selectedCours.getId());
            chargerCours();
            viderFormulaire();
            selectedCours = null;

            AlertUtils.showInformation("Succès", "Suppression réussie", "Cours supprimé avec succès.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de supprimer le cours.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerClasses();
        chargerCours();
    }

    private void configurerTable() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }

        if (codeColumn != null) {
            codeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));
        }

        if (intituleColumn != null) {
            intituleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIntitule()));
        }

        if (volumeHoraireColumn != null) {
            volumeHoraireColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getVolumeHoraire()));
        }

        if (classeColumn != null) {
            classeColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(getClasseLabel(data.getValue().getClasseId()))
            );
        }
    }

    private void configurerComboBoxClasses() {
        if (classeComboBox != null) {
            classeComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Classe classe) {
                    if (classe == null) {
                        return "";
                    }
                    return classe.getNomClasse() + " - " + classe.getNiveau();
                }

                @Override
                public Classe fromString(String string) {
                    return null;
                }
            });
        }
    }

    private void chargerClasses() {
        classesById.clear();

        var classes = classeService.getAllClasses();

        for (Classe classe : classes) {
            if (classe.getId() != null) {
                classesById.put(classe.getId(), classe);
            }
        }

        if (classeComboBox != null) {
            classeComboBox.setItems(FXCollections.observableArrayList(classes));
        }
    }

    private void chargerCours() {
        if (coursTable != null) {
            coursTable.setItems(FXCollections.observableArrayList(coursService.getAllCours()));
        }
    }

    private void ecouterSelectionTable() {
        if (coursTable != null) {
            coursTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedCours = newValue;
                remplirFormulaire(selectedCours);
            });
        }
    }

    private void remplirFormulaire(Cours cours) {
        if (cours == null) {
            return;
        }

        if (codeField != null) {
            codeField.setText(cours.getCode());
        }
        if (intituleField != null) {
            intituleField.setText(cours.getIntitule());
        }
        if (volumeHoraireField != null) {
            volumeHoraireField.setText(cours.getVolumeHoraire() != null ? String.valueOf(cours.getVolumeHoraire()) : "");
        }
        if (classeComboBox != null) {
            classeComboBox.setValue(classesById.get(cours.getClasseId()));
        }
    }

    private void viderFormulaire() {
        if (codeField != null) {
            codeField.clear();
        }
        if (intituleField != null) {
            intituleField.clear();
        }
        if (volumeHoraireField != null) {
            volumeHoraireField.clear();
        }
        if (classeComboBox != null) {
            classeComboBox.setValue(null);
        }
    }

    private Cours construireCoursDepuisFormulaire() {
        Classe classe = classeComboBox != null ? classeComboBox.getValue() : null;

        Integer volumeHoraire = volumeHoraireField != null && !volumeHoraireField.getText().isBlank()
                ? Integer.parseInt(volumeHoraireField.getText().trim())
                : null;

        return new Cours(
                codeField != null ? codeField.getText() : null,
                intituleField != null ? intituleField.getText() : null,
                volumeHoraire,
                classe != null ? classe.getId() : null
        );
    }

    private String getClasseLabel(Integer classeId) {
        if (classeId == null) {
            return "";
        }

        Classe classe = classesById.get(classeId);
        if (classe == null) {
            return "Classe #" + classeId;
        }

        return classe.getNomClasse() + " - " + classe.getNiveau();
    }
}