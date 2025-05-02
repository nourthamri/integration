package tn.esprit.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.IntStream;

public class AjouterEventController {

    @FXML
    private TextField nametxtfield;
    @FXML
    private WebView mapView;
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


    private double selectedLat = 0;
    private double selectedLng = 0;
    private File imageFile;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    @FXML
    public void initialize() {
        hourComboBox.getItems().addAll(IntStream.range(0, 24).boxed().toList());
        minuteComboBox.getItems().addAll(IntStream.range(0, 60).boxed().toList());
        hourComboBox.setValue(12);
        minuteComboBox.setValue(0);

        btnChoisirImage.setOnAction(event -> choisirImage());
        ajouterbtn.setOnAction(event -> ajouterEvent());

        // Get the WebEngine associated with the WebView
        WebEngine engine = mapView.getEngine();
        engine.setJavaScriptEnabled(true);

        // Création du label pour afficher la position sélectionnée
        Label coordsLabel = new Label("Aucun lieu sélectionné");
        coordsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");
        VBox.setMargin(coordsLabel, new Insets(5, 0, 0, 170)); // Position sous la carte

        // Ajoute dynamiquement le label à la scène
        ((VBox) mapView.getParent().getParent()).getChildren().add(coordsLabel);

        // Bridge Java <-> JavaScript
        JavaBridge bridge = new JavaBridge();
        bridge.setLabel(coordsLabel);

        // Écouteur de chargement de la carte
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", bridge);
            }
        });

        // Chargement du fichier map.html (chemin à adapter si nécessaire)
        String mapFilePath = getClass().getResource("/map.html").toExternalForm();
        if (mapFilePath != null) {
            engine.load(mapFilePath);
        } else {
            System.out.println("❌ Erreur : map.html introuvable.");
        }
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
        String description = desctxtfield.getText();
        LocalDate date = datetf.getValue();
        Integer hour = hourComboBox.getValue();
        Integer minute = minuteComboBox.getValue();

        // ✅ Validation
        if (nom.isEmpty() || description.isEmpty() || imageFile == null || date == null || hour == null || minute == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
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

        if (selectedLat == 0 && selectedLng == 0) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un lieu sur la carte.");
            return;
        }

        LocalDateTime dateTime = date.atTime(hour, minute);
        if (dateTime.isBefore(LocalDateTime.now())) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Vous ne pouvez pas planifier un événement dans le passé.");
            return;
        }

        // ✅ Image path
        File destinationDir = new File("img/");
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        String newFileName = System.currentTimeMillis() + "_" + imageFile.getName();
        File destinationFile = new File(destinationDir, newFileName);

        try {
            Files.copy(imageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String imgPath = "img/" + newFileName;

            // ✅ Crée l'objet Event avec les coordonnées sélectionnées
            Event event = new Event(nom, imgPath, description, dateTime, selectedLat, selectedLng);

            // ✅ Call service to add the event
            boolean success = serviceEvent.ajouter_t(event);
            if (success) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès !");
                clearFields();

                // Redirige vers afficherEvent.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AfficherEvent.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ajouterbtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout de l'événement.");
            }

        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'ajout de l'événement : " + e.getMessage());
        }
    }

    private void clearFields() {
        nametxtfield.clear();
        desctxtfield.clear();
        imageview.setImage(null);
        imageFile = null;
        datetf.setValue(null);
        hourComboBox.setValue(12);
        minuteComboBox.setValue(0);
        selectedLat = 0;
        selectedLng = 0;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public class JavaBridge {
        private Label coordsLabel;

        public void setLabel(Label label) {
            this.coordsLabel = label;
        }

        public void onLocationSelected(String placeName, double lat, double lng) {
            selectedLat = lat;
            selectedLng = lng;
            System.out.println("📍 Lieu sélectionné : " + placeName + " (" + lat + ", " + lng + ")");
            if (coordsLabel != null) {
                coordsLabel.setText("Lieu sélectionné : " + placeName);
            }
        }
    }

    @FXML
    private void handleReturnButtonClick() {
        Stage stage = (Stage) btnreturn.getScene().getWindow();
        stage.close();
    }
}
