package piproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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

        // Optional: prevent saving before data is set
        if (selectedProduct == null) {
            statusLabel.setText("Aucun produit sélectionné.");
        }
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

        try {
            selectedProduct.setNom(nomField.getText());
            selectedProduct.setDescription(descriptionField.getText());
            selectedProduct.setCoupon(Integer.parseInt(couponField.getText()));
            selectedProduct.setValeur(Float.parseFloat(valeurField.getText()));
            selectedProduct.setEtat(etatField.getText());
            selectedProduct.setDispo(dispoField.getText());
            selectedProduct.setCategorie(categorieCombo.getValue());
            selectedProduct.setImage(selectedImagePath);

            new ProductService().update(selectedProduct);
            statusLabel.setText("✅ Produit mis à jour !");
        } catch (Exception e) {
            statusLabel.setText("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
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
