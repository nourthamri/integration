package piproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import piproject.models.Product;
import piproject.services.ProductService;

import java.io.IOException;
import java.util.List;

public class ProductListControlle {

    @FXML
    private ListView<Product> productListView;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button modifyButton;

    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        loadProducts();
        productListView.setCellFactory(listView -> new ProductListCellController());
    }

    private void loadProducts() {
        List<Product> products = productService.getAll();
        productListView.getItems().setAll(products);

        // Set custom cell
        productListView.setCellFactory(listView -> new ProductListCellController());
    }

    @FXML
    private void onAddProduct(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AddProduct.fxml"));
        Parent root = loader.load();

        AddProductController controller = loader.getController();
        // Optional: pass stage if needed

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onDeleteProduct(ActionEvent event) {
        Product selected = productListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ProductService ps = new ProductService();
            ps.delete(selected); // Assuming your ProductService has a delete method
            loadProducts(); // Refresh the list after deletion
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

                // ✅ Get the controller and pass the selected product
                ModifyProductController controller = loader.getController();
                controller.setProduct(selected);

                // Show the new scene
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

    // New method to navigate to the CategoryList view
    @FXML
    private void onCategoryList(ActionEvent event) {
        try {
            // Load the CategoryList FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/CategoryList.fxml"));
            Parent root = loader.load();

            // Set the new scene in the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Handle the error if the FXML loading fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load Category List");
            alert.setContentText("An error occurred while trying to navigate to the category list.");
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showProductDetail(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ProductDetail.fxml"));
            Parent root = loader.load();



            Stage stage = new Stage();
            stage.setTitle("Détails du Produit");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
