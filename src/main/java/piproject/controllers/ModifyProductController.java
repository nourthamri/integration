package piproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import piproject.models.Product;
import piproject.models.Category;
import piproject.services.ProductService;
import piproject.services.CategoryService;

import java.io.File;
import java.io.IOException;

public class ModifyProductController {

    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private TextField couponField;
    @FXML private TextField valeurField;
    @FXML private TextField etatField;
    @FXML private TextField dispoField;
    @FXML private ComboBox<Category> categorieCombo;
    @FXML private Label statusLabel;

    private Product selectedProduct;
    private String selectedImagePath;

    /**
     * Called by the previous controller to pass the selected product.
     */
    public void setProduct(Product product) {
        this.selectedProduct = product;
        if (product != null) {
            selectedImagePath = product.getImage();
            fillFormWithProduct(product);
        }
    }

    /**
     * Initialize the combo box and other UI elements.
     */
    @FXML
    public void initialize() {
        // Load categories into combo box
        CategoryService categoryService = new CategoryService();
        categorieCombo.getItems().addAll(categoryService.getAll());
        categorieCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getCategory_name() : "";
            }

            @Override
            public Category fromString(String string) {
                for (Category c : categorieCombo.getItems()) {
                    if (c.getCategory_name().equals(string)) {
                        return c;
                    }
                }
                return null;
            }
        });

        // Optional: prevent saving before data is set
        if (selectedProduct == null) {
            statusLabel.setText("Aucun produit sélectionné.");
        }

        // Setup input validation
        setupInputFilters();
    }

    private void fillFormWithProduct(Product product) {
        nomField.setText(product.getNom());
        descriptionField.setText(product.getDescription());
        couponField.setText(String.valueOf(product.getCoupon()));
        valeurField.setText(String.valueOf(product.getValeur()));
        etatField.setText(product.getEtat());
        dispoField.setText(product.getDispo());
        categorieCombo.setValue(product.getCategorie());
    }

    private void setupInputFilters() {
        addLetterOnlyFilter(nomField); // Only letters for product name
        addDescriptionFilter(descriptionField); // Allowed letters and spaces for description
        addNumericFilter(couponField); // Only numbers for the coupon
        addNumericFilter(valeurField); // Only numbers for the value
        addLetterOnlyFilter(etatField); // Only letters for state
        addLetterOnlyFilter(dispoField); // Only letters for availability
    }

    private void addLetterOnlyFilter(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z]*")) {
                field.setText(oldVal);
            }
        });
    }

    private void addDescriptionFilter(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z ]*")) {
                field.setText(oldVal); // Allow letters and spaces only
            } else if (newVal.length() > 30) { // Limit to 30 characters
                field.setText(newVal.substring(0, 30));
            }
        });
    }

    private void addNumericFilter(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) { // Allow only digits
                field.setText(oldVal);
            }
        });
    }

    @FXML
    void onChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une nouvelle image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(nomField.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            statusLabel.setText("Image sélectionnée : " + file.getName());
        }
    }

    @FXML
    void onUpdateProduct() {
        if (selectedProduct == null) {
            statusLabel.setText("Erreur : aucun produit sélectionné.");
            return;
        }

        // Validate inputs
        try {
            if (!isValidInputs()) {
                return; // Invalid inputs; exit the method
            }

            selectedProduct.setNom(nomField.getText().trim());
            selectedProduct.setDescription(descriptionField.getText().trim());
            selectedProduct.setCoupon(Integer.parseInt(couponField.getText().trim()));
            selectedProduct.setValeur(Float.parseFloat(valeurField.getText().trim()));
            selectedProduct.setEtat(etatField.getText().trim());
            selectedProduct.setDispo(dispoField.getText().trim());
            selectedProduct.setCategorie(categorieCombo.getValue());
            selectedProduct.setImage(selectedImagePath);

            new ProductService().update(selectedProduct);
            statusLabel.setText("✅ Produit mis à jour !");
        } catch (Exception e) {
            statusLabel.setText("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidInputs() {
        // Product name validation
        if (nomField.getText().trim().isEmpty()) {
            statusLabel.setText("Le nom du produit ne peut pas être vide.");
            return false;
        }
        // Description validation
        if (descriptionField.getText().trim().isEmpty() || descriptionField.getText().length() > 30) {
            statusLabel.setText("La description doit contenir entre 1 et 30 caractères.");
            return false;
        }
        // Coupon validation
        try {
            Integer.parseInt(couponField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Le coupon doit être un entier valide.");
            return false;
        }
        // Valeur validation
        try {
            Float.parseFloat(valeurField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("La valeur doit être un nombre valide.");
            return false;
        }
        // State and availability validation
        if (etatField.getText().trim().isEmpty() || dispoField.getText().trim().isEmpty()) {
            statusLabel.setText("L'état et la disponibilité ne peuvent pas être vides.");
            return false;
        }
        return true; // All inputs are valid
    }

    @FXML
    void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ProductList.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}