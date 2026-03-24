package yoanemoudilou.cahiertexte.ui.users;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import yoanemoudilou.cahiertexte.model.ChefDepartement;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Enseignant;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;

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
    private ComboBox<Classe> classeComboBox;

    @FXML
    private CheckBox valideCheckBox;

    @FXML
    private CheckBox actifCheckBox;

    private final UserService userService = new UserService();
    private final ClasseService classeService = new ClasseService();

    private User selectedUser;

    @FXML
    private void initialize() {
        configurerTable();
        chargerRoles();
        chargerClasses();
        ecouterChangementRole();
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
                AlertUtils.showInformation("Succes", "Creation reussie", "Utilisateur cree avec succes.");
            } else {
                user.setId(selectedUser.getId());
                userService.modifierUtilisateur(user);
                AlertUtils.showInformation("Succes", "Modification reussie", "Utilisateur modifie avec succes.");
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
                AlertUtils.showWarning("Selection requise", null, "Selectionne un utilisateur a supprimer.");
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

            AlertUtils.showInformation("Succes", "Suppression reussie", "Utilisateur supprime avec succes.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de supprimer l'utilisateur.", e);
        }
    }

    @FXML
    private void handleValiderUtilisateur() {
        try {
            if (selectedUser == null || selectedUser.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne un utilisateur a valider.");
                return;
            }

            userService.validerUtilisateur(selectedUser.getId());
            chargerUtilisateurs();
            AlertUtils.showInformation("Succes", "Validation reussie", "Utilisateur valide avec succes.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de valider l'utilisateur.", e);
        }
    }

    @FXML
    private void handleBasculerActivation() {
        try {
            if (selectedUser == null || selectedUser.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne un utilisateur.");
                return;
            }

            if (selectedUser.isActif()) {
                userService.desactiverUtilisateur(selectedUser.getId());
            } else {
                userService.activerUtilisateur(selectedUser.getId());
            }

            chargerUtilisateurs();
            AlertUtils.showInformation("Succes", "Mise a jour reussie", "Statut actif mis a jour.");

        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de modifier l'etat actif.", e);
        }
    }

    @FXML
    private void handleRafraichir() {
        chargerClasses();
        chargerUtilisateurs();
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        AppNavigator.goToDashboardForCurrentUser();
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

    private void chargerClasses() {
        if (classeComboBox == null) {
            return;
        }

        classeComboBox.setItems(FXCollections.observableArrayList(classeService.getAllClasses()));
        classeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Classe classe) {
                return classe == null ? "" : classe.getNomClasse() + " - " + classe.getNiveau();
            }

            @Override
            public Classe fromString(String string) {
                return null;
            }
        });
    }

    private void ecouterChangementRole() {
        if (roleComboBox != null) {
            roleComboBox.valueProperty().addListener((obs, oldValue, newValue) -> mettreAJourChampClasse(newValue));
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

        if (classeComboBox != null) {
            Classe classe = user instanceof ResponsableClasse responsableClasse ? responsableClasse.getClasse() : null;
            selectClasse(classe);
        }

        mettreAJourChampClasse(user.getRole());
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
        if (classeComboBox != null) {
            classeComboBox.setValue(null);
            classeComboBox.setDisable(true);
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
            case null -> throw new IllegalArgumentException("Le role est requis.");
        };

        user.setNom(nomField != null ? nomField.getText() : null);
        user.setPrenom(prenomField != null ? prenomField.getText() : null);
        user.setEmail(emailField != null ? emailField.getText() : null);
        user.setMotDePasse(passwordField != null ? passwordField.getText() : null);
        user.setRole(role);
        user.setValide(valideCheckBox != null && valideCheckBox.isSelected());
        user.setActif(actifCheckBox == null || actifCheckBox.isSelected());

        if (user instanceof ResponsableClasse responsableClasse) {
            Classe classe = classeComboBox != null ? classeComboBox.getValue() : null;
            if (classe == null || classe.getId() == null) {
                throw new IllegalArgumentException("La classe du responsable est requise.");
            }
            responsableClasse.setClasse(classe);
        }

        return user;
    }

    private void mettreAJourChampClasse(Role role) {
        if (classeComboBox == null) {
            return;
        }

        boolean isResponsable = role == Role.RESPONSABLE_CLASSE;
        classeComboBox.setDisable(!isResponsable);
        if (!isResponsable) {
            classeComboBox.setValue(null);
        }
    }

    private void selectClasse(Classe classe) {
        if (classeComboBox == null) {
            return;
        }

        if (classe == null || classe.getId() == null) {
            classeComboBox.setValue(null);
            return;
        }

        classeComboBox.getItems().stream()
                .filter(item -> item.getId() != null && item.getId().equals(classe.getId()))
                .findFirst()
                .ifPresentOrElse(classeComboBox::setValue, () -> classeComboBox.setValue(classe));
    }
}
