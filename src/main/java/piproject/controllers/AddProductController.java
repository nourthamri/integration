package piproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import piproject.models.Category;
import piproject.models.Product;
import piproject.services.CategoryService;
import piproject.services.ProductService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AddProductController {

    @FXML
    private TextField nomField;
    @FXML
    public TextField descriptionField; // Ensure public access for debugging
    @FXML
    private TextField couponField;
    @FXML
    private TextField valeurField;
    @FXML
    private TextField etatField;
    @FXML
    private TextField dispoField;
    @FXML
    private ComboBox<Category> categorieCombo;
    @FXML
    private Label statusLabel;
    private String selectedImagePath;
    private CategoryService categoryService;
    private String extractedDescription;

    @FXML
    public void initialize() {
        categoryService = new CategoryService();
        populateCategoryComboBox();
        configureComboBox();
        setupInputFilters();
        setupValidationListeners();
    }

    private void configureComboBox() {
        categorieCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getCategory_name());
            }
        });

        categorieCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getCategory_name());
            }
        });
    }

    private void setupInputFilters() {
        addLetterOnlyFilter(nomField);
        addLetterOnlyFilter(etatField);
        addLetterOnlyFilter(dispoField);

        // Allow letters and spaces for the descriptionField
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z ]*")) { // Allow letters and spaces
                descriptionField.setText(oldVal);
            } else if (newVal.length() > 30) { // Restrict to max 30 characters
                descriptionField.setText(newVal.substring(0, 30));
            }
        });

        addNumericFilter(couponField);
        addNumericFilter(valeurField);
    }

    private void setupValidationListeners() {
        // Adjusted regex for description validation to allow spaces
        addValidationListener(nomField, "^[a-zA-Z]+$");
        addValidationListener(descriptionField, "^[a-zA-Z ]{10,30}$"); // Ensure this matches new max length and allows spaces
        addValidationListener(etatField, "^[a-zA-Z]+$");
        addValidationListener(dispoField, "^[a-zA-Z]+$");
    }

    private void addLetterOnlyFilter(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z]*")) {
                field.setText(oldVal);
            }
        });
    }

    private void addNumericFilter(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) { // Allow numbers, fix if needed
                field.setText(oldVal);
            }
        });
    }

    private void addValidationListener(TextField field, String regex) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.matches(regex)) {
                field.getStyleClass().removeAll("invalid-field");
                field.getStyleClass().add("valid-field");
            } else {
                field.getStyleClass().removeAll("valid-field");
                field.getStyleClass().add("invalid-field");
            }
        });
    }

    private void populateCategoryComboBox() {
        try {
            List<Category> categories = categoryService.getAll();
            categorieCombo.getItems().addAll(categories);
        } catch (Exception e) {
            statusLabel.setText("Erreur de chargement des catégories: " + e.getMessage());
        }
    }

    @FXML
    void onChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            statusLabel.setText("Image sélectionnée!");
        }
    }

    @FXML
    void onAddProduct(ActionEvent event) {
        String nom = nomField.getText().trim();
        String description = extractedDescription != null ? extractedDescription : descriptionField.getText().trim();
        String couponText = couponField.getText().trim();
        String valeurText = valeurField.getText().trim();
        String etat = etatField.getText().trim();
        String dispo = dispoField.getText().trim();
        Category selectedCategory = categorieCombo.getValue();

        if (!validateProductDetails(nom, description, couponText, valeurText, etat, dispo, selectedCategory)) {
            return; // Validation failed
        }

        // All validations passed - create product
        try {
            Product newProduct = new Product(nom, description, Integer.parseInt(couponText),
                    Float.parseFloat(valeurText), etat, dispo, selectedCategory, selectedImagePath);
            new ProductService().add(newProduct);
            statusLabel.setText("✅ Produit ajouté avec succès!");
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    private boolean validateProductDetails(String nom, String description, String couponText, String valeurText, String etat, String dispo, Category selectedCategory) {
        if (selectedImagePath == null) {
            showError("Veuillez sélectionner une image.");
            return false;
        }
        if (selectedCategory == null) {
            showError("Veuillez sélectionner une catégorie.");
            return false;
        }
        if (!nom.matches("^[a-zA-Z]+$")) {
            showError("Le nom doit contenir uniquement des lettres.", nomField);
            return false;
        }
        if (description.length() < 10 || description.length() > 30) {
            showError("La description doit contenir entre 10 et 30 lettres.", descriptionField);
            return false;
        }
        int coupon;
        try {
            coupon = Integer.parseInt(couponText);
            if (coupon <= 0) {
                showError("Le coupon doit être un entier positif.", couponField);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Coupon invalide (entier positif requis).", couponField);
            return false;
        }
        float valeur;
        try {
            valeur = Float.parseFloat(valeurText);
            if (valeur <= 0) {
                showError("La valeur doit être un nombre positif.", valeurField);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Valeur invalide (nombre positif requis).", valeurField);
            return false;
        }
        if (!etat.matches("^[a-zA-Z]+$")) {
            showError("L'état doit contenir uniquement des lettres.", etatField);
            return false;
        }
        if (!dispo.matches("^[a-zA-Z]+$")) {
            showError("La disponibilité doit contenir uniquement des lettres.", dispoField);
            return false;
        }
        return true;
    }

    private void showError(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message, TextField field) {
        statusLabel.setText(message);
        field.requestFocus();
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        couponField.clear();
        valeurField.clear();
        etatField.clear();
        dispoField.clear();
        categorieCombo.setValue(null);
        selectedImagePath = null;

        nomField.getStyleClass().removeAll("valid-field", "invalid-field");
        descriptionField.getStyleClass().removeAll("valid-field", "invalid-field");
        etatField.getStyleClass().removeAll("valid-field", "invalid-field");
        dispoField.getStyleClass().removeAll("valid-field", "invalid-field");
    }


    @FXML
    void generateDescriptionFromNom(ActionEvent event) {
        String productName = nomField.getText().trim(); // Get and trim the product name
        if (productName.isEmpty()) {
            showError("Erreur : nom du produit vide."); // Show error if name is empty
            return;
        }

        // Call the API to fetch a generated description
        extractedDescription = callGeminiAPI(productName);
        if (extractedDescription != null) {
            // Truncate the description to a maximum of 30 characters
            if (extractedDescription.length() > 30) {
                extractedDescription = extractedDescription.substring(0, 30);
            }

            // Set the truncated extracted description in the description field
            descriptionField.setText(extractedDescription); // Immediately set the generated description in the field
            System.out.println("Description set in field: " + extractedDescription); // Debugging message
        } else {
            showError("Erreur : description introuvable."); // Error if no description is found
        }
    }


    private String callGeminiAPI(String productName) {
        String apiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyBsFnljS8R4PSVW2vMrE79VGioLORGULJs"; // Replace with your actual API Key

        // Construct the JSON payload
        String jsonInputString = String.format("{\"contents\": [{\"parts\":[{\"text\": \"Generate a description (max 30 characters) for: '%s'\"}]}]}", productName);

        try {
            URL url = new URL(apiEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true); // Enable writing to the connection's output stream

            // Write request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Log the response code for debugging

            // Handle response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Log the full response for debugging
                    String responseBody = response.toString();
                    System.out.println("Response Body: " + responseBody);

                    // Extract and log the description
                    String extractedDescription = extractDescriptionFromResponse(responseBody);
                    System.out.println("Extracted Description: " + extractedDescription); // Log the extracted description

                    return extractedDescription; // Return the extracted description
                }
            } else {
                // Handle error response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.err.println("API Error Response: " + errorResponse.toString());
                    showError("Erreur d'API: " + responseCode + " - " + errorResponse.toString());
                }
            }
        } catch (Exception e) {
            showError("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String extractDescriptionFromResponse(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // Navigate directly to the generated text
        if (jsonObject.has("candidates") && jsonObject.getAsJsonArray("candidates").size() > 0) {
            JsonObject candidate = jsonObject.getAsJsonArray("candidates").get(0).getAsJsonObject();

            // Check if the candidate has 'content' and get the generated text
            String generatedText = candidate.getAsJsonObject("content").getAsJsonArray("parts")
                    .get(0).getAsJsonObject().get("text").getAsString();

            return generatedText; // Return only the generated text
        }

        // If we can't find the text, show an error message
        showError("Aucune description générée trouvée dans la réponse.");
        return null;
    }
}