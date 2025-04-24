package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.example.entities.User;
import org.example.services.ServiceUser;
import org.example.utils.EmailUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InscriptionController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField mdpField;

    @FXML
    private Button signupButton;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        // Initialization logic if needed can be placed here.
    }

    @FXML
    private void handleSignupButtonAction(ActionEvent event) {
        ServiceUser userService = new ServiceUser(); // Moved to the start for better scope management
        try {
            // Retrieve data from input fields
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String mdp = mdpField.getText().trim();

            // Validate fields
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }
            if (!isEmailValid(email)) {
                showAlert("Erreur", "Adresse email invalide !");
                return;
            }

            // Check if email is already registered
            if (userService.emailExists(email)) {
                showAlert("Erreur", "Cet email est déjà utilisé !");
                return;
            }

            String role = "ROLE_USER";
            User newUser = new User(email,  mdp, nom, prenom, null, null);

            // Save user as unverified and send verification email
            if (userService.ajouteru(newUser)) {
                String verificationToken = generateVerificationToken();

                // Store the verification token in the database (you need to implement this in ServiceUser)


                // Create the verification URL with the token
                String verificationUrl = "http://localhost:8085/verify?email=" + email + "&token=" + verificationToken;
                showAlert("Inscription réussie", "Un email de vérification a été envoyé à votre adresse. Veuillez vérifier votre boîte de réception.");
                clearInputFields();
            } else {
                showAlert("Erreur", "L'inscription a échoué. Veuillez réessayer.");
            }
        } catch (Exception e) { // For any other exceptions that might occur
            showAlert("Erreur", "Une erreur inattendue s'est produite. Veuillez réessayer.");
            e.printStackTrace();
        }
    }

    private void redirectToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Ensure the same scene size to prevent layout issues
            Scene scene = new Scene(root, 1920, 1080);
            stage.setScene(scene);
            stage.centerOnScreen(); // Ensure the window is centered
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de connexion.");
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        redirectToLogin(event);
    }

    private void clearInputFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        mdpField.clear();
    }

    private void showAlert(String title, String message) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle(title);
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private String generateVerificationToken() {
        return UUID.randomUUID().toString(); // Generates a unique token
    }
}