package piproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import piproject.models.Product;
import piproject.models.Category;  // Import the Category model
import piproject.services.ProductService;
import piproject.services.CategoryService;  // Import the CategoryService

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class productController implements Initializable {

    @FXML private TextField nom;
    @FXML private TextField description;
    @FXML private TextField coupon;
    @FXML private TextField valeur;
    @FXML private TextField etat;
    @FXML private TextField dispo;
    @FXML private ComboBox<Category> categoriecombo;  // ComboBox should use Category objects
    @FXML private ImageView imagePreview;
    @FXML private Label statusLabel;
    @FXML private ListView<String> produitListView;

    private File selectedImageFile;
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();  // Create CategoryService instance
    private int editingProductId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategoriesToComboBox();  // Load categories into ComboBox dynamically
        loadProductsToListView();

        // Display category names only in ComboBox
        categoriecombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getCategory_name() : "";
            }

            @Override
            public Category fromString(String string) {
                for (Category c : categoriecombo.getItems()) {
                    if (c.getCategory_name().equals(string)) {
                        return c;
                    }
                }
                return null;
            }
        });
    }


    // Method to load categories dynamically from the database into the ComboBox
    private void loadCategoriesToComboBox() {
        List<Category> categories = categoryService.getAll();  // Fetch categories from the database
        ObservableList<Category> categoryList = FXCollections.observableArrayList(categories);  // ObservableList of Category

        categoriecombo.setItems(categoryList);  // Set the ComboBox items
    }

    // Validation method for checking if a text field contains only alphabetic characters (and spaces)
    private boolean isAlpha(String text) {
        return text.matches("[a-zA-Z\\s]+");
    }

    // Validation for numeric fields (coupon and valeur)
    private boolean isNumeric(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void add(MouseEvent event) {
        try {
            String nomText = nom.getText();
            String descriptionText = description.getText();
            String couponValueText = coupon.getText();
            String valeurValueText = valeur.getText();
            String etatText = etat.getText();
            String dispoText = dispo.getText();
            Category selectedCategory = categoriecombo.getValue();  // Get the selected Category object
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "";

            // Validate required fields
            if (nomText.isEmpty() || descriptionText.isEmpty() || couponValueText.isEmpty() ||
                    valeurValueText.isEmpty() || selectedCategory == null || imagePath.isEmpty()) {
                statusLabel.setText("❗ Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Validate alphabetic fields
            if (!isAlpha(nomText) || !isAlpha(descriptionText) || !isAlpha(etatText) || !isAlpha(dispoText)) {
                statusLabel.setText("❌ Les champs nom, description, état et disponibilité doivent contenir uniquement des lettres.");
                return;
            }

            // Validate numeric fields
            if (!isNumeric(couponValueText)) {
                statusLabel.setText("❌ Coupon doit être un nombre valide.");
                return;
            }
            if (!isNumeric(valeurValueText)) {
                statusLabel.setText("❌ Valeur doit être un nombre valide.");
                return;
            }

            int couponValue = Integer.parseInt(couponValueText);
            float valeurValue = Float.parseFloat(valeurValueText);

            Product product = new Product(nomText, descriptionText, couponValue, valeurValue, etatText, dispoText, selectedCategory, imagePath);
            productService.add(product);

            statusLabel.setText("✅ Produit ajouté avec succès !");
            clearForm();
            loadProductsToListView();

        } catch (Exception e) {
            statusLabel.setText("❌ Erreur: " + e.getMessage());
        }
    }

    @FXML
    void update() {
        try {
            String nomText = nom.getText();
            String descriptionText = description.getText();
            String couponValueText = coupon.getText();
            String valeurValueText = valeur.getText();
            String etatText = etat.getText();
            String dispoText = dispo.getText();
            Category selectedCategory = categoriecombo.getValue();  // Get the selected Category object
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "";

            // Validate required fields
            if (nomText.isEmpty() || descriptionText.isEmpty() || couponValueText.isEmpty() ||
                    valeurValueText.isEmpty() || selectedCategory == null || imagePath.isEmpty()) {
                statusLabel.setText("❗ Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Validate alphabetic fields
            if (!isAlpha(nomText) || !isAlpha(descriptionText) || !isAlpha(etatText) || !isAlpha(dispoText)) {
                statusLabel.setText("❌ Les champs nom, description, état et disponibilité doivent contenir uniquement des lettres.");
                return;
            }

            // Validate numeric fields
            if (!isNumeric(couponValueText)) {
                statusLabel.setText("❌ Coupon doit être un nombre valide.");
                return;
            }
            if (!isNumeric(valeurValueText)) {
                statusLabel.setText("❌ Valeur doit être un nombre valide.");
                return;
            }

            int couponValue = Integer.parseInt(couponValueText);
            float valeurValue = Float.parseFloat(valeurValueText);

            Product updatedProduct = new Product(nomText, descriptionText, couponValue, valeurValue, etatText, dispoText, selectedCategory, imagePath);
            updatedProduct.setId(editingProductId);

            productService.update(updatedProduct);

            statusLabel.setText("✅ Produit mis à jour avec succès !");
            clearForm();
            loadProductsToListView();

        } catch (Exception e) {
            statusLabel.setText("❌ Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @FXML
    void imageChoose() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    private void clearForm() {
        nom.clear();
        description.clear();
        coupon.clear();
        valeur.clear();
        etat.clear();
        dispo.clear();
        categoriecombo.getSelectionModel().clearSelection();
        imagePreview.setImage(null);
        selectedImageFile = null;
    }

    @FXML
    private void loadProductsToListView() {
        List<Product> products = productService.getAll();
        ObservableList<String> productStrings = FXCollections.observableArrayList();

        for (Product p : products) {
            productStrings.add("🆔 " + p.getId() + " | " + p.getNom() + " - " + p.getCategorie().getCategory_name() + " | 💵 " + p.getValeur() + " DT");
        }

        produitListView.setItems(productStrings);
    }

    @FXML
    void onProductSelected(MouseEvent event) {
        String selectedItem = produitListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Extract the product ID from the selected string
            String[] parts = selectedItem.split(" ");
            int id = Integer.parseInt(parts[1]);

            // Load product by ID
            Product selectedProduct = productService.getById(id);
            if (selectedProduct != null) {
                // Set current editing ID
                editingProductId = selectedProduct.getId();

                // Populate form fields
                nom.setText(selectedProduct.getNom());
                description.setText(selectedProduct.getDescription());
                coupon.setText(String.valueOf(selectedProduct.getCoupon()));
                valeur.setText(String.valueOf(selectedProduct.getValeur()));
                etat.setText(selectedProduct.getEtat());
                dispo.setText(selectedProduct.getDispo());
                categoriecombo.setValue(selectedProduct.getCategorie());  // Set Category in ComboBox
                selectedImageFile = new File(selectedProduct.getImage());

                if (selectedImageFile.exists()) {
                    imagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
                }

                statusLabel.setText("✏️ Produit sélectionné pour modification.");
            }
        }
    }

    @FXML
    void deleteProduct() {
        if (editingProductId == -1) {
            statusLabel.setText("❗ Aucun produit sélectionné à supprimer.");
            return;
        }

        Product productToDelete = productService.getById(editingProductId);
        if (productToDelete == null) {
            statusLabel.setText("❌ Produit introuvable.");
            return;
        }

        productService.delete(productToDelete);
        statusLabel.setText("🗑️ Produit supprimé avec succès.");
        clearForm();
        loadProductsToListView();
        editingProductId = -1;
    }
    @FXML
    private void onBackToProductList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ProductList.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("❌ Erreur lors du retour à la liste des produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
