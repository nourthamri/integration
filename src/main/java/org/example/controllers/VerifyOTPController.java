package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class VerifyOTPController {

    @FXML
    private TextField otpField;

    @FXML
    private Label statusLabel;

    private String correctOtp;
    private String userEmail;

    public void setCorrectOtp(String otp) {
        this.correctOtp = otp;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void verifyOtp() {
        String enteredOtp = otpField.getText().trim();

        if (enteredOtp.equals(correctOtp)) {
            statusLabel.setText("✅ OTP Vérifié ! Redirection...");
            navigateToResetPassword();
        } else {
            statusLabel.setText("❌ OTP incorrect !");
        }
    }

    private void navigateToResetPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ResetPassword.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());

            ResetPasswordController controller = loader.getController();
            controller.setUserEmail(userEmail); // Pass user email to reset password page

            stage.setScene(scene);
            stage.setTitle("Réinitialiser le mot de passe");
            stage.setResizable(false);
            stage.show();

            // Close OTP window after opening Reset Password
            otpField.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
