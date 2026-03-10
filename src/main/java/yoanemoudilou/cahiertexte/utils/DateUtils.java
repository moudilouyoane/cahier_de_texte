package yoanemoudilou.cahiertexte.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaire pour la gestion des dates et heures.
 * Compatible avec Java moderne et JDBC/MySQL.
 */
public final class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtils() {
        // Empêche l'instanciation.
    }

    /**
     * Formate une date au format jj/MM/aaaa.
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Formate une heure au format HH:mm.
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    /**
     * Formate une date-heure au format jj/MM/aaaa HH:mm.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    /**
     * Parse une date au format jj/MM/aaaa.
     */
    public static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    /**
     * Parse une heure au format HH:mm.
     */
    public static LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalTime.parse(value, TIME_FORMATTER);
    }

    /**
     * Combine une date et une heure.
     */
    public static LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return null;
        }
        return LocalDateTime.of(date, time);
    }

    /**
     * Convertit LocalDate vers java.sql.Date.
     */
    public static java.sql.Date toSqlDate(LocalDate date) {
        return date != null ? java.sql.Date.valueOf(date) : null;
    }

    /**
     * Convertit LocalTime vers java.sql.Time.
     */
    public static Time toSqlTime(LocalTime time) {
        return time != null ? Time.valueOf(time) : null;
    }

    /**
     * Convertit LocalDateTime vers java.sql.Timestamp.
     */
    public static Timestamp toSqlTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    /**
     * Convertit java.sql.Date vers LocalDate.
     */
    public static LocalDate toLocalDate(java.sql.Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    /**
     * Convertit java.sql.Time vers LocalTime.
     */
    public static LocalTime toLocalTime(Time time) {
        return time != null ? time.toLocalTime() : null;
    }

    /**
     * Convertit java.sql.Timestamp vers LocalDateTime.
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
