package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrat d'accès aux données des utilisateurs.
 */
public interface UserRepository {

    User save(User user) throws SQLException;

    boolean update(User user) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    Optional<User> findById(Integer id) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    List<User> findAll() throws SQLException;

    List<User> findByRole(Role role) throws SQLException;

    Optional<User> findResponsableByClasseId(Integer classeId) throws SQLException;

    List<User> findPendingValidation() throws SQLException;

    boolean updateValidationStatus(Integer userId, boolean valide) throws SQLException;

    boolean updateActiveStatus(Integer userId, boolean actif) throws SQLException;
}
