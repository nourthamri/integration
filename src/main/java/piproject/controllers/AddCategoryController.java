package piproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import piproject.models.Category;
import piproject.services.CategoryService;

import java.io.IOException;

public class AddCategoryController {

    @FXML private TextField nameField;
    @FXML private TextField descField;

    private final CategoryService service = new CategoryService();

    @FXML
    private void handleAddCategory(ActionEvent event) {
        String name = nameField.getText();
        String desc = descField.getText();

        // Validate name and description (alphabetic only)
        if (name.isEmpty() || desc.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        if (!name.matches("[a-zA-Z\\s]+")) {
            showAlert("Category name must contain only alphabetic characters.");
            return;
        }

        if (!desc.matches("[a-zA-Z\\s]+")) {
            showAlert("Category description must contain only alphabetic characters.");
            return;
        }

        // Create and add the category
        Category category = new Category(name, desc);
        service.add(category);
        showAlert("Category added successfully!");
        clearFields();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Load the CategoryList view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/CategoryList.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Category List");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        descField.clear();
    }
}
