package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class AjouterEventController {

    @FXML
    private TextField nametxtfield;
    @FXML
    private TextField lieutxtfield;
    @FXML
    private TextField desctxtfield;
    @FXML
    private DatePicker datetf;
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;
    @FXML
    private Button btnChoisirImage;
    @FXML
    private Button annulerbtn;
    @FXML
    private ImageView imageview;
    @FXML
    private Button ajouterbtn;
    @FXML
    private Button btnreturn;

    private final ServiceEvent serviceEvent = new ServiceEvent();
    private File imageFile;

    @FXML
    public void initialize() {
        hourComboBox.getItems().addAll(IntStream.range(0, 24).boxed().toList());
        minuteComboBox.getItems().addAll(IntStream.range(0, 60).boxed().toList());
        hourComboBox.setValue(12);
        minuteComboBox.setValue(0);

        btnChoisirImage.setOnAction(event -> choisirImage());
        ajouterbtn.setOnAction(event -> ajouterEvent());
    }

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            imageFile = selectedFile;
            imageview.setImage(new Image(imageFile.toURI().toString()));
        }
    }

    private void ajouterEvent() {
        String nom = nametxtfield.getText();
        String lieu = lieutxtfield.getText();
        String description = desctxtfield.getText();
        LocalDate date = datetf.getValue();
        Integer hour = hourComboBox.getValue();
        Integer minute = minuteComboBox.getValue();

        if (nom.isEmpty() || lieu.isEmpty() || description.isEmpty() || imageFile == null || date == null || hour == null || minute == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (!Character.isUpperCase(lieu.charAt(0))) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Le lieu doit commencer par une majuscule !");
            return;
        }

        if (nom.length() < 3) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Le nom doit comporter au moins 3 caractères.");
            return;
        }

        if (description.length() < 10) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La description doit comporter au moins 10 caractères.");
            return;
        }

        LocalDateTime dateTime = date.atTime(hour, minute);
        if (dateTime.isBefore(LocalDateTime.now())) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Vous ne pouvez pas planifier un event pour une date passée.");
            return;
        }

        File destinationDir = new File("img/");
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        String newFileName = System.currentTimeMillis() + "_" + imageFile.getName();
        File destinationFile = new File(destinationDir, newFileName);

        try {
            Files.copy(imageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String imgPath = "img/" + newFileName;
            Event E = new Event(nom, imgPath, description, lieu, dateTime);

            serviceEvent.ajouter_t(E);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Evenement ajouté avec succès !");
            clearFields();

            // ✅ Rediriger vers afficherEvent.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AfficherEvent.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ajouterbtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("✅ Image copiée et sauvegardée avec succès : " + imgPath);
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de copier l'image : " + e.getMessage());
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter l'événement : " + e.getMessage());
        }
    }

    private void clearFields() {
        nametxtfield.clear();
        lieutxtfield.clear();
        desctxtfield.clear();
        imageview.setImage(null);
        imageFile = null;
        datetf.setValue(null);
        hourComboBox.setValue(12);
        minuteComboBox.setValue(0);
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReturnButtonClick() {
        Stage stage = (Stage) btnreturn.getScene().getWindow();
        stage.close();
    }
}
