package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.NotificationService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.utils.DateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur du dashboard responsable de classe.
 */
public class ResponsableDashboardController {

    @FXML
    private Label bienvenuLabel;

    @FXML
    private ComboBox<Classe> classeFilterComboBox;

    @FXML
    private Label totalCoursLabel;

    @FXML
    private Label totalSeancesLabel;

    @FXML
    private Label seancesEnAttenteLabel;

    @FXML
    private Label seancesValideesLabel;

    @FXML
    private TableView<Seance> seancesTable;

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
    private TextArea commentaireArea;

    @FXML
    private Label notificationsCountLabel;

    @FXML
    private ListView<String> notificationsListView;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final CoursService coursService = new CoursService();
    private final NotificationService notificationService = new NotificationService();
    private final SeanceService seanceService = new SeanceService();
    private final UserService userService = new UserService();

    private final Map<Integer, String> coursLabels = new HashMap<>();
    private final Map<Integer, String> enseignantsLabels = new HashMap<>();

    private Seance selectedSeance;
    private Classe classeResponsable;

    @FXML
    private void initialize() {
        configurerTable();
        configurerClasseResponsable();
        chargerDonnees();
        ecouterSelection();
    }

    @FXML
    private void handleFiltrerParClasse() {
        chargerDonnees();
    }

    @FXML
    private void handleValiderSeance() {
        mettreAJourStatut(StatutSeance.VALIDEE, "Seance validee avec succes.");
    }

    @FXML
    private void handleRejeterSeance() {
        mettreAJourStatut(StatutSeance.REJETEE, "Seance rejetee avec succes.");
    }

    @FXML
    private void handleOuvrirRapports(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/report.fxml", "Rapports");
    }

    @FXML
    private void handleOuvrirCahierTexte(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/cahier.fxml", "Cahier de texte");
    }

    @FXML
    private void handleRafraichir() {
        chargerDonnees();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }

