package yoanemoudilou.cahiertexte;

import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.StatutSeance;
import yoanemoudilou.cahiertexte.repository.SeanceRepository;
import yoanemoudilou.cahiertexte.service.SeanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SeanceServiceTest {

    private FakeSeanceRepository seanceRepository;
    private SeanceService seanceService;

    @BeforeEach
    void setUp() {
        seanceRepository = new FakeSeanceRepository();
        seanceService = new SeanceService(seanceRepository);
    }

    @Test
    void creerSeance_shouldSetStatusEnAttente_whenStatusIsNull() {
        Seance seance = new Seance(
                10, 20,
                LocalDate.now(),
                LocalTime.of(8, 0),
                120,
                "POO - héritage",
                "RAS",
                null,
                null
        );

        Seance saved = seanceService.creerSeance(seance);

        assertNotNull(saved.getId());
        assertEquals(StatutSeance.EN_ATTENTE, saved.getStatut());
    }

    @Test
    void validerSeance_shouldUpdateStatus() {
        Seance seance = new Seance(
                1, 2,
                LocalDate.now(),
                LocalTime.of(10, 0),
                60,
                "Collections Java",
                "",
                StatutSeance.EN_ATTENTE,
                null
        );

        Seance saved = seanceRepository.save(seance);

        boolean ok = seanceService.validerSeance(saved.getId(), "Bon contenu");
        assertTrue(ok);

        Seance updated = seanceRepository.findById(saved.getId()).orElseThrow();
        assertEquals(StatutSeance.VALIDEE, updated.getStatut());
        assertEquals("Bon contenu", updated.getCommentaireValidation());
    }

    @Test
    void getSeancesParPeriode_shouldReturnOnlyMatchingSeances() {
        seanceRepository.save(new Seance(1, 1, LocalDate.of(2026, 3, 1), LocalTime.of(8, 0), 60, "A", "", StatutSeance.EN_ATTENTE, null));
        seanceRepository.save(new Seance(1, 1, LocalDate.of(2026, 3, 5), LocalTime.of(8, 0), 60, "B", "", StatutSeance.EN_ATTENTE, null));
        seanceRepository.save(new Seance(1, 1, LocalDate.of(2026, 3, 10), LocalTime.of(8, 0), 60, "C", "", StatutSeance.EN_ATTENTE, null));

        var result = seanceService.getSeancesParPeriode(
                LocalDate.of(2026, 3, 2),
                LocalDate.of(2026, 3, 8)
        );

        assertEquals(1, result.size());
        assertEquals("B", result.get(0).getContenu());
    }

    private static class FakeSeanceRepository implements SeanceRepository {
        private final Map<Integer, Seance> data = new HashMap<>();
        private int sequence = 1;

        @Override
        public Seance save(Seance seance) {
            if (seance.getId() == null) {
                seance.setId(sequence++);
            }
            data.put(seance.getId(), seance);
            return seance;
        }

        @Override public boolean update(Seance seance) { data.put(seance.getId(), seance); return true; }
        @Override public boolean deleteById(Integer id) { return data.remove(id) != null; }
        @Override public Optional<Seance> findById(Integer id) { return Optional.ofNullable(data.get(id)); }
        @Override public List<Seance> findAll() { return new ArrayList<>(data.values()); }
        @Override public List<Seance> findByCoursId(Integer coursId) { return data.values().stream().filter(s -> Objects.equals(s.getCoursId(), coursId)).toList(); }
        @Override public List<Seance> findByEnseignantId(Integer enseignantId) { return data.values().stream().filter(s -> Objects.equals(s.getEnseignantId(), enseignantId)).toList(); }
        @Override public List<Seance> findByClasseId(Integer classeId) { return List.of(); }
        @Override public List<Seance> findByStatut(StatutSeance statut) { return data.values().stream().filter(s -> s.getStatut() == statut).toList(); }
        @Override public List<Seance> findByDateBetween(LocalDate debut, LocalDate fin) {
            return data.values().stream()
                    .filter(s -> !s.getDateSeance().isBefore(debut) && !s.getDateSeance().isAfter(fin))
                    .sorted(Comparator.comparing(Seance::getDateSeance))
                    .toList();
        }
        @Override public boolean updateStatut(Integer seanceId, StatutSeance statut, String commentaireValidation) {
            Seance s = data.get(seanceId);
            if (s == null) return false;
            s.setStatut(statut);
            s.setCommentaireValidation(commentaireValidation);
            return true;
        }
    }
}