package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import org.example.model.LoginHistory;
import org.example.model.User;
import org.example.services.LoginHistoryService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class AjoutLoginHistoryController {

    @FXML
    private TextField ipAddressField;

    @FXML
    private TextField deviceInfoField;

    @FXML
    private Button addButton;
    @FXML
    private Button showHistoryButton;

    // Assume this is injected or passed (e.g., the logged-in user)
    private User currentUser;

    private final LoginHistoryService loginHistoryService = new LoginHistoryService();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    void initialize() {
        addButton.setOnAction(this::handleAddLoginHistory);
        showHistoryButton.setOnAction(this::handleShowHistory);
    }

    private void handleAddLoginHistory(ActionEvent event) {

        String ip = ipAddressField.getText();
        String device = deviceInfoField.getText();

        if (ip.isEmpty() || device.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all fields.");
            return;
        }

        LoginHistory loginHistory = new LoginHistory(
                currentUser,
                LocalDateTime.now(),
                ip,
                device
        );

        loginHistoryService.save(loginHistory);
        showAlert(Alert.AlertType.INFORMATION, "Login history added successfully.");
        clearFields();

        // After successful insertion, load the ajouterLoginHistory.fxml

    }


    private void clearFields() {
        ipAddressField.clear();
        deviceInfoField.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void handleShowHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginHistory.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'historique.");
        }
    }
}
