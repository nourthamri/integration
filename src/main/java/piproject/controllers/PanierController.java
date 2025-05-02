package piproject.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import piproject.models.Panier;
import piproject.services.PanierService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PanierController implements Initializable {

    @FXML
    private ListView<Panier> panierListView;
    @FXML
    private Label totalLabel;
    @FXML
    private Button exportPdfButton;
    @FXML
    private Button payButton;
    @FXML
    private Button testApiButton;

    private PanierService panierService = new PanierService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPanier();
        exportPdfButton.setOnAction(event -> generatePDFInvoice());
        payButton.setOnAction(event -> handlePayment());
    }

    private void loadPanier() {
        try {
            List<Panier> panierItems = panierService.getPanierList();
            panierListView.setItems(FXCollections.observableArrayList(panierItems));
            panierListView.setCellFactory(param -> new ListCell<Panier>() {
                private final ImageView imageView = new ImageView();
                private final Label nameLabel = new Label();
                private final Label quantityLabel = new Label();
                private final Label priceLabel = new Label();
                private final Button deleteButton = new Button("🗑️");
                private final HBox content = new HBox(10);

                {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setPreserveRatio(true);

                    nameLabel.setStyle("-fx-font-weight: bold;");
                    quantityLabel.setStyle("-fx-text-fill: #666;");
                    priceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
                    deleteButton.setOnAction(event -> {
                        Panier item = getItem();
                        if (item != null) {
                            try {
                                panierService.removeFromPanier(item.getId());
                                loadPanier();
                            } catch (SQLException e) {
                                showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
                            }
                        }
                    });

                    VBox textBox = new VBox(5, nameLabel, quantityLabel, priceLabel);
                    HBox.setHgrow(textBox, Priority.ALWAYS);
                    content.setAlignment(Pos.CENTER_LEFT);
                    content.getChildren().addAll(imageView, textBox, deleteButton);
                }

                @Override
                protected void updateItem(Panier item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        nameLabel.setText(item.getProduct().getNom());
                        quantityLabel.setText("Quantité: " + item.getQuantity());
                        priceLabel.setText(String.format("%.2f DT", item.getProduct().getValeur() * item.getQuantity()));

                        if (item.getProduct().getImage() != null && !item.getProduct().getImage().isEmpty()) {
                            try {
                                Image img = new Image("file:" + item.getProduct().getImage());
                                imageView.setImage(img);
                            } catch (Exception e) {
                                imageView.setImage(null);
                            }
                        } else {
                            imageView.setImage(null);
                        }

                        setGraphic(content);
                    }
                }
            });

            updateTotal();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement", e.getMessage());
        }
    }

    @FXML
    private void generatePDFInvoice() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer la facture");
            fileChooser.setInitialFileName("Facture_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());

            if (file != null) {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Company Information
                Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
                Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

                Paragraph companyName = new Paragraph("EXCHANGY", companyFont);
                companyName.setAlignment(Element.ALIGN_CENTER);
                companyName.setSpacingAfter(5);

                Paragraph address = new Paragraph("Esprit Ghazela", infoFont);
                address.setAlignment(Element.ALIGN_CENTER);
                address.setSpacingAfter(5);

                Paragraph phone = new Paragraph("Tél: 28 044 602", infoFont);
                phone.setAlignment(Element.ALIGN_CENTER);
                phone.setSpacingAfter(20);

                document.add(companyName);
                document.add(address);
                document.add(phone);

                // Invoice title
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
                Paragraph title = new Paragraph("FACTURE", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                // Separator line
                Paragraph line = new Paragraph("________________________________________________");
                line.setAlignment(Element.ALIGN_CENTER);
                line.setSpacingAfter(10);
                document.add(line);

                // Add date
                Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                Paragraph date = new Paragraph("Date: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), dateFont);
                date.setAlignment(Element.ALIGN_RIGHT);
                date.setSpacingAfter(20);
                document.add(date);

                // Create table
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                // Table headers
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                addTableHeader(table, "Produit", headerFont);
                addTableHeader(table, "Prix unitaire", headerFont);
                addTableHeader(table, "Quantité", headerFont);
                addTableHeader(table, "Total", headerFont);

                // Add products
                Font productFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                double total = 0;

                for (Panier item : panierListView.getItems()) {
                    addTableCell(table, item.getProduct().getNom(), productFont);
                    addTableCell(table, String.format("%.2f DT", item.getProduct().getValeur()), productFont);
                    addTableCell(table, String.valueOf(item.getQuantity()), productFont);
                    addTableCell(table, String.format("%.2f DT", item.getProduct().getValeur() * item.getQuantity()), productFont);
                    total += item.getProduct().getValeur() * item.getQuantity();
                }

                document.add(table);

                // Add total
                Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
                Paragraph totalParagraph = new Paragraph("Total: " + String.format("%.2f DT", total), totalFont);
                totalParagraph.setAlignment(Element.ALIGN_RIGHT);
                totalParagraph.setSpacingBefore(20);
                document.add(totalParagraph);

                // Add thank you message
                Font messageFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
                Paragraph message = new Paragraph("Merci pour votre confiance!", messageFont);
                message.setAlignment(Element.ALIGN_CENTER);
                message.setSpacingBefore(20);
                document.add(message);

                document.close();

                showAlert("Succès", "Facture générée", "La facture a été enregistrée avec succès!");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération", e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void updateTotal() throws SQLException {
        double total = panierService.getPanierList().stream()
                .mapToDouble(p -> p.getProduct().getValeur() * p.getQuantity())
                .sum();
        totalLabel.setText(String.format("%.2f DT", total));
    }

    @FXML
    private void onClearPanier() {
        try {
            panierService.clearPanier();
            loadPanier();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handlePayment() {
        try {
            // Calculate total
            double total = panierService.getPanierList().stream()
                    .mapToDouble(p -> p.getProduct().getValeur() * p.getQuantity())
                    .sum();

            // Load payment interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/payment.fxml"));
            Parent root = loader.load();

            // Pass the total amount to payment controller
            PaymentController paymentController = loader.getController();
            paymentController.setTotalAmount(total);

            // Create new stage for payment
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Paiement Flousi");
            paymentStage.setScene(new Scene(root));
            paymentStage.initModality(Modality.APPLICATION_MODAL);
            paymentStage.showAndWait();

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de calcul", "Impossible de calculer le montant total");
        } catch (IOException e) {
            showAlert("Erreur", "Erreur d'interface", "Impossible d'ouvrir l'interface de paiement");
        }
    }

    @FXML
    private void testFlousiApi() {
        try {
            // Show loading dialog immediately
            Alert loadingAlert = createLoadingAlert();
            loadingAlert.show();

            new Thread(() -> {
                try {
                    // Step 1: Verify internet connectivity
                    if (!isInternetAvailable()) {
                        showError(loadingAlert, "Aucune connexion Internet",
                                "Vérifiez votre connexion réseau");
                        return;
                    }

                    // Step 2: Verify DNS resolution
                    if (!isDnsResolving("api.flousi.com")) {
                        showError(loadingAlert, "Erreur DNS",
                                "Impossible de résoudre api.flousi.com");
                        return;
                    }

                    // Step 3: Verify API endpoint reachability
                    if (!isApiEndpointReachable()) {
                        showError(loadingAlert, "API inaccessible",
                                "Le serveur API ne répond pas");
                        return;
                    }

                    // Step 4: Proceed with actual API test
                    String apiKey = System.getenv("FLOUSI_API_KEY");
                    if (apiKey == null || apiKey.isEmpty()) {
                        showError(loadingAlert, "Clé API manquante",
                                "Configurez FLOUSI_API_KEY");
                        return;
                    }

                    // Test API call
                    boolean success = testApiConnection(apiKey);

                    javafx.application.Platform.runLater(() -> {
                        loadingAlert.close();
                        if (success) {
                            showAlert("Succès", "API fonctionnelle",
                                    "La connexion à l'API Flousi est opérationnelle");
                        } else {
                            showAlert("Avertissement", "Problème API",
                                    "L'API a répondu mais avec une erreur");
                        }
                    });

                } catch (Exception e) {
                    showError(loadingAlert, "Erreur technique",
                            "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur initiale", e.getMessage());
        }
    }

    // Helper methods
    private Alert createLoadingAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test en cours");
        alert.setHeaderText("Vérification de la connectivité");
        alert.setContentText("Patientez pendant les tests...");
        return alert;
    }

    private void showError(Alert loadingAlert, String title, String message) {
        javafx.application.Platform.runLater(() -> {
            loadingAlert.close();
            showAlert("Erreur", title, message);
        });
    }

    private boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDnsResolving(String hostname) {
        try {
            InetAddress.getByName(hostname);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean isApiEndpointReachable() {
        try {
            URL url = new URL("https://api.flousi.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testApiConnection(String apiKey) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String testBody = "{\"amount\": 10.00, \"currency\": \"TND\", \"description\": \"TEST CONNECTION\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.flousi.com/payment"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }
    @FXML
    private void runNetworkDiagnostics() {
        StringBuilder results = new StringBuilder("Résultats du diagnostic:\n\n");

        // 1. Internet test
        results.append("1. Connexion Internet: ")
                .append(isInternetAvailable() ? "✔ Disponible\n" : "✖ Indisponible\n");

        // 2. DNS test with multiple providers
        results.append("2. Résolution DNS:\n");
        String[] dnsProviders = {"Système", "Google (8.8.8.8)", "Cloudflare (1.1.1.1)"};
        boolean anyDnsWorking = false;

        for (int i = 0; i < dnsProviders.length; i++) {
            try {
                if (i == 1) System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
                if (i == 2) System.setProperty("sun.net.spi.nameservice.nameservers", "1.1.1.1");

                InetAddress.getByName("api.flousi.com");
                results.append("   - ").append(dnsProviders[i]).append(": ✔ Réussi\n");
                anyDnsWorking = true;
            } catch (Exception e) {
                results.append("   - ").append(dnsProviders[i]).append(": ✖ Échoué\n");
            }
        }

        // 3. API reachability
        results.append("3. Accessibilité API: ")
                .append(anyDnsWorking ? "✔ Accessible\n" : "✖ Inaccessible (problème DNS)\n");

        // 4. API Key check
        results.append("4. Clé API configurée: ")
                .append(System.getenv("FLOUSI_API_KEY") != null ? "✔ Présente\n" : "✖ Absente\n");

        // 5. Suggested solutions
        if (!anyDnsWorking) {
            results.append("\nSolutions recommandées:\n");
            results.append("- Changer de serveur DNS (essayer 8.8.8.8)\n");
            results.append("- Modifier le fichier hosts (voir aide)");
        }

        Alert diagnosticsAlert = new Alert(Alert.AlertType.INFORMATION);
        diagnosticsAlert.setTitle("Diagnostic réseau complet");
        diagnosticsAlert.setHeaderText("État de la connectivité");
        diagnosticsAlert.setContentText(results.toString());

        // Add help button
        ButtonType helpButton = new ButtonType("Aide DNS", ButtonBar.ButtonData.HELP);
        diagnosticsAlert.getButtonTypes().add(helpButton);

        Optional<ButtonType> result = diagnosticsAlert.showAndWait();
        if (result.isPresent() && result.get() == helpButton) {
            showDnsHelpDialog();
        }
    }

    private void showDnsHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Aide DNS");
        helpAlert.setHeaderText("Comment changer vos paramètres DNS");

        String os = System.getProperty("os.name").toLowerCase();
        String instructions;

        if (os.contains("win")) {
            instructions = "1. Ouvrir 'Panneau de configuration'\n"
                    + "2. Sélectionner 'Réseau et Internet'\n"
                    + "3. Changer les paramètres DNS dans 'Adaptateur réseau'";
        } else if (os.contains("mac")) {
            instructions = "1. Ouvrir 'Préférences Système'\n"
                    + "2. Sélectionner 'Réseau'\n"
                    + "3. Modifier DNS sous 'Avancé'";
        } else {
            instructions = "Modifiez /etc/resolv.conf ou les paramètres réseau";
        }

        helpAlert.setContentText(instructions + "\n\nServeurs DNS recommandés:\n"
                + "- 8.8.8.8 (Google)\n"
                + "- 1.1.1.1 (Cloudflare)");
        helpAlert.showAndWait();
    }
    // Add this temporary workaround to your PanierController.java
// This bypasses DNS by using direct IP (if known)
    private static final String FLousi_API_IP = "192.168.1.100"; // Replace with actual IP if known

    private boolean testWithDirectIP() {
        try {
            // Override hostname resolution
            java.security.Security.setProperty("networkaddress.cache.ttl", "0");
            System.setProperty("sun.net.inetaddr.ttl", "0");

            // Use direct IP connection test
            URL url = new URL("https://" + FLousi_API_IP);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return true;
        } catch (Exception e) {
            System.out.println("Direct IP connection failed: " + e.getMessage());
            return false;
        }
    }
    // Add this DNS verification method
    private void checkAlternativeDNS() {
        try {
            String[] dnsServers = {"8.8.8.8", "1.1.1.1", "208.67.222.222"}; // Google, Cloudflare, OpenDNS

            for (String dns : dnsServers) {
                System.out.println("Testing DNS: " + dns);
                java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");
                System.setProperty("sun.net.spi.nameservice.nameservers", dns);
                System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");

                if (InetAddress.getByName("api.flousi.com") != null) {
                    System.out.println("Success with DNS: " + dns);
                    showAlert("DNS Réussi", "Résolution fonctionnelle avec", dns);
                    return;
                }
            }
            showAlert("Échec DNS", "Aucun serveur DNS alternatif n'a fonctionné",
                    "Essayez manuellement avec:\n1. 8.8.8.8 (Google)\n2. 1.1.1.1 (Cloudflare)");
        } catch (Exception e) {
            showAlert("Erreur DNS", "Exception lors du test DNS", e.getMessage());
        }
    }
    // Add this method to help users modify hosts file
    private void suggestHostsFileFix() {
        String os = System.getProperty("os.name").toLowerCase();
        String hostsPath = os.contains("win") ?
                "C:\\Windows\\System32\\drivers\\etc\\hosts" :
                "/etc/hosts";

        String message = "Ajoutez cette ligne à " + hostsPath + ":\n\n" +
                "# Flousi API\n" +
                "192.168.1.100 api.flousi.com\n\n" +
                "Remplacez 192.168.1.100 par l'IP réelle du serveur";

        showAlert("Solution Hosts File", "Configuration manuelle requise", message);
    }
}