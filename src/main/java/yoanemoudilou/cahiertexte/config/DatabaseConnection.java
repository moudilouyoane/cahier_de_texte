package yoanemoudilou.cahiertexte.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère la connexion à la base de données MySQL.
 * Cette classe peut :
 * - charger le driver MySQL ;
 * - créer la base de données si elle n'existe pas ;
 * - fournir une connexion JDBC ;
 * - exécuter automatiquement le script SQL du projet si présent.
 */
public final class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String DB_HOST = getEnvOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = getEnvOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = getEnvOrDefault("DB_NAME", "cahier");
    private static final String DB_USER = getEnvOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = getEnvOrDefault("DB_PASSWORD", "");

    private static final String SERVER_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT +
                    "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String DATABASE_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private DatabaseConnection() {
        // Empêche l'instanciation.
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("Driver MySQL chargé avec succès.");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Impossible de charger le driver MySQL.", e);
        }
    }

    /**
     * Retourne une connexion à la base de données cible.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Initialise la base de données :
     * - création de la base si elle n'existe pas ;
     * - exécution du script SQL si présent dans les resources du projet.
     */
    public static void initializeDatabase() {
        createDatabaseIfNotExists();
        executeSchemaIfPresent();
    }

    /**
     * Vérifie qu'une connexion à la base peut être ouverte.
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Échec du test de connexion à la base de données.", e);
            return false;
        }
    }

    /**
     * Crée la base de données si elle n'existe pas.
     */
    private static void createDatabaseIfNotExists() {
        validateDatabaseName(DB_NAME);

        String sql = "CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` " +
                "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";

        try (Connection connection = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            LOGGER.info("Base de données vérifiée/créée avec succès : " + DB_NAME);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la base de données.", e);
        }
    }

    /**
     * Exécute le script SQL du projet s'il existe dans le classpath.
     */
    private static void executeSchemaIfPresent() {
        InputStream inputStream = DatabaseConnection.class.getResourceAsStream(
                "/yoanemoudilou/cahiertexte/script/gestion_cahier_texte.sql"
        );

        if (inputStream == null) {
            LOGGER.info("Aucun script SQL projet trouvé dans les resources. Initialisation ignorée.");
            return;
        }

        try (Connection connection = getConnection()) {
            executeSqlScript(connection, inputStream);
            LOGGER.info("Script SQL projet exécuté avec succès.");
        } catch (SQLException | IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'exécution du script SQL projet.", e);
        }
    }

    /**
     * Exécute les requêtes SQL contenues dans un fichier.
     */
    private static void executeSqlScript(Connection connection, InputStream inputStream)
            throws IOException, SQLException {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             Statement statement = connection.createStatement()) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();

                // Ignore les lignes vides et les commentaires SQL simples.
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }

                sqlBuilder.append(line).append(" ");

                if (trimmedLine.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    sql = sql.substring(0, sql.length() - 1).trim();

                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }

                    sqlBuilder.setLength(0);
                }
            }

            // Exécute une éventuelle dernière requête sans point-virgule final.
            String remainingSql = sqlBuilder.toString().trim();
            if (!remainingSql.isEmpty()) {
                statement.execute(remainingSql);
            }
        }
    }

    /**
     * Vérifie que le nom de la base de données est sûr.
     */
    private static void validateDatabaseName(String databaseName) {
        if (databaseName == null || !databaseName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Nom de base de données invalide : " + databaseName);
        }
    }

    /**
     * Lit une variable d'environnement ou retourne une valeur par défaut.
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
