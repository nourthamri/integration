package piproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

public class PaymentController {

    @FXML private Label amountLabel;
    @FXML private ComboBox<String> paymentMethod;
    @FXML private VBox cardDetails;
    @FXML private TextField cardNumber;
    @FXML private TextField expiryDate;
    @FXML private TextField cvv;
    @FXML private Button payButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;

    private double totalAmount;

    @FXML
    public void initialize() {
        // Setup payment method listener
        paymentMethod.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            cardDetails.setVisible("Carte Bancaire".equals(newVal));
        });

        // Setup pay button action
        payButton.setOnAction(event -> processPayment());
    }

    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
        amountLabel.setText(String.format("%.2f DT", amount));
    }

    private void processPayment() {
        String selectedMethod = paymentMethod.getValue();
        if (selectedMethod == null) {
            statusLabel.setText("Veuillez sélectionner une méthode de paiement");
            return;
        }

        // Validate card details if paying by card
        if ("Carte Bancaire".equals(selectedMethod)) {
            if (!validateCardDetails()) {
                return;
            }
        }

        // Show loading indicator
        payButton.setDisable(true);
        progressIndicator.setVisible(true);
        statusLabel.setText("");

        // Process payment in background thread
        new Thread(() -> {
            try {
                boolean success = callFlousiApi(totalAmount);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    payButton.setDisable(false);

                    if (success) {
                        statusLabel.setText("Paiement réussi!");
                        statusLabel.setStyle("-fx-text-fill: #27ae60;");
                        // Close window after successful payment
                        payButton.getScene().getWindow().hide();
                    } else {
                        statusLabel.setText("Échec du paiement. Veuillez réessayer.");
                        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    payButton.setDisable(false);
                    statusLabel.setText("Erreur de connexion: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                });
            }
        }).start();
    }

    private boolean validateCardDetails() {
        if (cardNumber.getText().trim().isEmpty() ||
                expiryDate.getText().trim().isEmpty() ||
                cvv.getText().trim().isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs de la carte");
            return false;
        }

        // Validate card number length (16 digits)
        if (cardNumber.getText().trim().length() != 16) {
            statusLabel.setText("Le numéro de carte doit contenir 16 chiffres");
            return false;
        }

        // Validate CVV length (3-4 digits)
        if (cvv.getText().trim().length() < 3 || cvv.getText().trim().length() > 4) {
            statusLabel.setText("Le CVV doit contenir 3 ou 4 chiffres");
            return false;
        }

        return true;
    }

    private boolean callFlousiApi(double amount) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Prepare payment data based on selected method
        String paymentData;
        String selectedMethod = paymentMethod.getValue();

        if ("Carte Bancaire".equals(selectedMethod)) {
            paymentData = String.format(
                    "{\"amount\": %.2f, \"currency\": \"TND\", \"method\": \"CARD\", " +
                            "\"card\": {\"number\": \"%s\", \"expiry\": \"%s\", \"cvv\": \"%s\"}, " +
                            "\"description\": \"Paiement EXCHANGY\"}",
                    amount,
                    cardNumber.getText().trim(),
                    expiryDate.getText().trim(),
                    cvv.getText().trim()
            );
        } else {
            paymentData = String.format(
                    "{\"amount\": %.2f, \"currency\": \"TND\", \"method\": \"%s\", " +
                            "\"description\": \"Paiement EXCHANGY\"}",
                    amount,
                    selectedMethod.toUpperCase().replace(" ", "_")
            );
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.flousi.com/payment"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("FLOUSI_API_KEY"))
                .POST(HttpRequest.BodyPublishers.ofString(paymentData))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // Check for successful payment (status code 200-299)
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }


}