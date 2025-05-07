package services;

import org.json.JSONObject;
import utils.ApiClient;


public class TranslationService {
    private static final String API_URL = "https://api.mymemory.translated.net/get";

    public String translateText(String text, String targetLanguage) {
        try {
            // Construire l'URL de requête
            String url = String.format("%s?q=%s&langpair=en|%s",
                    API_URL,
                    ApiClient.encodeValue(text),
                    targetLanguage
            );

            // Envoyer la requête HTTP GET
            String response = ApiClient.sendGetRequest(url);

            // Parser la réponse JSON
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getJSONObject("responseData").getString("translatedText");
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur de traduction : " + e.getMessage();
        }
    }
}