package yoanemoudilou.cahiertexte.service;

import yoanemoudilou.cahiertexte.model.Cours;
import yoanemoudilou.cahiertexte.model.NotificationApp;
import yoanemoudilou.cahiertexte.model.Role;
import yoanemoudilou.cahiertexte.model.Seance;
import yoanemoudilou.cahiertexte.model.User;
import yoanemoudilou.cahiertexte.repository.NotificationRepository;
import yoanemoudilou.cahiertexte.repository.impl.NotificationRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service metier pour les notifications internes.
 */
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final CoursService coursService;

    public NotificationService() {
        this(new NotificationRepositoryImpl(), new UserService(), new CoursService());
    }

    public NotificationService(NotificationRepository notificationRepository, UserService userService, CoursService coursService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.coursService = coursService;
    }

    public NotificationApp creerNotification(Integer destinataireId, String titre, String message) {
        if (destinataireId == null) {
            throw new IllegalArgumentException("Le destinataire est requis.");
        }

        try {
            return notificationRepository.save(
                    new NotificationApp(destinataireId, titre, message, false, LocalDateTime.now())
            );
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de la notification.", e);
        }
    }

    public List<NotificationApp> getNotificationsPourUtilisateur(Integer userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return notificationRepository.findByDestinataireId(userId, limit);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recuperation des notifications.", e);
        }
    }

    public long countNotificationsNonLues(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'id utilisateur est requis.");
        }

        try {
            return notificationRepository.countUnreadByDestinataireId(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des notifications.", e);
        }
    }

    public void marquerToutesCommeLues(Integer userId) {
        if (userId == null) {
            return;
        }

        try {
            notificationRepository.markAllAsRead(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour des notifications.", e);
        }
    }

    public void notifierCoursAttribue(Integer enseignantId, Integer coursId) {
        Cours cours = chargerCours(coursId);
        String libelleCours = cours.getCode() + " - " + cours.getIntitule();
        creerNotification(
                enseignantId,
                "Nouveau cours attribue",
                "Le cours " + libelleCours + " t'a ete attribue."
        );
    }

    public void notifierResponsablePourVerification(Seance seance) {
        Cours cours = chargerCours(seance.getCoursId());
        if (cours.getClasseId() == null) {
            return;
        }

        Optional<User> responsable = userService.getResponsableByClasseId(cours.getClasseId());
        if (responsable.isEmpty() || responsable.get().getId() == null) {
            return;
        }

        String libelleCours = cours.getCode() + " - " + cours.getIntitule();
        creerNotification(
                responsable.get().getId(),
                "Seance a verifier",
                "Une seance du cours " + libelleCours + " a ete envoyee pour verification."
        );
    }

    public void notifierAdminsSeanceValidee(Seance seance, User responsable) {
        Cours cours = chargerCours(seance.getCoursId());
        String libelleCours = cours.getCode() + " - " + cours.getIntitule();
        String auteur = responsable != null ? responsable.getNomComplet() : "Le responsable";

        for (User admin : userService.getUtilisateursByRole(Role.CHEF_DEPARTEMENT)) {
            if (admin.getId() != null) {
                creerNotification(
                        admin.getId(),
                        "Seance validee",
                        auteur + " a valide la seance du cours " + libelleCours + "."
                );
            }
        }
    }

    private Cours chargerCours(Integer coursId) {
        return coursService.getCoursById(coursId)
                .orElseThrow(() -> new IllegalArgumentException("Cours introuvable pour la notification."));
    }
}
