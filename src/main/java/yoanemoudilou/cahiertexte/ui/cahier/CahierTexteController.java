package yoanemoudilou.cahiertexte.ui.cahier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.CahierTexte;
import yoanemoudilou.cahiertexte.model.Classe;
import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.service.CahierTexteService;
import yoanemoudilou.cahiertexte.service.ClasseService;
import yoanemoudilou.cahiertexte.service.CoursService;
import yoanemoudilou.cahiertexte.service.SeanceService;
import yoanemoudilou.cahiertexte.service.UserService;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import yoanemoudilou.cahiertexte.utils.AppNavigator;
import yoanemoudilou.cahiertexte.utils.DateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controleur de consultation des cahiers de texte.
 */
public class CahierTexteController {

    @FXML
    private Label titreLabel;

    @FXML
    private ComboBox<Classe> classeFilterComboBox;

    @FXML
    private Label detailsLabel;

    @FXML
    private TableView<CahierTexte> cahiersTable;

    @FXML
    private TableColumn<CahierTexte, String> cahierClasseColumn;

    @FXML
    private TableColumn<CahierTexte, String> cahierAnneeColumn;

    @FXML
    private TableColumn<CahierTexte, String> cahierSemestreColumn;

    @FXML
    private TableColumn<CahierTexte, String> cahierDateCreationColumn;

    @FXML
    private TableView<Seance> seancesTable;

    @FXML
    private TableColumn<Seance, String> seanceDateColumn;

    @FXML
    private TableColumn<Seance, String> seanceHeureColumn;

    @FXML
    private TableColumn<Seance, String> seanceCoursColumn;

    @FXML
    private TableColumn<Seance, String> seanceEnseignantColumn;

    @FXML
    private TableColumn<Seance, String> seanceStatutColumn;

    @FXML
    private TableColumn<Seance, String> seanceContenuColumn;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final CahierTexteService cahierTexteService = new CahierTexteService();
    private final ClasseService classeService = new ClasseService();
    private final CoursService coursService = new CoursService();
    private final SeanceService seanceService = new SeanceService();
    private final UserService userService = new UserService();

    private final Map<Integer, Classe> classesById = new HashMap<>();
    private final Map<Integer, String> coursLabels = new HashMap<>();
    private final Map<Integer, String> enseignantsLabels = new HashMap<>();

    private Classe classeRestreinte;

    @FXML
    private void initialize() {
        configurerTables();
        configurerFiltreClasse();
        chargerReferences();
        chargerCahiers();
        ecouterSelectionCahier();
    }

    @FXML
    private void handleFiltrer() {
        chargerCahiers();
    }

    @FXML
    private void handleRafraichir() {
        chargerReferences();
        chargerCahiers();
    }

    @FXML
    private void handleRetourDashboard(ActionEvent event) {
        AppNavigator.goToDashboardForCurrentUser();
    }

