package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.example.entities.User;
import org.example.services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;

public class UpdatePasswordController {

    @FXML
    private PasswordField currentMdpField;

    @FXML
    private PasswordField newMdpField;

    @FXML
    private PasswordField confirmMdpField;

    @FXML
    private Label errorLabel;

    private User currentUser;

    // Method to set the current user details
    public void setUserDetails(User user) {
        this.currentUser = user;
    }

    // Method to handle password update submission
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        String currentMdp = currentMdpField.getText();
        String newMdp = newMdpField.getText();
        String confirmMdp = confirmMdpField.getText();

        // Validate input fields
        if (currentMdp.isEmpty() || newMdp.isEmpty() || confirmMdp.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!newMdp.equals(confirmMdp)) {
            errorLabel.setText("Les nouveaux mots de passe ne correspondent pas.");
            return;
        }

        // Verify the current password
        if (!currentUser.getPassword().equals(currentMdp)) {
            errorLabel.setText("Le mot de passe actuel est incorrect."+currentMdp+currentUser.getPassword());
            return;
        }

        // Update the user's password
        currentUser.setPassword(newMdp);
        ServiceUser serviceUser = new ServiceUser(); // Instantiate the service to handle user updates

        if (serviceUser.updatePassword(currentUser)) { // Use service method to update the user in the database
            errorLabel.setText("Mise à jour du mot de passe réussie !");
        } else {
            errorLabel.setText("Échec de la mise à jour du mot de passe. Veuillez réessayer.");
        }
    }

    // Method to handle cancel button action
    @FXML

    private void handleCancelButtonAction(ActionEvent event) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/profil.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


}