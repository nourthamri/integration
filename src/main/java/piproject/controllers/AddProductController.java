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

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddProductController {

    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private TextField couponField;
    @FXML private TextField valeurField;
    @FXML private TextField etatField;
    @FXML private TextField dispoField;
    @FXML private ComboBox<Category> categorieCombo; // Change ComboBox to use Category model
    @FXML private Label statusLabel;

    private String selectedImagePath;
    private CategoryService categoryService;

    @FXML
    public void initialize() {
        categoryService = new CategoryService();
        populateCategoryComboBox();

        // ✅ Display only category names in the ComboBox
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


    // Populate the category combo box with categories from the database
    private void populateCategoryComboBox() {
        try {
            List<Category> categories = categoryService.getAll();
            categorieCombo.getItems().addAll(categories); // Add categories to the ComboBox
        } catch (Exception e) {
            statusLabel.setText("Erreur de chargement des catégories : " + e.getMessage());
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
            statusLabel.setText("Image sélectionnée !");
        }
    }

    @FXML
    void onAddProduct(ActionEvent event) {
        String nom = nomField.getText();
        String description = descriptionField.getText();
        String couponText = couponField.getText();
        String valeurText = valeurField.getText();
        String etat = etatField.getText();
        String dispo = dispoField.getText();
        Category selectedCategory = categorieCombo.getValue();

        // Check if image is selected
        if (selectedImagePath == null) {
            statusLabel.setText("Veuillez sélectionner une image.");
            return;
        }

        // Check category
        if (selectedCategory == null) {
            statusLabel.setText("Veuillez sélectionner une catégorie.");
            return;
        }

        // Validate 'nom' (alphabet only)
        if (nom.isEmpty() || !nom.matches("[a-zA-Z\\s]+")) {
            statusLabel.setText("Le nom doit contenir uniquement des lettres.");
            return;
        }

        // Validate 'description' (min 10 chars and alphabet only)
        if (description.length() < 10 || !description.matches("[a-zA-Z\\s]+")) {
            statusLabel.setText("La description doit contenir au moins 10 lettres et uniquement des lettres.");
            return;
        }

        // Validate 'coupon' (positive integer)
        int coupon;
        try {
            coupon = Integer.parseInt(couponText);
            if (coupon < 0) {
                statusLabel.setText("Le coupon doit être un entier positif.");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Coupon invalide (entier requis).");
            return;
        }

        // Validate 'valeur' (positive float)
        float valeur;
        try {
            valeur = Float.parseFloat(valeurText);
            if (valeur <= 0) {
                statusLabel.setText("La valeur doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Valeur invalide (nombre requis).");
            return;
        }

        // Validate 'etat' (alphabet only)
        if (etat.isEmpty() || !etat.matches("[a-zA-Z\\s]+")) {
            statusLabel.setText("L'état doit contenir uniquement des lettres.");
            return;
        }

        // Validate 'dispo' (alphabet only)
        if (dispo.isEmpty() || !dispo.matches("[a-zA-Z\\s]+")) {
            statusLabel.setText("La disponibilité doit contenir uniquement des lettres.");
            return;
        }

        // ✅ All good – create the product
        try {
            Product newProduct = new Product(nom, description, coupon, valeur, etat, dispo, selectedCategory, selectedImagePath);
            new ProductService().add(newProduct);
            statusLabel.setText("✅ Produit ajouté avec succès !");
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ProductList.fxml")); // ✅ Set the FXML file here
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clear the form fields after adding the product
    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        couponField.clear();
        valeurField.clear();
        etatField.clear();
        dispoField.clear();
        categorieCombo.setValue(null);
        selectedImagePath = null;
    }
}
