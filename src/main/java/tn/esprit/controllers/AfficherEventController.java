package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tn.esprit.entities.Event;
import tn.esprit.entities.User;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.ServiceEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class AfficherEventController {

    @FXML
    public AnchorPane demandeJoinWBContainer;
    @FXML
    public Circle demandeJoinWBImg;
    @FXML
    public Label courseWBJTitle;
    @FXML
    private ListView<Event> listViewEvent;
    private static final ServiceEvent serviceevent = new ServiceEvent(); // Instance of your service class.

    @FXML
    private Button btnajouter;
    @FXML
    private Button btnmodifier;

    @FXML
    public void initialize() {
        if (listViewEvent == null) {
            System.out.println("❌ ERREUR : listView est NULL dans initialize()");
            return;
        }
        listViewEvent.setCellFactory(param -> {
            EventListCellController cellController = new EventListCellController();
            cellController.setAfficherController(this);
            return cellController;
        });

        chargerEvent(); // Loads events, which likely involves a database connection.
        listViewEvent.setOnMouseClicked(event -> {
            Event selectedEvent = listViewEvent.getSelectionModel().getSelectedItem();

        });

        String role ="Admin" ;

        if (role == null || !role.equals("Admin")) {
            btnajouter.setDisable(true);
            btnmodifier.setDisable(true);
        }

    }

    private void openEventDetail(Event selectedEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetailsEvent.fxml"));
            Parent root = loader.load();
            DetailsEventController controller = loader.getController();
            controller.setEvent(selectedEvent);
            controller.setAfficherEventController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'Événement");
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerEvent() {
        try {
            List<Event> events = serviceevent.afficher_t(); // Calls service to fetch events.
            ObservableList<Event> data = FXCollections.observableArrayList(events);
            listViewEvent.setItems(data);
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de charger les evenements : " + e.getMessage());
        }
    }

    private static void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAjouterEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AjouterEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un event");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.setOnHidden((WindowEvent e) -> rafraichirAffichage());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de AjouterEvent.fxml");
        }
    }

    @FXML
    private void handleModifierEvent() {
        Event selectedEvent = listViewEvent.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifierEvent.fxml"));
                Parent root = loader.load();
                ModifierEventController controller = loader.getController();
                controller.setEvent(selectedEvent);
                controller.setAfficherEventController(this);
                Stage stage = new Stage();
                stage.setTitle("Modifier l'événement");
                stage.setScene(new Scene(root));
                stage.setResizable(true);
                stage.setOnHidden((WindowEvent e) -> rafraichirAffichage());
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erreur lors du chargement de ModifierEvent.fxml");
            }
        } else {
            afficherAlerte(Alert.AlertType.WARNING, "Sélectionnez un événement", "Veuillez sélectionner un événement à modifier.");
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


    public void rafraichirAffichage() {
        try {
            List<Event> events = serviceevent.afficher_t(); // Calls service to refresh the list.
            listViewEvent.setItems(FXCollections.observableArrayList(events));
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de rafraîchir l'affichage : " + e.getMessage());
        }
    }
}