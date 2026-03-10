package yoanemoudilou.cahiertexte.ui.stats;

import yoanemoudilou.cahiertexte.model.*;
import yoanemoudilou.cahiertexte.service.*;
import yoanemoudilou.cahiertexte.utils.AlertUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur de statistiques.
 */
public class StatistiqueController {

    // --- Filtres ---
    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private ComboBox<Classe> classeFilterComboBox;

    @FXML
    private ComboBox<User> enseignantFilterComboBox;

    // --- Compteurs globaux ---
    @FXML
    private Label totalUtilisateursLabel;

    @FXML
    private Label totalEnseignantsLabel;

    @FXML
    private Label totalCoursLabel;

    @FXML
    private Label totalSeancesLabel;

    @FXML
    private Label totalFilieresLabel;

    @FXML
    private Label totalClassesLabel;

    // --- Graphique statut séances ---
    @FXML
    private PieChart statutPieChart;

    // --- Tableau séances par enseignant ---
    @FXML
    private TableView<Map.Entry<String, long[]>> statsEnseignantTable;

    @FXML
    private TableColumn<Map.Entry<String, long[]>, String> enseignantNomColumn;

    @FXML
    private TableColumn<Map.Entry<String, long[]>, Long> enseignantTotalColumn;

    @FXML
    private TableColumn<Map.Entry<String, long[]>, Long> enseignantValideesColumn;

    @FXML
    private TableColumn<Map.Entry<String, long[]>, Long> enseignantAttenteColumn;

    @FXML
    private TableColumn<Map.Entry<String, long[]>, Long> enseignantRejeteesColumn;

    // --- Services ---
    private final UserService userService = new UserService();
    private final CoursService coursService = new CoursService();
    private final SeanceService seanceService = new SeanceService();
    private final FiliereService filiereService = new FiliereService();
    private final ClasseService classeService = new ClasseService();

    private final Map<Integer, String> enseignantNoms = new HashMap<>();

    // ===================
    // INITIALISATION
    // ===================

    @FXML
    private void initialize() {
        chargerFiltres();
        configurerTableEnseignant();
        chargerStatistiques();
    }

    // ===================
    // HANDLERS FXML
    // ===================

    @FXML
    private void handleFiltrer() {
        chargerStatistiques();
    }

    @FXML
    private void handleFiltrer(ActionEvent event) {
        chargerStatistiques();
    }

    @FXML
    private void handleReinitialiserFiltres() {
        reinitialiserFiltres();
    }

    @FXML
    private void handleReinitialiserFiltres(ActionEvent event) {
        reinitialiserFiltres();
    }

    @FXML
    private void handleRafraichir() {
        chargerFiltres();
        chargerStatistiques();
    }

    @FXML
    private void handleRafraichir(ActionEvent event) {
        chargerFiltres();
        chargerStatistiques();
    }

    // ===================
    // MÉTHODES PRIVÉES
    // ===================

    private void reinitialiserFiltres() {
        if (dateDebutPicker != null) {
            dateDebutPicker.setValue(null);
        }
        if (dateFinPicker != null) {
            dateFinPicker.setValue(null);
        }
        if (classeFilterComboBox != null) {
            classeFilterComboBox.setValue(null);
        }
        if (enseignantFilterComboBox != null) {
            enseignantFilterComboBox.setValue(null);
        }
        chargerStatistiques();
    }

