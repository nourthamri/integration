package piproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import piproject.models.Category;
import piproject.services.CategoryService;

import java.io.IOException;

public class CategoryListController {

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> idCol;
    @FXML private TableColumn<Category, String> nameCol;
    @FXML private TableColumn<Category, String> descCol;
    @FXML private TableColumn<Category, Void> actionCol;

    private final CategoryService categoryService = new CategoryService();

    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("category_id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("category_name"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("category_description"));

        addActionButtons();
        loadData();
    }

    private void loadData() {
        ObservableList<Category> list = FXCollections.observableArrayList(categoryService.getAll());
        categoryTable.setItems(list);
    }

    private void addActionButtons() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button modifyBtn = new Button("Modify");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, modifyBtn, deleteBtn);

            {
                modifyBtn.setOnAction(e -> {
                    Category category = getTableView().getItems().get(getIndex());
                    goToModify(category);
                });
                deleteBtn.setOnAction(e -> {
                    Category category = getTableView().getItems().get(getIndex());
                    categoryService.delete(category);
                    loadData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    @FXML
    private void goToAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AddCategory.fxml"));
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToModify(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ModifyCategory.fxml"));
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            ModifyCategoryController controller = loader.getController();
            controller.setCategory(category);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // New method to navigate back to the Product List
    @FXML
    private void goToProductList(ActionEvent event) {
        try {
            // Load the ProductList FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ProductList.fxml"));
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            // Handle error if loading the ProductList view fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load Product List");
            alert.setContentText("An error occurred while trying to navigate back to the product list.");
            alert.showAndWait();
        }
    }
}
