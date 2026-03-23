package yoanemoudilou.cahiertexte;

import yoanemoudilou.cahiertexte.config.SessionManager;
import yoanemoudilou.cahiertexte.model.Enseignant;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.ResponsableClasse;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.repository.UserRepository;
import yoanemoudilou.cahiertexte.service.AuthService;
import yoanemoudilou.cahiertexte.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private FakeUserRepository userRepository;
    private SessionManager sessionManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        sessionManager = SessionManager.getInstance();
        sessionManager.fermerSession();
        authService = new AuthService(userRepository, sessionManager);

        Enseignant user = new Enseignant();
        user.setId(1);
        user.setNom("Doe");
        user.setPrenom("John");
        user.setEmail("john@test.com");
        user.setMotDePasse(PasswordUtils.hashPassword("secret123"));
        user.setRole(Role.ENSEIGNANT);
        user.setValide(true);
        user.setActif(true);

        userRepository.store(user);
    }

    @Test
    void authenticate_shouldReturnUser_whenCredentialsAreValid() {
        Optional<User> result = authService.authenticate("john@test.com", "secret123");
        assertTrue(result.isPresent());
        assertEquals("john@test.com", result.get().getEmail());
    }

    @Test
    void authenticate_shouldReturnEmpty_whenPasswordIsInvalid() {
        Optional<User> result = authService.authenticate("john@test.com", "wrong-password");
        assertTrue(result.isEmpty());
    }

    @Test
    void login_shouldOpenSession_whenCredentialsAreValid() {
        boolean logged = authService.login("john@test.com", "secret123");

        assertTrue(logged);
        assertNotNull(sessionManager.getUtilisateurConnecte());
        assertEquals("john@test.com", sessionManager.getUtilisateurConnecte().getEmail());
    }

    @Test
    void inscrire_shouldCreateUserWithNormalizedEmailAndHashedPassword() {
        Enseignant user = new Enseignant();
        user.setId(2);
        user.setNom("Dupont");
        user.setPrenom("Alice");
        user.setEmail("  ALICE@TEST.COM ");
        user.setMotDePasse("motdepasse");
        user.setRole(Role.ENSEIGNANT);
        user.setValide(false);
        user.setActif(true);

        User created = authService.inscrire(user);

        assertEquals("alice@test.com", created.getEmail());
        assertNotEquals("motdepasse", created.getMotDePasse());
        assertTrue(PasswordUtils.verifyPassword("motdepasse", created.getMotDePasse()));
    }

    @Test
    void inscrire_shouldRejectDuplicateEmail() {
        ResponsableClasse user = new ResponsableClasse();
        user.setNom("Doe");
        user.setPrenom("Jane");
        user.setEmail("JOHN@test.com");
        user.setMotDePasse("secret123");
        user.setRole(Role.RESPONSABLE_CLASSE);
        user.setValide(false);
        user.setActif(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.inscrire(user)
        );

        assertEquals("Un utilisateur avec cet email existe dÃ©jÃ .", exception.getMessage());
    }

    private static class FakeUserRepository implements UserRepository {
        private final Map<Integer, User> byId = new HashMap<>();
        private final Map<String, User> byEmail = new HashMap<>();

        void store(User user) {
            byId.put(user.getId(), user);
            byEmail.put(user.getEmail(), user);
        }

        @Override public User save(User user) { store(user); return user; }
        @Override public boolean update(User user) { store(user); return true; }
        @Override public boolean deleteById(Integer id) { return byId.remove(id) != null; }
        @Override public Optional<User> findById(Integer id) { return Optional.ofNullable(byId.get(id)); }
        @Override public Optional<User> findByEmail(String email) { return Optional.ofNullable(byEmail.get(email)); }
        @Override public List<User> findAll() { return new ArrayList<>(byId.values()); }
        @Override public List<User> findByRole(Role role) { return byId.values().stream().filter(u -> u.getRole() == role).toList(); }
        @Override public List<User> findPendingValidation() { return byId.values().stream().filter(u -> !u.isValide()).toList(); }
        @Override public boolean updateValidationStatus(Integer userId, boolean valide) { return true; }
        @Override public boolean updateActiveStatus(Integer userId, boolean actif) { return true; }
    }
}
