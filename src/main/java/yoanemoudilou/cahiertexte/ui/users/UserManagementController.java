package yoanemoudilou.cahiertexte.ui.users;

import yoanemoudilou.cahiertexte.model.ChefDepartement;
import yoanemoudilou.cahiertexte.model.Enseignant;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Contrôleur de gestion des utilisateurs.
 */
public class UserManagementController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nomColumn;

    @FXML
    private TableColumn<User, String> prenomColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Boolean> valideColumn;

    @FXML
    private TableColumn<User, Boolean> actifColumn;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private CheckBox valideCheckBox;

    @FXML
    private CheckBox actifCheckBox;

    private final UserService userService = new UserService();

    private User selectedUser;

    @FXML
    private void initialize() {
        configurerTable();
        chargerRoles();
        chargerUtilisateurs();
        ecouterSelectionTable();
        viderFormulaire();
    }

    @FXML
    private void handleNouveau() {
        selectedUser = null;
        viderFormulaire();
    }

    @FXML
    private void handleEnregistrer() {
        try {
            User user = construireUtilisateurDepuisFormulaire();

            if (selectedUser == null || selectedUser.getId() == null) {
                userService.creerUtilisateur(user);
                AlertUtils.showInformation("Succès", "Création réussie", "Utilisateur créé avec succès.");
            } else {
                user.setId(selectedUser.getId());
                userService.modifierUtilisateur(user);
                AlertUtils.showInformation("Succès", "Modification réussie", "Utilisateur modifié avec succès.");
            }

            chargerUtilisateurs();
            viderFormulaire();
            selectedUser = null;

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible d'enregistrer l'utilisateur.", e);
        }
    }

    @FXML
    private void handleSupprimer() {
        try {
            if (selectedUser == null || selectedUser.getId() == null) {
                AlertUtils.showWarning("Sélection requise", null, "Sélectionne un utilisateur à supprimer.");
                return;
            }

            boolean confirmer = AlertUtils.showConfirmation(
                    "Confirmation",
                    "Suppression d'utilisateur",
                    "Veux-tu vraiment supprimer cet utilisateur ?"
            );

            if (!confirmer) {
                return;
            }

            userService.supprimerUtilisateur(selectedUser.getId());
            chargerUtilisateurs();
            viderFormulaire();
            selectedUser = null;

            AlertUtils.showInformation("Succès", "Suppression réussie", "Utilisateur supprimé avec succès.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de supprimer l'utilisateur.", e);
        }
    }

    @FXML
    private void handleValiderUtilisateur() {
        try {
            if (selectedUser == null || selectedUser.getId() == null) {
                AlertUtils.showWarning("Sélection requise", null, "Sélectionne un utilisateur à valider.");
                return;
            }

            userService.validerUtilisateur(selectedUser.getId());
            chargerUtilisateurs();
            AlertUtils.showInformation("Succès", "Validation réussie", "Utilisateur validé avec succès.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de valider l'utilisateur.", e);
        }
    }

    @FXML
    private void handleBasculerActivation() {
        try {
            if (selectedUser == null || selectedUser.getId() == null) {
                AlertUtils.showWarning("Sélection requise", null, "Sélectionne un utilisateur.");
                return;
            }

            if (selectedUser.isActif()) {
                userService.desactiverUtilisateur(selectedUser.getId());
            } else {
                userService.activerUtilisateur(selectedUser.getId());
            }

            chargerUtilisateurs();
            AlertUtils.showInformation("Succès", "Mise à jour réussie", "Statut actif mis à jour.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de modifier l'état actif.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerUtilisateurs();
    }

    private void configurerTable() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        }

        if (nomColumn != null) {
            nomColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNom()));
        }

        if (prenomColumn != null) {
            prenomColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPrenom()));
        }

        if (emailColumn != null) {
            emailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        }

        if (roleColumn != null) {
            roleColumn.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(data.getValue().getRole() != null ? data.getValue().getRole().name() : "")
            );
        }

        if (valideColumn != null) {
            valideColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().isValide()));
        }

        if (actifColumn != null) {
            actifColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().isActif()));
        }
    }

    private void chargerRoles() {
        if (roleComboBox != null) {
            roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        }
    }

    private void chargerUtilisateurs() {
        if (usersTable != null) {
            usersTable.setItems(FXCollections.observableArrayList(userService.getAllUtilisateurs()));
        }
    }

    private void ecouterSelectionTable() {
        if (usersTable != null) {
            usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedUser = newValue;
                remplirFormulaire(selectedUser);
            });
        }
    }

    private void remplirFormulaire(User user) {
        if (user == null) {
            return;
        }

        if (nomField != null) {
            nomField.setText(user.getNom());
        }
        if (prenomField != null) {
            prenomField.setText(user.getPrenom());
        }
        if (emailField != null) {
            emailField.setText(user.getEmail());
        }
        if (passwordField != null) {
            passwordField.clear();
        }
        if (roleComboBox != null) {
            roleComboBox.setValue(user.getRole());
        }
        if (valideCheckBox != null) {
            valideCheckBox.setSelected(user.isValide());
        }
        if (actifCheckBox != null) {
            actifCheckBox.setSelected(user.isActif());
        }
    }

    private void viderFormulaire() {
        if (nomField != null) {
            nomField.clear();
        }
        if (prenomField != null) {
            prenomField.clear();
        }
        if (emailField != null) {
            emailField.clear();
        }
        if (passwordField != null) {
            passwordField.clear();
        }
        if (roleComboBox != null) {
            roleComboBox.setValue(null);
        }
        if (valideCheckBox != null) {
            valideCheckBox.setSelected(false);
        }
        if (actifCheckBox != null) {
            actifCheckBox.setSelected(true);
        }
    }

    private User construireUtilisateurDepuisFormulaire() {
        Role role = roleComboBox != null ? roleComboBox.getValue() : null;

        User user = switch (role) {
            case ENSEIGNANT -> new Enseignant();
            case RESPONSABLE_CLASSE -> new ResponsableClasse();
            case CHEF_DEPARTEMENT -> new ChefDepartement();
            case null -> throw new IllegalArgumentException("Le rôle est requis.");
        };

        user.setNom(nomField != null ? nomField.getText() : null);
        user.setPrenom(prenomField != null ? prenomField.getText() : null);
        user.setEmail(emailField != null ? emailField.getText() : null);
        user.setMotDePasse(passwordField != null ? passwordField.getText() : null);
        user.setRole(role);
        user.setValide(valideCheckBox != null && valideCheckBox.isSelected());
        user.setActif(actifCheckBox == null || actifCheckBox.isSelected());

        return user;
    }
}