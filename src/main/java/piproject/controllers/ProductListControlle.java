package piproject.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import piproject.models.Panier;
import piproject.models.Product;
import piproject.services.PanierService;
import piproject.services.ProductService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ProductListControlle {

    @FXML
    private ListView<Product> productListView;
    @FXML
    private Button addButton;
    @FXML
    private Button addProductBackButton;
    @FXML
    private ComboBox<String>

            languageComboBox;
    @FXML private VBox detailsBox;
    @FXML private Label title;
    @FXML private Label description;
    @FXML private Label valeur;
    @FXML private Label coupon;
    @FXML private Label etat;
    @FXML private Label dispo;
    @FXML private Label categorie;
    @FXML private VBox qrBox;
    @FXML private Label qrLabel;
    @FXML private ImageView qrView;
    @FXML
    private TextField searchField;
    private final ProductService productService = new ProductService();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadProducts();
        setupProductListView();
        setupSearchField();


    }
    @FXML
    private Button translateBtn;
    @FXML
    private Label translatedDescription;

    private void setupProductListView() {
        productListView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label valueLabel = new Label();
            private final Label categoryLabel = new Label();
            private final Button modifyBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox buttonsBox = new HBox(5, modifyBtn, deleteBtn);
            private final VBox textContainer = new VBox(5, nameLabel, valueLabel, categoryLabel);
            private final HBox content = new HBox(10, imageView, textContainer, buttonsBox);

            {
                // Configure image view
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);

                // Configure labels
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                valueLabel.setStyle("-fx-text-fill: green;");
                categoryLabel.setStyle("-fx-text-fill: gray;");

                // Configure buttons
                modifyBtn.getStyleClass().add("modify-btn");
                deleteBtn.getStyleClass().add("delete-btn");

                // Set button actions
                modifyBtn.setOnAction(event -> {
                    Product product = getItem();
                    if (product != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ModifyProduct.fxml"));
                            Parent root = loader.load();
                            ModifyProductController controller = loader.getController();
                            controller.setProduct(product);

                            Stage stage = new Stage();
                            stage.setTitle("Modifier Produit");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            showError("Erreur lors du chargement de la vue de modification");
                        }
                    }
                });

                deleteBtn.setOnAction(event -> {
                    Product product = getItem();
                    if (product != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Supprimer le produit");
                        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + product.getNom() + "?");

                        if (alert.showAndWait().get() == ButtonType.OK) {
                            productService.delete(product);
                            loadProducts(); // Refresh the list
                        }
                    }
                });

                // Configure layout
                buttonsBox.setAlignment(Pos.CENTER_RIGHT);
                HBox.setHgrow(textContainer, Priority.ALWAYS);
                content.setAlignment(Pos.CENTER_LEFT);
            }


            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(product.getNom());
                    valueLabel.setText("Valeur: " + product.getValeur());

                    if (product.getCategorie() != null) {
                        categoryLabel.setText("Catégorie: " + product.getCategorie().getCategory_name());
                    } else {
                        categoryLabel.setText("Catégorie: (non spécifiée)");
                    }

                    if (product.getImage() != null && !product.getImage().isEmpty()) {
                        try {
                            Image img = new Image("file:" + product.getImage());
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
    }

    private void loadProducts() {
        productListView.getItems().setAll(productService.getAll());
    }

    // Add this new method to set up search functionality
    private void setupSearchField() {

        allProducts.setAll(productService.getAll());

        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });

        //

    }

    // Add this new method to filter products
    private void filterProducts(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            productListView.setItems(allProducts);
            return;
        }

        ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
        String lowerCaseSearch = searchText.toLowerCase();

        for (Product product : allProducts) {
            if (product.getNom().toLowerCase().contains(lowerCaseSearch)) {
                filteredProducts.add(product);
            }
        }

        productListView.setItems(filteredProducts);
    }

    @FXML
    private void onAddToCart(ActionEvent event) {
        Product selected = productListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                PanierService panierService = new PanierService();

                // Check if product is already in cart
                if (panierService.isProductInPanier(selected)) {
                    showError("⚠️ This product is already in your cart!");
                    return;
                }

                // If not, add it
                Panier panierItem = new Panier(selected, 1);
                panierService.addToPanier(panierItem);

                // Load and show the cart
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Panier.fxml"));
                Parent root = loader.load();
                PanierController controller = loader.getController();

                Stage stage = new Stage();
                stage.setTitle("Mon Panier");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            } catch (IOException e) {
                showError("Failed to load cart view.");
            }
        } else {
            showError("Please select a product first.");
        }
    }


    @FXML
    private void onProductDetails(ActionEvent event) {
        Product selected = productListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Stage detailStage = new Stage();
            detailStage.setTitle("✨ Détails du Produit");

            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(20));
            vbox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;");

            VBox detailsBox = new VBox(8);
            detailsBox.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label(selected.getNom());
            title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 10px 0;");

            Label description = new Label("• Description : " + selected.getDescription());
            description.setWrapText(true);

            Label valeur = new Label("• Valeur : " + selected.getValeur() + " DT");
            Label coupon = new Label("• Coupon : " + selected.getCoupon());
            Label etat = new Label("• État : " + selected.getEtat());
            Label dispo = new Label("• Disponibilité : " + selected.getDispo());
            Label categorie = new Label("• Catégorie : " +
                    (selected.getCategorie() != null ? selected.getCategorie().getCategory_name() : "N/A"));

            // QR Code Section
            VBox qrBox = new VBox(5);
            qrBox.setAlignment(Pos.CENTER);
            qrBox.setPadding(new Insets(15, 0, 0, 0));

            String qrData = String.format(
                    "PRODUCT DETAILS\n\n" +
                            "Name: %s\nDescription: %s\nPrice: %.2f DT\n" +
                            "Coupon: %d\nStatus: %s\nCategory: %s\nImage Path: %s",
                    selected.getNom(),
                    selected.getDescription(),
                    selected.getValeur(),
                    selected.getCoupon(),
                    selected.getEtat(),
                    (selected.getCategorie() != null ? selected.getCategorie().getCategory_name() : "N/A"),
                    selected.getImage()
            );

            Image qrImage = generateQRCodeImage(qrData, 150, 150);
            ImageView qrView = new ImageView(qrImage);
            qrView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");

            Label qrLabel = new Label("Scan this QR code for product details");
            qrLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

            detailsBox.getChildren().addAll(title, description, valeur, coupon, etat, dispo, categorie);
            qrBox.getChildren().addAll(qrLabel, qrView);

            Separator separator = new Separator();
            separator.setPadding(new Insets(10, 0, 10, 0));

            vbox.getChildren().addAll(detailsBox, separator, qrBox);

            Scene scene = new Scene(vbox, 350, 500); // Adjusted size
            detailStage.setScene(scene);
            detailStage.show();
        } else {
            showError("Veuillez sélectionner un produit pour voir les détails.");
        }
    }

    // QR Code Generation Method
    private Image generateQRCodeImage(String data, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return new Image(new ByteArrayInputStream(pngData));
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: Return empty image if QR generation fails
            WritableImage emptyImage = new WritableImage(width, height);
            PixelWriter writer = emptyImage.getPixelWriter();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    writer.setColor(x, y, Color.WHITE);
                }
            }
            return emptyImage;
        }
    }


    @FXML
    private void onAddProduct(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AddProduct.fxml"));
        Parent root = loader.load();
        AddProductController controller = loader.getController();

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onDeleteProduct(ActionEvent event) {
        Product selected = productListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productService.delete(selected);
            loadProducts();
        } else {
            showError("Veuillez sélectionner un produit à supprimer.");
        }
    }

    @FXML
    private void onModifyProduct(ActionEvent event) {
        Product selected = productListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ModifyProduct.fxml"));
                Parent root = loader.load();

                ModifyProductController controller = loader.getController();
                controller.setProduct(selected);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showError("Veuillez sélectionner un produit à modifier.");
        }
    }

    @FXML
    private void onCategoryList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/CategoryList.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la liste des catégories.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onAddProductBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AddProductBack.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'interface AddProductBack.");
        }
    }

    @FXML
    private void onOpenCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Panier.fxml"));
            Parent root = loader.load();
            PanierController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Mon Panier");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors du chargement du panier.");
        }
    }
    private String translateText(String text, String targetLanguage) throws IOException, InterruptedException {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Encode the text for URL
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

        // Correct API endpoint - you were missing parameters
        String url = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=fr|" + targetLanguage;

        System.out.println("API URL: " + url); // Debug output

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Debug raw response
        System.out.println("Raw response: " + response.body());

        // Parse the JSON response
        String responseBody = response.body();
        int start = responseBody.indexOf("\"translatedText\":\"") + 18;
        int end = responseBody.indexOf("\"", start);

        if (start > 17 && end > start) {
            return responseBody.substring(start, end);
        }

        return "Translation failed. API response: " + responseBody;
    }

}