    private void configurerTable() {
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
                    coursLabels.getOrDefault(data.getValue().getCoursId(), "Cours #" + data.getValue().getCoursId()))
            );
        }
        if (enseignantColumn != null) {
            enseignantColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    enseignantsLabels.getOrDefault(
                            data.getValue().getEnseignantId(),
                            "Enseignant #" + data.getValue().getEnseignantId()))
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
    }

    private void configurerClasseResponsable() {
        User currentUser = sessionManager.getUtilisateurConnecte();
        classeResponsable = currentUser instanceof ResponsableClasse responsableClasse
                ? responsableClasse.getClasse()
                : null;

        if (classeFilterComboBox != null) {
            classeFilterComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Classe classe) {
                    return classe == null ? "" : classe.getNomClasse() + " - " + classe.getNiveau();
                }

                @Override
                public Classe fromString(String string) {
                    return null;
                }
            });

            if (classeResponsable != null) {
                classeFilterComboBox.setItems(FXCollections.observableArrayList(classeResponsable));
                classeFilterComboBox.setValue(classeResponsable);
            } else {
                classeFilterComboBox.setItems(FXCollections.emptyObservableList());
                classeFilterComboBox.setValue(null);
            }
            classeFilterComboBox.setDisable(true);
        }
    }

    private void chargerDonnees() {
        try {
            User currentUser = sessionManager.getUtilisateurConnecte();
            setLabel(bienvenuLabel, currentUser != null ? "Bienvenue, " + currentUser.getNomComplet() : "Bienvenue");

            if (classeResponsable == null || classeResponsable.getId() == null) {
                viderDonnees();
                if (commentaireArea != null) {
                    commentaireArea.setText("Aucune classe n'est associee a ce responsable.");
                }
                return;
            }

            List<User> enseignants = userService.getUtilisateursByRole(yoanemoudilou.cahiertexte.model.Role.ENSEIGNANT);
            enseignantsLabels.clear();
            for (User enseignant : enseignants) {
                if (enseignant.getId() != null) {
                    enseignantsLabels.put(enseignant.getId(), enseignant.getNomComplet());
                }
            }

            List<Cours> cours = coursService.getCoursByClasseId(classeResponsable.getId());
            List<Seance> seances = seanceService.getSeancesByClasseId(classeResponsable.getId());

            coursLabels.clear();
            for (Cours value : cours) {
                if (value.getId() != null) {
                    coursLabels.put(value.getId(), value.getCode() + " - " + value.getIntitule());
                }
            }

            setLabel(totalCoursLabel, String.valueOf(cours.size()));
            setLabel(totalSeancesLabel, String.valueOf(seances.size()));
            setLabel(seancesEnAttenteLabel, String.valueOf(
                    seances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count()
            ));
            setLabel(seancesValideesLabel, String.valueOf(
                    seances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count()
            ));

            if (seancesTable != null) {
                seancesTable.setItems(FXCollections.observableArrayList(seances));
            }
            chargerNotifications(currentUser);
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard responsable.", e);
        }
    }

    private void ecouterSelection() {
        if (seancesTable != null) {
            seancesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                selectedSeance = newValue;
                if (commentaireArea != null) {
                    commentaireArea.setText(
                            newValue != null && newValue.getCommentaireValidation() != null
                                    ? newValue.getCommentaireValidation()
                                    : ""
                    );
                }
            });
        }
    }

    private void mettreAJourStatut(StatutSeance statut, String message) {
        try {
            if (selectedSeance == null || selectedSeance.getId() == null) {
                AlertUtils.showWarning("Selection requise", null, "Selectionne une seance a traiter.");
                return;
            }

            if (!selectedSeanceAppartientAClasseResponsable()) {
                AlertUtils.showWarning(
                        "Acces refuse",
                        null,
                        "Tu ne peux traiter que les seances de ta classe."
                );
                return;
            }

            String commentaire = commentaireArea != null ? commentaireArea.getText() : null;
            seanceService.updateStatutSeance(selectedSeance.getId(), statut, commentaire);
            if (statut == StatutSeance.VALIDEE) {
                notificationService.notifierAdminsSeanceValidee(selectedSeance, sessionManager.getUtilisateurConnecte());
            }
            chargerDonnees();
            AlertUtils.showInformation("Succes", "Mise a jour reussie", message);
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de mettre a jour la seance.", e);
        }
    }

    private boolean selectedSeanceAppartientAClasseResponsable() {
        if (selectedSeance == null || selectedSeance.getCoursId() == null || classeResponsable == null || classeResponsable.getId() == null) {
            return false;
        }

        return coursService.getCoursById(selectedSeance.getCoursId())
                .map(cours -> classeResponsable.getId().equals(cours.getClasseId()))
                .orElse(false);
    }

    private void viderDonnees() {
        setLabel(totalCoursLabel, "0");
        setLabel(totalSeancesLabel, "0");
        setLabel(seancesEnAttenteLabel, "0");
        setLabel(seancesValideesLabel, "0");
        coursLabels.clear();
        if (seancesTable != null) {
            seancesTable.setItems(FXCollections.emptyObservableList());
        }
    }

    private void setLabel(Label label, String value) {
        if (label != null) {
            label.setText(value != null ? value : "");
        }
    }

    private void chargerNotifications(User currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            return;
        }

        var notifications = notificationService.getNotificationsPourUtilisateur(currentUser.getId(), 6);
        if (notificationsListView != null) {
            notificationsListView.setItems(FXCollections.observableArrayList(
                    notifications.stream()
                            .map(n -> n.getTitre() + " - " + n.getMessage())
                            .toList()
            ));
        }
        setLabel(notificationsCountLabel, String.valueOf(notificationService.countNotificationsNonLues(currentUser.getId())));
        notificationService.marquerToutesCommeLues(currentUser.getId());
    }
}
