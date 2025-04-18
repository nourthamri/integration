package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.SessionManager;
import tn.esprit.entities.User;
import tn.esprit.services.ServiceParticipant;
import tn.esprit.services.ServiceEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class DetailsEventController {
    @FXML
    private Label nomLabelDetail;
    @FXML
    private Label lieuLabelDetail;
    @FXML
    private Label desLabelDetail;
    @FXML
    private Label dateLabelDetail;

    private Event event;
    private final ServiceParticipant serviceParticipant=new ServiceParticipant();
    private final ServiceEvent serviceevent = new ServiceEvent();
    private AfficherEventController affichereventcontroller ;


    @FXML
    private Button btnsupp;
    @FXML
    private Button btnpart;
    @FXML
    private Pane mainPageContainer;

    @FXML
    private ImageView ImageViewDetail;
    private AfficherEventController afficherEventController;
    @FXML
    private Button btnmodifier;
    @FXML
    private Button btnparticiper;


    @FXML
    private Label userRoleLabel;
    @FXML
    private Button btnreturn;

    @FXML
    public void initialize() {
        btnmodifier.setOnAction(event -> modifierEvent());
        btnsupp.setOnAction(event->supprimerEvent());
        affichereventcontroller = new AfficherEventController();



            userRoleLabel.setText("Utilisateur non connecté");


        String role ="Admin" ;

        if (role == null || !role.equals("Admin")) {
            btnmodifier.setDisable(true);
            btnsupp.setDisable(true);



        }


    }



    public void setEvent(Event event) {
        this.event = event;
        System.out.println("✅ Détails mis à jour pour l'événement ID : " + event.getId_event());

        if (event != null) {
            nomLabelDetail.setText(event.getNom());
            lieuLabelDetail.setText(event.getLieu());
            desLabelDetail.setText(event.getDesc());
            dateLabelDetail.setText(event.getDate().toString());

            // Charger l'image
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                Image image = new Image("file:" + event.getImage());
                ImageViewDetail.setImage(image);
            }
            updateParticipationButton();
        }
    }

    @FXML
    private void Participer() {
        if (event == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucun événement sélectionné.");
            return;
        }

        int idEvent = event.getId_event();
        int idUser  = 1;

        try {

            if (serviceParticipant.aDejaParticipe(idUser)) {
                afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Vous avez déjà participé à un événement !");
                return;
            }


            serviceevent.participer(1,idEvent);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Vous avez participé à l'événement avec succès !");
            updateParticipationButton();
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de participer : " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }



    @FXML
    private void supprimerEvent() {
        if (event == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cet événement ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceevent.supprimer_t(event.getId_event());

                if (afficherEventController != null) {
                    afficherEventController.rafraichirAffichage();
                } else {
                    System.out.println("⚠️ afficherEventController est NULL, impossible de rafraîchir !");
                }

                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Événement supprimé avec succès !");


                Stage stage = (Stage) btnsupp.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'événement : " + e.getMessage());
            }
        }
    }



    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setAfficherEventController(AfficherEventController controller) {
        if (controller != null) {
            this.afficherEventController = controller;
        } else {
            System.out.println("⚠️ ERREUR : afficherEventController reçu est NULL !");
        }
    }

    @FXML
    private void modifierEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifierEvent.fxml"));
            Parent root = loader.load();

            ModifierEventController controller = loader.getController();
            controller.setDetailsEventController(this);
            controller.setEvent(event);
            controller.setAfficherEventController(afficherEventController);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier l'Événement");
            stage.setResizable(false);


            stage.setOnHidden(e -> {
                if (afficherEventController != null) {
                    afficherEventController.rafraichirAffichage();
                }
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherListeParticipants() {
        if (event == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucun événement sélectionné.");
            return;
        }

        System.out.println("🔹 ID de l'événement envoyé : " + event.getId_event());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/listeParticipants.fxml"));
            Parent root = loader.load();

            listeParticipantControlle controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Participants de " + event.getNom());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher la liste des participants.");
        }
    }
    @FXML
    private void updateParticipationButton() {
        int idUser = 1;
        try {
            boolean participeDeja = serviceParticipant.aDejaParticipe(idUser);
            if (participeDeja) {
                btnparticiper.setText("Annuler Participation");
                btnparticiper.setOnAction(e -> annulerParticipation());
                btnparticiper.setTooltip(new Tooltip("Annuler votre participation à cet événement"));
            } else {
                btnparticiper.setText("Participer");
                btnparticiper.setOnAction(e -> Participer());
                btnparticiper.setTooltip(new Tooltip("Participer à cet événement"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la vérification de participation : " + e.getMessage());
        }
    }
    @FXML
    private void annulerParticipation() {
        int id_User = 1;

        try {
            serviceParticipant.annulerParticipation(id_User); // Crée cette méthode dans ton ServiceParticipant
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Participation annulée avec succès !");
            updateParticipationButton();

        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'annuler la participation : " + e.getMessage());
        }
    }

    @FXML
    private void handleReturnButtonClick() {

        Stage stage = (Stage) btnreturn.getScene().getWindow();
        stage.close();
    }








}