    private void configurerTables() {
        if (cahierClasseColumn != null) {
            cahierClasseColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    getClasseLabel(data.getValue().getClasseId()))
            );
        }
        if (cahierAnneeColumn != null) {
            cahierAnneeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAnneeScolaire()));
        }
        if (cahierSemestreColumn != null) {
            cahierSemestreColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    data.getValue().getSemestre() != null ? data.getValue().getSemestre().name() : "")
            );
        }
        if (cahierDateCreationColumn != null) {
            cahierDateCreationColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    DateUtils.formatDateTime(data.getValue().getDateCreation()))
            );
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
        if (seanceEnseignantColumn != null) {
            seanceEnseignantColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                    enseignantsLabels.getOrDefault(data.getValue().getEnseignantId(), "Enseignant #" + data.getValue().getEnseignantId()))
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

    private void configurerFiltreClasse() {
        if (classeFilterComboBox == null) {
            return;
        }

        classeFilterComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Classe classe) {
                return classe == null ? "Toutes les classes" : classe.getNomClasse() + " - " + classe.getNiveau();
            }

            @Override
            public Classe fromString(String string) {
                return null;
            }
        });
    }

    private void chargerReferences() {
        try {
            classesById.clear();
            for (Classe classe : classeService.getAllClasses()) {
                if (classe.getId() != null) {
                    classesById.put(classe.getId(), classe);
                }
            }

            coursLabels.clear();
            for (Cours cours : coursService.getAllCours()) {
                if (cours.getId() != null) {
                    String classeLabel = getClasseLabel(cours.getClasseId());
                    coursLabels.put(cours.getId(), cours.getCode() + " - " + cours.getIntitule() + " (" + classeLabel + ")");
                }
            }

            enseignantsLabels.clear();
            for (User user : userService.getUtilisateursByRole(Role.ENSEIGNANT)) {
                if (user.getId() != null) {
                    enseignantsLabels.put(user.getId(), user.getNomComplet());
                }
            }

            User currentUser = sessionManager.getUtilisateurConnecte();
            classeRestreinte = currentUser instanceof ResponsableClasse responsableClasse
                    ? responsableClasse.getClasse()
                    : null;

            if (titreLabel != null) {
                titreLabel.setText(classeRestreinte != null ? "Cahier de texte de ma classe" : "Cahiers de texte");
            }

            if (classeFilterComboBox != null) {
                if (classeRestreinte != null) {
                    classeFilterComboBox.setItems(FXCollections.observableArrayList(classeRestreinte));
                    classeFilterComboBox.setValue(classeRestreinte);
                    classeFilterComboBox.setDisable(true);
                } else {
                    classeFilterComboBox.setItems(FXCollections.observableArrayList(classesById.values()));
                    classeFilterComboBox.setValue(null);
                    classeFilterComboBox.setDisable(false);
                }
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger les references du cahier de texte.", e);
        }
    }

    private void chargerCahiers() {
        try {
            List<CahierTexte> cahiers;
            Classe classeFiltre = classeRestreinte != null ? classeRestreinte
                    : (classeFilterComboBox != null ? classeFilterComboBox.getValue() : null);

            if (classeFiltre != null && classeFiltre.getId() != null) {
                cahiers = cahierTexteService.getCahiersByClasseId(classeFiltre.getId());
            } else {
                cahiers = cahierTexteService.getAllCahiers();
            }

            if (cahiersTable != null) {
                cahiersTable.setItems(FXCollections.observableArrayList(cahiers));
                if (!cahiers.isEmpty()) {
                    cahiersTable.getSelectionModel().selectFirst();
                } else {
                    detailsLabel.setText("Aucun cahier de texte disponible.");
                    if (seancesTable != null) {
                        seancesTable.setItems(FXCollections.emptyObservableList());
                    }
                }
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger les cahiers de texte.", e);
        }
    }

    private void ecouterSelectionCahier() {
        if (cahiersTable != null) {
            cahiersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> chargerSeances(newValue));
        }
    }

    private void chargerSeances(CahierTexte cahierTexte) {
        try {
            if (cahierTexte == null || cahierTexte.getId() == null) {
                detailsLabel.setText("Selectionne un cahier de texte.");
                if (seancesTable != null) {
                    seancesTable.setItems(FXCollections.emptyObservableList());
                }
                return;
            }

            List<Seance> seances = seanceService.getSeancesByCahierTexteId(cahierTexte.getId());
            String classeLabel = getClasseLabel(cahierTexte.getClasseId());
            detailsLabel.setText(
                    "Classe : " + classeLabel
                            + " | Annee : " + cahierTexte.getAnneeScolaire()
                            + " | Semestre : " + (cahierTexte.getSemestre() != null ? cahierTexte.getSemestre().name() : "")
                            + " | Seances : " + seances.size()
            );

            if (seancesTable != null) {
                seancesTable.setItems(FXCollections.observableArrayList(seances));
            }
        } catch (Exception e) {
            AlertUtils.showException("Erreur", "Impossible de charger les seances du cahier.", e);
        }
    }

    private String getClasseLabel(Integer classeId) {
        Classe classe = classeId != null ? classesById.get(classeId) : null;
        return classe != null ? classe.getNomClasse() + " - " + classe.getNiveau() : "Classe #" + classeId;
    }
}
