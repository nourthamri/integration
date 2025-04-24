package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.io.IOException;

import org.example.services.ServiceUser;

public class ResetPasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void changePassword() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("❌ Les champs ne peuvent pas être vides !");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("❌ Les mots de passe ne correspondent pas !");
            return;
        }

        ServiceUser serviceUser = new ServiceUser();
        boolean updated = serviceUser.updatePassword(userEmail, newPassword);

        if (updated) {
            statusLabel.setText("✅ Mot de passe changé avec succès !");
            navigateToLogin();
        } else {
            statusLabel.setText("❌ Erreur lors de la mise à jour du mot de passe !");
        }
    }

    private void navigateToLogin() {
        try {
            // Load Login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Connexion");
            stage.setResizable(false);
            stage.show();

            // Close Reset Password window
            newPasswordField.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
