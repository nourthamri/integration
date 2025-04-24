package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.example.entities.SessionManager;
import org.example.entities.User;
import org.example.services.ServiceUser;
import org.example.utils.MyDataBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Button forgotPasswordButton;

    private static final String CONFIG_FILE = "user_session.properties";

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setWidth(1920);
            stage.setHeight(1080);
            stage.centerOnScreen();
        });
        loadSavedLogin();
    }

    @FXML
    private void handleSignupButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inscription.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        ServiceUser serviceUser = new ServiceUser();

        if (serviceUser.isUserBlocked(email)) {
            System.out.println("❌ Your account is blocked!");
            return;
        }
        if (!serviceUser.isUserVerified(email)) {
            System.out.println("❌ Please verify your email before logging in!");
            return;
        }

        User user = validateLogin(email, password);
        if (user != null) {

            SessionManager.getInstance().setCurrentUser(user);
            System.out.println("User found: " + user.getId());

            if (rememberMeCheckBox.isSelected()) {
                saveLoginSession(email, password);
            } else {
                clearLoginSession();
            }
            navigateToUserDashboard(user, event);
        } else {
            System.out.println("Login failed. Check your email and password.");
        }
    }

    private void saveLoginSession(String email, String password) {
        try (FileOutputStream fileOut = new FileOutputStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.setProperty("email", email);
            properties.setProperty("password", password);
            properties.store(fileOut, "User Session");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSavedLogin() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return;

        try (FileInputStream fileIn = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(fileIn);

            String savedEmail = properties.getProperty("email");
            String savedPassword = properties.getProperty("password");

            if (savedEmail != null && savedPassword != null) {
                emailField.setText(savedEmail);
                passwordField.setText(savedPassword);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearLoginSession() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private User validateLogin(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";

        try (Connection connection = MyDataBase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("email"),

                            resultSet.getString("password"),

                            resultSet.getString("username"),
                            resultSet.getString("lastName"),
                            resultSet.getString("phoneNumber"),
                            resultSet.getString("image")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void navigateToUserDashboard(User user, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profil.fxml"));
            Parent root = loader.load();



            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ForgotPassword.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Forgot Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}