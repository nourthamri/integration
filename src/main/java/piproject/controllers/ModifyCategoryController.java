package piproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import piproject.models.Category;
import piproject.services.CategoryService;

import java.io.IOException;

public class ModifyCategoryController {

    @FXML private TextField nameField;
    @FXML private TextField descField;

    private final CategoryService service = new CategoryService();
    private Category categoryToEdit;

    public void setCategory(Category category) {
        this.categoryToEdit = category;
        nameField.setText(category.getCategory_name());
        descField.setText(category.getCategory_description());
    }

    @FXML
    private void handleUpdateCategory(ActionEvent event) {
        // Update the category with the new values from the text fields
        categoryToEdit.setCategory_name(nameField.getText());
        categoryToEdit.setCategory_description(descField.getText());

        // Update the category in the database
        service.update(categoryToEdit);

        // Navigate to CategoryList view
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


    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/CategoryList.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Category List");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}