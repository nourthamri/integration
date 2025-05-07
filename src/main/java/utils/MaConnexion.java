package utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;

public class MaConnexion {
    private static MaConnexion instance;
    private Connection cnx;

    private final String URL = "jdbc:mysql://localhost:3306/db_name";
    private final String USER = "root";
    private final String PASSWORD = "";
    private static final String BAD_WORDS_API_KEY = "Gm8cf947SAxv3d4KJgDNng==pc4y8RDZd0uouYG4"; // Remplacez par votre clé

    private MaConnexion() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à la base de données établie");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
        }
    }

    public static MaConnexion getInstance() {
        if (instance == null) {
            instance = new MaConnexion();
        }
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }

    // Ajoutez cette méthode pour le filtrage
    public static boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) return false;

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.api-ninjas.com/v1/profanityfilter")) // URL de l'API
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + BAD_WORDS_API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("{\"text\":\"%s\"}", text.replace("\"", "\\\""))))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().contains("\"contains_bad_words\":true");
        } catch (Exception e) {
            System.err.println("Erreur API filtrage: " + e.getMessage());
            return false; // En cas d'erreur, on laisse passer
        }
    }
}