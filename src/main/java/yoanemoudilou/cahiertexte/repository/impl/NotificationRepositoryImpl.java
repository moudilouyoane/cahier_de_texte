package yoanemoudilou.cahiertexte.repository.impl;

import yoanemoudilou.cahiertexte.config.DatabaseConnection;
import yoanemoudilou.cahiertexte.model.NotificationApp;
import yoanemoudilou.cahiertexte.repository.NotificationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    private static final String INSERT_SQL =
            "INSERT INTO notifications (destinataire_id, titre, message, lue, date_creation) VALUES (?, ?, ?, ?, ?)";

    @Override
    public NotificationApp save(NotificationApp notification) throws SQLException {
        if (notification == null) {
            throw new IllegalArgumentException("La notification est requise.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, notification.getDestinataireId());
            ps.setString(2, notification.getTitre());
            ps.setString(3, notification.getMessage());
            ps.setBoolean(4, notification.isLue());
            ps.setTimestamp(5, notification.getDateCreation() != null ? Timestamp.valueOf(notification.getDateCreation()) : null);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    notification.setId(rs.getInt(1));
                }
            }

            return notification;
        }
    }

    @Override
    public List<NotificationApp> findByDestinataireId(Integer destinataireId, int limit) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE destinataire_id = ? ORDER BY date_creation DESC, id DESC LIMIT ?";
        List<NotificationApp> notifications = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, destinataireId);
            ps.setInt(2, Math.max(limit, 1));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }
        }

        return notifications;
    }

    @Override
    public long countUnreadByDestinataireId(Integer destinataireId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE destinataire_id = ? AND lue = FALSE";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, destinataireId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    @Override
    public boolean markAllAsRead(Integer destinataireId) throws SQLException {
        String sql = "UPDATE notifications SET lue = TRUE WHERE destinataire_id = ? AND lue = FALSE";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, destinataireId);
            return ps.executeUpdate() >= 0;
        }
    }

    private NotificationApp mapNotification(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("date_creation");
        return new NotificationApp(
                rs.getInt("id"),
                rs.getObject("destinataire_id", Integer.class),
                rs.getString("titre"),
                rs.getString("message"),
                rs.getBoolean("lue"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
