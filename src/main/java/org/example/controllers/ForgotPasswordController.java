package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.services.ServiceUser;
import org.example.utils.EmailUtil;

import java.io.IOException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label statusLabel;

    private String generatedOTP;
    private String userEmail;

    // Method to handle sending the OTP
    public void sendOtp(ActionEvent event) {
        userEmail = emailField.getText().trim();

        if (userEmail.isEmpty()) {
            statusLabel.setText("❌ Please enter your email!");
            return;
        }

        ServiceUser serviceUser = new ServiceUser();
        if (!serviceUser.emailExists(userEmail)) {
            statusLabel.setText("❌ This email does not exist!");
            return;
        }

        generatedOTP = generateOtp();

        boolean emailSent = EmailUtil.sendOtpEmail(userEmail, generatedOTP);
        if (emailSent) {
            statusLabel.setText("✅ OTP sent! Check your email.");
            navigateToOTPVerification();
        } else {
            statusLabel.setText("❌ Error sending the email.");
        }
    }

    // Method to generate a random OTP
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // Method to navigate to OTP verification screen
    private void navigateToOTPVerification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VerifyOTP.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());

            VerifyOTPController controller = loader.getController();
            controller.setCorrectOtp(generatedOTP);
            controller.setUserEmail(userEmail);

            stage.setResizable(false);  // Prevent resizing
            stage.setWidth(400);  // Set width
            stage.setHeight(500);  // Set height
            stage.setScene(scene);
            stage.setTitle("OTP Verification");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}