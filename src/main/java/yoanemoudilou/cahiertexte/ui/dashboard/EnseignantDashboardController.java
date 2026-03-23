package yoanemoudilou.cahiertexte.ui.dashboard;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.utils.DateUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur du dashboard enseignant.
 */
public class EnseignantDashboardController {

    @FXML
    private Label bienvenuLabel;

    @FXML
    private Label mesCoursCountLabel;

    @FXML
    private Label mesSeancesCountLabel;

    @FXML
    private Label seancesEnAttenteCountLabel;

    @FXML
    private Label seancesValideesCountLabel;

    @FXML
    private Label seancesRejeteesCountLabel;

    @FXML
    private TableView<Cours> mesCoursTable;

    @FXML
    private TableColumn<Cours, String> coursCodeColumn;

    @FXML
    private TableColumn<Cours, String> coursIntituleColumn;

    @FXML
    private TableColumn<Cours, Integer> coursVolumeColumn;

    @FXML
    private TableView<Seance> dernieresSeancesTable;

    @FXML
    private TableColumn<Seance, String> seanceDateColumn;

    @FXML
    private TableColumn<Seance, String> seanceHeureColumn;

    @FXML
    private TableColumn<Seance, String> seanceCoursColumn;

    @FXML
    private TableColumn<Seance, String> seanceStatutColumn;

    @FXML
    private TableColumn<Seance, String> seanceContenuColumn;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final CoursService coursService = new CoursService();
    private final SeanceService seanceService = new SeanceService();
    private final Map<Integer, String> coursLabels = new HashMap<>();

    @FXML
    private void initialize() {
        configurerTables();
        chargerInfos();
    }

    @FXML
    private void handleNouvelleSeance(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/seance.fxml", "Nouvelle séance");
    }

    @FXML
    private void handleVoirMesSeances(ActionEvent event) {
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/seance.fxml", "Mes séances");
    }

    @FXML
    private void handleRafraichir() {
        chargerInfos();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        AppNavigator.navigate(event, "/yoanemoudilou/cahiertexte/view/login.fxml", "Connexion");
    }

    private void configurerTables() {
        if (coursCodeColumn != null) {
            coursCodeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));
        }
        if (coursIntituleColumn != null) {
            coursIntituleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIntitule()));
        }
        if (coursVolumeColumn != null) {
            coursVolumeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getVolumeHoraire()));
        }
        if (seanceDateColumn != null) {
            seanceDateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatDate(data.getValue().getDateSeance()))
            );
        }
        if (seanceHeureColumn != null) {
            seanceHeureColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatTime(data.getValue().getHeureSeance()))
            );
        }
        if (seanceCoursColumn != null) {
            seanceCoursColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    coursLabels.getOrDefault(data.getValue().getCoursId(), "Cours #" + data.getValue().getCoursId()))
            );
        }
        if (seanceStatutColumn != null) {
            seanceStatutColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getStatut() != null ? data.getValue().getStatut().name() : "")
            );
        }
        if (seanceContenuColumn != null) {
            seanceContenuColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContenu()));
        }
    }

    private void chargerInfos() {
        try {
            User currentUser = sessionManager.getUtilisateurConnecte();

            if (currentUser == null || currentUser.getId() == null) {
                return;
            }

            List<Cours> mesCours = coursService.getCoursByEnseignantId(currentUser.getId());
            List<Seance> mesSeances = seanceService.getSeancesByEnseignantId(currentUser.getId());

            coursLabels.clear();
            for (Cours cours : mesCours) {
                if (cours.getId() != null) {
                    coursLabels.put(cours.getId(), cours.getCode() + " - " + cours.getIntitule());
                }
            }

            setLabel(bienvenuLabel, "Bienvenue, " + currentUser.getNomComplet());
            setLabel(mesCoursCountLabel, String.valueOf(mesCours.size()));
            setLabel(mesSeancesCountLabel, String.valueOf(mesSeances.size()));
            setLabel(seancesEnAttenteCountLabel, String.valueOf(
                    mesSeances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count()
            ));
            setLabel(seancesValideesCountLabel, String.valueOf(
                    mesSeances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count()
            ));
            setLabel(seancesRejeteesCountLabel, String.valueOf(
                    mesSeances.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count()
            ));

            if (mesCoursTable != null) {
                mesCoursTable.setItems(FXCollections.observableArrayList(mesCours));
            }

            if (dernieresSeancesTable != null) {
                List<Seance> recentes = mesSeances.stream()
                        .sorted(Comparator
                                .comparing(Seance::getDateSeance, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Seance::getHeureSeance, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(10)
                        .toList();
                dernieresSeancesTable.setItems(FXCollections.observableArrayList(recentes));
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger le dashboard enseignant.", e);
        }
    }

    private void setLabel(Label label, String value) {
        if (label != null) {
            label.setText(value != null ? value : "");
        }
    }
}
