package yoanemoudilou.cahiertexte.repository;

import yoanemoudilou.cahiertexte.model.NotificationApp;

import java.sql.SQLException;
import java.util.List;

public interface NotificationRepository {

    NotificationApp save(NotificationApp notification) throws SQLException;

    List<NotificationApp> findByDestinataireId(Integer destinataireId, int limit) throws SQLException;

    long countUnreadByDestinataireId(Integer destinataireId) throws SQLException;

    boolean markAllAsRead(Integer destinataireId) throws SQLException;
}
