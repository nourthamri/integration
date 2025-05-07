package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BadWordsFilter {
    private static final String API_KEY = "Gm8cf947SAxv3d4KJgDNng==pc4y8RDZd0uouYG4";
    private static final String API_URL = "https://api.api-ninjas.com/v1/profanityfilter";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) return false;

        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?text=" + encodedText))
                    .header("X-Api-Key", API_KEY)
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logDebug("API Response", response.body());

            return parseApiResponse(response.body());
        } catch (Exception e) {
            logError("API Error", e.toString());
            return false; // Ne pas bloquer l'application en cas d'erreur
        }
    }

    private static boolean parseApiResponse(String json) {
        try {
            JsonObject response = JsonParser.parseString(json).getAsJsonObject();
            return response.get("has_profanity").getAsBoolean();
        } catch (Exception e) {
            logError("JSON Parsing Error", e.toString());
            return false;
        }
    }

    private static void logDebug(String context, String message) {
        System.out.println("[BadWordsFilter] " + context + ": " + message);
    }

    private static void logError(String context, String message) {
        System.err.println("[BadWordsFilter] ERROR in " + context + ": " + message);
    }
}