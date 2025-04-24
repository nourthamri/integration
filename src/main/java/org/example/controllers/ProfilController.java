package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.entities.SessionManager;
import org.example.entities.User;
import org.example.services.ServiceUser;

import java.io.File;
import java.io.IOException;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.sql.SQLException;
import java.util.Objects;

public class ProfilController {


    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;
    @FXML
    private ImageView profilImageView;
    private File selectedImageFile;


    private User currentUser;

    // Initializes the controller and sets user details
    @FXML
    private void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            System.out.println("✅ Logged in user: " + currentUser);
            System.out.println("🆔 User ID: " + currentUser.getId());
            setUserDetails(currentUser);

            // 🖼️ Charger l'image de profil si elle existe
            if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                File imageFile = new File("src/main/resources/images/" + currentUser.getImage());
                if (imageFile.exists()) {
                    profilImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("⚠️ Image not found: " + imageFile.getAbsolutePath());
                }
            }
        } else {
            System.out.println("❌ No user is currently logged in.");
        }
    }

    // Sets the current user's details in the UI
    private void setUserDetails(User user) {

        emailField.setText(user.getEmail());
        usernameField.setText(user.getUsername());
        lastNameField.setText(user.getLastName());
        phoneNumberField.setText(user.getPhoneNumber());

        // Load the user's profile image
        String imagePath = user.getImage(); // Assuming this returns the image path
        if (imagePath != null && !imagePath.isEmpty()) {
            profilImageView.setImage(new Image("file:" + imagePath));
        } else {
            profilImageView.setImage(new Image("/img/image.png")); // Default image if user doesn't have one
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        ServiceUser userService = new ServiceUser();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        // If an image was selected, save the image path
        if (selectedImageFile != null) {
            currentUser.setImage(selectedImageFile.getAbsolutePath());  // Save the file path to the user's profile
        }

        // Update current user object with new values from the form
        currentUser.setEmail(email);
        currentUser.setUsername(username);
        currentUser.setLastName(lastName);
        currentUser.setPhoneNumber(phoneNumber);

        // Call service to update user in database
        if (userService.modifieru(currentUser)) {
            showAlert("Succès", "Votre profil a été mis à jour avec succès!");

            // Load the profile page after successful update
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Profil.fxml"));
                Parent profilePage = loader.load();
                Scene profileScene = new Scene(profilePage);

                // Get the current stage and set the profile scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(profileScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Erreur", "Impossible de charger la page du profil.");
            }
        } else {
            showErrorAlert("Erreur", "Échec de la mise à jour, veuillez réessayer.");
        }
    }

    // Handles deleting the user's account
    @FXML
    public void handleDelete(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer votre compte ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ServiceUser serviceUser = new ServiceUser();
                System.out.println("🗑 Trying to delete user with ID: " + currentUser.getId());

                if (serviceUser.supprimeru(currentUser.getId())) {
                    showAlert("Succès", "Votre compte a été supprimé avec succès.");
                    goToLogin(event);
                } else {
                    showErrorAlert("Erreur", "Échec de la suppression de votre compte. Veuillez réessayer.");
                }
            }
        });
    }

    // Shows an error alert
    private void showErrorAlert(String title, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }

    // Shows a success alert
    private void showAlert(String title, String content) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(null);
        successAlert.setContentText(content);
        successAlert.showAndWait();
    }

    // Navigates back to the login screen
    private void goToLogin(ActionEvent event) {
        try {
            SessionManager.getInstance().logout();  // Ensure the session is cleared
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Erreur lors de la redirection vers la page de connexion.");
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) {

        SessionManager.getInstance().logout();


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de connexion : " + e.getMessage());
        }
    }
    public void handleEditProfil(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/update.fxml")));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }
    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            profilImageView.setImage(image);

            // Save image path as String (or you could upload it to server if needed)
            currentUser.setImage(selectedImageFile.getAbsolutePath());
        }
    }
    public void handleEditPassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update_password.fxml"));
        Parent root = loader.load();


        UpdatePasswordController controller = loader.getController();
        controller.setUserDetails(currentUser);


        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }
    @FXML
    private void loadAjoutLoginHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajoutLoginHistory.fxml"));
            Parent root = loader.load();

            // Assuming you have a stage to load this scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void del(ActionEvent actionEvent) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirmation de la suppression");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer votre compte " +
                currentUser.getId()+ "?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ServiceUser serviceUser = new ServiceUser();


                boolean deleted = serviceUser.supprimeru(currentUser.getId());

                if (deleted) {

                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Suppression réussie");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Votre compte a été supprimé avec succès !");
                    infoAlert.showAndWait();


                    goToLogin(actionEvent);
                } else {
                    showErrorAlert("Erreur lors de la suppression du compte", "Une erreur est survenue. Veuillez réessayer.");
                }
            }
        });
    }
    @FXML
    private void handleOuvrirHome(ActionEvent event) throws IOException {
        Parent trajetPage = FXMLLoader.load(getClass().getResource("/view/HomePage.fxml"));
        Scene scene = new Scene(trajetPage);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    private static void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}