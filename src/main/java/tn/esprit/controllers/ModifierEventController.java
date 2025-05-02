package tn.esprit.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ModifierEventController {

    @FXML private DatePicker datetf;
    @FXML private Button modifbtn;
    @FXML private Button annulerbtn;
    @FXML private TextField nametxtfield;
    @FXML private TextField desctxtfield;
    @FXML private ImageView imageview;
    @FXML private ComboBox<String> hourComboBox;
    @FXML private ComboBox<String> minuteComboBox;
    @FXML private Button btnChoisirImage;
    @FXML private WebView mapView;

    private Event eventSelected;
    private final ServiceEvent serviceEvent = new ServiceEvent();
    private AfficherEventController afficherEventController;
    private DetailsEventController detailsEventController;

    private double selectedLat;
    private double selectedLng;

    public void setAfficherEventController(AfficherEventController controller) {
        this.afficherEventController = controller;
    }

    public void setDetailsEventController(DetailsEventController controller) {
        this.detailsEventController = controller;
    }

    @FXML
    public void initialize() {
        btnChoisirImage.setOnAction(event -> choisirImage());
        modifbtn.setOnAction(event -> modifierEvent());
        annulerbtn.setOnAction(event -> annulerModification());

        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(String.format("%02d", i));
        }

        WebEngine engine = mapView.getEngine();
        engine.setJavaScriptEnabled(true);
        JavaBridge bridge = new JavaBridge();

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED && eventSelected != null) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", bridge);
                engine.executeScript("setMapCenter(" + eventSelected.getLatitude() + "," + eventSelected.getLongitude() + ")");
            }
        });

        String mapFilePath = getClass().getResource("/map.html").toExternalForm();
        engine.load(mapFilePath);
    }

    public void setEvent(Event event) {
        this.eventSelected = event;
        if (event != null) {
            nametxtfield.setText(event.getNom());
            desctxtfield.setText(event.getDescription());
            datetf.setValue(event.getDate().toLocalDate());
            hourComboBox.setValue(String.format("%02d", event.getDate().getHour()));
            minuteComboBox.setValue(String.format("%02d", event.getDate().getMinute()));

            selectedLat = event.getLatitude();
            selectedLng = event.getLongitude();

            if (event.getImage() != null && !event.getImage().isEmpty()) {
                imageview.setImage(new Image("file:" + event.getImage()));
            }
        }
    }

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.setTitle("Choisir une image pour l'événement");

        File selectedFile = fileChooser.showOpenDialog(imageview.getScene().getWindow());
        if (selectedFile != null) {
            imageview.setImage(new Image(selectedFile.toURI().toString()));
            eventSelected.setImage(selectedFile.getAbsolutePath());
        } else {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune image", "Aucune image sélectionnée.");
        }
    }

    private void modifierEvent() {
        if (eventSelected == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
            return;
        }

        String nom = nametxtfield.getText();
        String description = desctxtfield.getText();

        if (nom.isEmpty() || description.isEmpty()) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (nom.length() < 3 || description.length() < 10) {
            afficherAlerte(Alert.AlertType.ERROR, "Validation", "Nom ou description trop court.");
            return;
        }

        int hour = Integer.parseInt(hourComboBox.getValue());
        int minute = Integer.parseInt(minuteComboBox.getValue());
        LocalDateTime dateTime = datetf.getValue().atTime(hour, minute);

        if (dateTime.isBefore(LocalDateTime.now())) {
            afficherAlerte(Alert.AlertType.WARNING, "Date invalide", "La date est dans le passé.");
            return;
        }

        eventSelected.setNom(nom);
        eventSelected.setDescription(description);
        eventSelected.setLatitude(selectedLat);
        eventSelected.setLongitude(selectedLng);
        eventSelected.setDate(dateTime);

        try {
            serviceEvent.modifier_t(eventSelected);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Événement modifié !");
            fermerFenetre();
            if (afficherEventController != null) afficherEventController.rafraichirAffichage();
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Modification échouée : " + e.getMessage());
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        if (detailsEventController != null) {
            detailsEventController.setEvent(eventSelected);
        }
        Stage stage = (Stage) modifbtn.getScene().getWindow();
        stage.close();
    }

    private void annulerModification() {
        fermerFenetre();
    }

    public class JavaBridge {
        public void onLocationSelected(String placeName, double lat, double lng) {
            selectedLat = lat;
            selectedLng = lng;
            System.out.println("Coordonnées sélectionnées : " + placeName + " (" + lat + ", " + lng + ")");
        }
    }
}
