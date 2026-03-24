package yoanemoudilou.cahiertexte.ui.filieres;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import yoanemoudilou.cahiertexte.model.Filiere;
import yoanemoudilou.cahiertexte.service.FiliereService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

/**
 * Controleur de gestion des filieres.
 */
public class FiliereManagementController {

    @FXML
    private TableView<Filiere> filieresTable;

    @FXML
    private TableColumn<Filiere, Integer> idColumn;

    @FXML
    private TableColumn<Filiere, String> codeColumn;

    @FXML
    private TableColumn<Filiere, String> nomColumn;

    @FXML
    private TextField codeField;

    @FXML
    private TextField nomField;

    private final FiliereService filiereService = new FiliereService();

    private Filiere selectedFiliere;

    @FXML
    private void initialize() {
        configurerTable();
        chargerFilieres();
        ecouterSelectionTable();
        viderFormulaire();
    }

    @FXML
    private void handleNouveau() {
        selectedFiliere = null;
        viderFormulaire();
    }

    @FXML
    private void handleEnregistrer() {
        try {
            Filiere filiere = construireFiliereDepuisFormulaire();

            if (selectedFiliere == null || selectedFiliere.getId() == null) {
                filiereService.creerFiliere(filiere);
                AlertUtils.showInformation("Succes", "Creation reussie", "Filiere creee avec succes.");
            } else {
                filiere.setId(selectedFiliere.getId());
                filiereService.modifierFiliere(filiere);
                AlertUtils.showInformation("Succes", "Modification reussie", "Filiere modifiee avec succes.");
            }

            chargerFilieres();
            viderFormulaire();
            selectedFiliere = null;
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'enregistrer la filiere.", e);
        }
    }

    @FXML
    private void handleSupprimer() {
        try {
            if (selectedFiliere == null || selectedFiliere.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne une filiere a supprimer.");
                return;
            }

            boolean confirmer = AlertUtils.showConfirmation(
                    "Confirmation",
                    "Suppression de filiere",
                    "Veux-tu vraiment supprimer cette filiere ?"
            );

            if (!confirmer) {
                return;
            }

            filiereService.supprimerFiliere(selectedFiliere.getId());
            chargerFilieres();
            viderFormulaire();
            selectedFiliere = null;

            AlertUtils.showInformation("Succes", "Suppression reussie", "Filiere supprimee avec succes.");
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de supprimer la filiere.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerFilieres();
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        AppNavigator.goToDashboardForCurrentUser();
    }

    private void configurerTable() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }
        if (codeColumn != null) {
            codeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));
        }
        if (nomColumn != null) {
            nomColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNom()));
        }
    }

    private void chargerFilieres() {
        if (filieresTable != null) {
            filieresTable.setItems(FXCollections.observableArrayList(filiereService.getAllFilieres()));
        }
    }

    private void ecouterSelectionTable() {
        if (filieresTable != null) {
            filieresTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedFiliere = newValue;
                remplirFormulaire(selectedFiliere);
            });
        }
    }

    private void remplirFormulaire(Filiere filiere) {
        if (filiere == null) {
            return;
        }

        if (codeField != null) {
            codeField.setText(filiere.getCode());
        }
        if (nomField != null) {
            nomField.setText(filiere.getNom());
        }
    }

    private void viderFormulaire() {
        if (codeField != null) {
            codeField.clear();
        }
        if (nomField != null) {
            nomField.clear();
        }
    }

    private Filiere construireFiliereDepuisFormulaire() {
        return new Filiere(
                codeField != null ? codeField.getText() : null,
                nomField != null ? nomField.getText() : null
        );
    }
}