    private void chargerFiltres() {
        try {
            List<Classe> classes = classeService.getAllClasses();
            List<User> enseignants = userService.getUtilisateursByRole(Role.ENSEIGNANT);

            enseignantNoms.clear();
            for (User u : enseignants) {
                if (u.getId() != null) {
                    enseignantNoms.put(u.getId(), u.getNomComplet());
                }
            }

            if (classeFilterComboBox != null) {
                classeFilterComboBox.setItems(FXCollections.observableArrayList(classes));
                classeFilterComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Classe c) {
                        return c == null ? "Toutes les classes" : c.getNomClasse() + " - " + c.getNiveau();
                    }

                    @Override
                    public Classe fromString(String s) {
                        return null;
                    }
                });
            }

            if (enseignantFilterComboBox != null) {
                enseignantFilterComboBox.setItems(FXCollections.observableArrayList(enseignants));
                enseignantFilterComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(User u) {
                        return u == null ? "Tous les enseignants" : u.getNomComplet();
                    }

                    @Override
                    public User fromString(String s) {
                        return null;
                    }
                });
            }

        } catch (Exception e) {
            AlertUtils.showError("Erreur", "Impossible de charger les filtres : " ,e.getMessage());
        }
    }

    private void configurerTableEnseignant() {
        if (enseignantNomColumn != null) {
            enseignantNomColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getKey()));
        }
        if (enseignantTotalColumn != null) {
            enseignantTotalColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getValue()[0]));
        }
        if (enseignantValideesColumn != null) {
            enseignantValideesColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getValue()[1]));
        }
        if (enseignantAttenteColumn != null) {
            enseignantAttenteColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getValue()[2]));
        }
        if (enseignantRejeteesColumn != null) {
            enseignantRejeteesColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getValue()[3]));
        }
    }

    private void chargerStatistiques() {
        try {
            // Compteurs globaux
            setLabel(totalUtilisateursLabel, String.valueOf(userService.getAllUtilisateurs().size()));
            setLabel(totalEnseignantsLabel, String.valueOf(userService.getUtilisateursByRole(Role.ENSEIGNANT).size()));
            setLabel(totalCoursLabel, String.valueOf(coursService.getAllCours().size()));
            setLabel(totalFilieresLabel, String.valueOf(filiereService.getAllFilieres().size()));
            setLabel(totalClassesLabel, String.valueOf(classeService.getAllClasses().size()));

            // Séances filtrées
            List<Seance> seances = getSeancesFiltrees();
            setLabel(totalSeancesLabel, String.valueOf(seances.size()));

            // PieChart statuts
            chargerPieChart(seances);

            // Tableau par enseignant
            chargerTableEnseignant(seances);

        } catch (Exception e) {
            AlertUtils.showError("Erreur", "Impossible de charger les statistiques : " ,e.getMessage());
        }
    }

    private List<Seance> getSeancesFiltrees() {
        LocalDate debut = dateDebutPicker != null ? dateDebutPicker.getValue() : null;
        LocalDate fin = dateFinPicker != null ? dateFinPicker.getValue() : null;
        Classe classe = classeFilterComboBox != null ? classeFilterComboBox.getValue() : null;
        User enseignant = enseignantFilterComboBox != null ? enseignantFilterComboBox.getValue() : null;

        List<Seance> seances;

        // Récupération initiale selon les filtres principaux
        if (debut != null && fin != null) {
            seances = seanceService.getSeancesParPeriode(debut, fin);
        } else if (classe != null && classe.getId() != null) {
            seances = seanceService.getSeancesByClasseId(classe.getId());
        } else if (enseignant != null && enseignant.getId() != null) {
            seances = seanceService.getSeancesByEnseignantId(enseignant.getId());
        } else {
            seances = seanceService.getAllSeances();
        }

        // Filtrage supplémentaire par enseignant si période sélectionnée
        if (enseignant != null && enseignant.getId() != null && debut != null) {
            seances = seances.stream()
                    .filter(s -> enseignant.getId().equals(s.getEnseignantId()))
                    .toList();
        }

        // Filtrage supplémentaire par classe si période sélectionnée
        if (classe != null && classe.getId() != null && debut != null) {
            List<Cours> coursClasse = coursService.getCoursByClasseId(classe.getId());
            List<Integer> coursIds = coursClasse.stream().map(Cours::getId).toList();
            seances = seances.stream()
                    .filter(s -> coursIds.contains(s.getCoursId()))
                    .toList();
        }

        return seances;
    }

    private void chargerPieChart(List<Seance> seances) {
        if (statutPieChart == null) {
            return;
        }

        long enAttente = seances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count();
        long validees = seances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
        long rejetees = seances.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("En attente (" + enAttente + ")", enAttente),
                new PieChart.Data("Validées (" + validees + ")", validees),
                new PieChart.Data("Rejetées (" + rejetees + ")", rejetees)
        );

        statutPieChart.setData(data);
        statutPieChart.setTitle("Répartition des séances par statut");
    }

    private void chargerTableEnseignant(List<Seance> seances) {
        if (statsEnseignantTable == null) {
            return;
        }

        // Regrouper par enseignant
        Map<String, long[]> stats = new HashMap<>();

        for (Seance seance : seances) {
            String nom = enseignantNoms.getOrDefault(
                    seance.getEnseignantId(),
                    "Enseignant #" + seance.getEnseignantId()
            );

            stats.computeIfAbsent(nom, k -> new long[4]);
            long[] compteurs = stats.get(nom);

            compteurs[0]++; // total

            if (seance.getStatut() == StatutSeance.VALIDEE) {
                compteurs[1]++;
            } else if (seance.getStatut() == StatutSeance.EN_ATTENTE) {
                compteurs[2]++;
            } else if (seance.getStatut() == StatutSeance.REJETEE) {
                compteurs[3]++;
            }
        }

        statsEnseignantTable.setItems(FXCollections.observableArrayList(stats.entrySet()));
    }

    private void setLabel(Label label, String text) {
        if (label != null) {
            label.setText(text != null ? text : "0");
        }
    }
}