package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ModifierEventController {

    @FXML
    private DatePicker datetf;
    @FXML
    private Button modifbtn;
    @FXML
    private Button annulerbtn;
    @FXML
    private TextField nametxtfield;
    @FXML
    private TextField lieutxtfield;
    @FXML
    private TextField desctxtfield;
    @FXML
    private ImageView imageview;
    @FXML
    private ComboBox hourComboBox;
    @FXML
    private ComboBox minuteComboBox;
    @FXML
    private Button btnChoisirImage;
    private AfficherEventController afficherEventController;
    @FXML
    private TextField txtImg;
    @FXML
    private Button btnreturn;

    public void setAfficherEventController(AfficherEventController controller) {
        this.afficherEventController = controller;
    }

    private Event eventSelected;
    private final ServiceEvent serviceEvent = new ServiceEvent();



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
    }

    public void setEvent(Event event) {
        this.eventSelected = event;
        if (event != null) {
            nametxtfield.setText(event.getNom());
            lieutxtfield.setText(event.getLieu());
            desctxtfield.setText(event.getDesc());
            txtImg.setText(event.getImage());
            datetf.setValue(event.getDate().toLocalDate());


            hourComboBox.setValue(String.format("%02d", event.getDate().getHour()));
            minuteComboBox.setValue(String.format("%02d", event.getDate().getMinute()));
        }
    }

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.setTitle("Choisir une image pour l'event");

        File selectedFile = fileChooser.showOpenDialog(txtImg.getScene().getWindow());

        if (selectedFile != null) {

            txtImg.setText(selectedFile.getAbsolutePath());


            Image image = new Image(selectedFile.toURI().toString());
            imageview.setImage(image);
        } else {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Aucune image n'a été sélectionnée !");
        }
    }
    private void modifierEvent() {
        if (eventSelected == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Aucun event sélectionné !");
            return;
        }

        String nom = nametxtfield.getText();
        String lieu = lieutxtfield.getText();
        String description = desctxtfield.getText();
        String img = txtImg.getText();


        if (nom.isEmpty() || lieu.isEmpty() || description.isEmpty() || img.isEmpty()) {
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

        int hour = Integer.parseInt(hourComboBox.getValue().toString());
        int minute = Integer.parseInt(minuteComboBox.getValue().toString());
        LocalDateTime dateTime = datetf.getValue().atTime(hour, minute);
        LocalDateTime today = LocalDateTime.now();
        if (dateTime.isBefore(today)) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Vous ne pouvez pas planifier un event pour une date passée.");
            return;
        }
        eventSelected.setNom(nom);
        eventSelected.setLieu(lieu);
        eventSelected.setDesc(description);
        eventSelected.setImage(img);
        eventSelected.setDate(dateTime);

        try {
            serviceEvent.modifier_t(eventSelected);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "event modifié avec succès !");
            fermerFenetre();
            if (afficherEventController != null) {
                afficherEventController.rafraichirAffichage();
            }

        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de modifier le event : " + e.getMessage());
        }
    }


    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private DetailsEventController detailsEventController;

    private void fermerFenetre() {
        if (detailsEventController != null) {
            detailsEventController.setEvent(eventSelected);
        } else {
            System.out.println("⚠️ `detailsEventController` est NULL, impossible de mettre à jour l'affichage !");
        }

        Stage stage = (Stage) modifbtn.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void annulerModification() {
        Stage stage = (Stage) annulerbtn.getScene().getWindow();
        stage.close();
    }
    public void setDetailsEventController(DetailsEventController controller) {
        this.detailsEventController = controller;
    }
    @FXML
    private void handleReturnButtonClick() {

        Stage stage = (Stage) btnreturn.getScene().getWindow();
        stage.close();
    }

}
