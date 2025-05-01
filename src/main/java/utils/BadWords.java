package utils;

import java.net.*;
import java.net.http.*;
import java.time.Duration;
import com.google.gson.*;

public class BadWords {
    // Configuration RapidAPI
    private static final String API_KEY = "01789be857msh14db8bb96e784afp132154jsnf661f2a39d10";
    private static final String API_HOST = "profanity-filter-by-api-ninjas.p.rapidapi.com";
    private static final String API_URL = "https://profanity-filter-by-api-ninjas.p.rapidapi.com/v1/profanityfilter";

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) return false;

        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?text=" + encodedText))
                    .header("x-rapidapi-key", API_KEY)
                    .header("x-rapidapi-host", API_HOST)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            logDebug("API Response Code", String.valueOf(response.statusCode()));
            logDebug("API Response Body", response.body());

            return parseApiResponse(response.body());
        } catch (Exception e) {
            logError("API Error", "Erreur lors de la vérification des gros mots: " + e.getMessage());
            return false;
        }
    }

    private static boolean parseApiResponse(String json) {
        try {
            JsonObject response = JsonParser.parseString(json).getAsJsonObject();
            return response.has("has_profanity") && response.get("has_profanity").getAsBoolean();
        } catch (Exception e) {
            logError("JSON Error", "Format de réponse inattendu: " + json);
            return false;
        }
    }

    // Méthodes de logging améliorées
    private static void logDebug(String context, String message) {
        System.out.println("[Reclamation Filter] DEBUG - " + context + " || " + message);
    }

    private static void logError(String context, String message) {
        System.err.println("[Reclamation Filter] ERREUR - " + context + " || " + message);
    }
}