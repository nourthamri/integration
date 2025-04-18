package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Participant;
import tn.esprit.services.ServiceParticipant;
import tn.esprit.services.ServiceEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class listeParticipantControlle implements Initializable {

    @FXML
    private Button btnsupprimerParticipants;
    @FXML
    private Button btnreturn;
    @FXML
    private ImageView btnsupprimerParticipant1;
    @FXML
    private ListView<Participant> listViewParticipant;

    private final ServiceParticipant serviceparticipant = new ServiceParticipant();
    private final ServiceEvent serviceevent = new ServiceEvent();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        chargerEvents();

        listViewParticipant.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {

                        String userName = serviceparticipant.getUserNameById(item.getId_user());

                        Label userLabel = new Label("👤 participant: " + userName);
                        userLabel.getStyleClass().add("event-label");

                        HBox hBox = new HBox(25, userLabel);
                        hBox.setAlignment(Pos.CENTER_LEFT);

                        setGraphic(hBox);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        setText("Erreur chargement nom utilisateur");
                    }
                }
                String role ="Amin" ;

                if (role == null || !role.equals("Admin")) {
                    btnsupprimerParticipants.setDisable(true);

                }

                }


        });


    }
    private Event event;

    public void setEvent(Event event) {
        if (event == null) {
            System.out.println("❌ `setEvent()` reçu un `null` !");
            return;
        }

        this.event = event;
        System.out.println("✅ Événement reçu avec ID : " + event.getId_event());

        chargerParticipants();
    }


    private void chargerParticipants() {
        try {
            List<Participant> participants = serviceevent.getParticipantsByEvent(event.getId_event());
            ObservableList<Participant> data = FXCollections.observableArrayList(participants);
            listViewParticipant.setItems(data);


            System.out.println("Vérification ListView après chargement :");
            for (Participant p : listViewParticipant.getItems()) {
                System.out.println(" Participant dans ListView : " + p);
            }

            System.out.println("✅ Participants chargés !");
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Impossible de charger les participants : " + e.getMessage());
        }
    }


    private void chargerEvents() {
        try {
            List<Participant> participants = serviceparticipant.afficher_t();

            for (Participant participant : participants) {
                String nomEvent = serviceevent.getEventById(participant.getId_event()).getNom();
                System.out.println(" Participant chargé : ID = " + participant.getId_part());
            }

            ObservableList<Participant> data = FXCollections.observableArrayList(participants);
            listViewParticipant.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Impossible de charger les participants : " + e.getMessage());
        }
    }

    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void supprimerParticipant() {
        Participant participantSelectionne = listViewParticipant.getSelectionModel().getSelectedItem();

        if (participantSelectionne == null) {
            afficherAlerte("Avertissement", "Veuillez sélectionner un participant valide.");
            return;
        }

        System.out.println("🔹 ID sélectionné pour suppression : " + participantSelectionne.getId_part());

        if (participantSelectionne.getId_part() <= 0) {
            afficherAlerte("Erreur", "L'ID du participant est invalide.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer ce participant ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceparticipant.supprimer_t(participantSelectionne.getId_part());
                afficherAlerte("Succès", "Participant supprimé avec succès !");
                listViewParticipant.getItems().remove(participantSelectionne);
                rafraichirAffichage();
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de supprimer le participant : " + e.getMessage());
            }
        }
    }


    private void rafraichirAffichage() {
        Task<ObservableList<Participant>> task = new Task<>() {
            @Override
            protected ObservableList<Participant> call() throws SQLException {
                List<Participant> participants = serviceevent.getParticipantsByEvent(event.getId_event());
                for (Participant participant : participants) {
                    String nomEvant = serviceevent.getEventById(participant.getId_event()).getNom();
                }
                return FXCollections.observableArrayList(participants);
            }
        };

        task.setOnSucceeded(event -> listViewParticipant.setItems(task.getValue()));
        task.setOnFailed(event -> afficherAlerte("Erreur SQL", "Impossible de rafraîchir l'affichage"));

        new Thread(task).start();
    }


    private void afficherAlerte(String avertissement, String s) {
    }
    @FXML
    private void handleReturnButtonClick() {

        Stage stage = (Stage) btnreturn.getScene().getWindow();
        stage.close();
    }

}