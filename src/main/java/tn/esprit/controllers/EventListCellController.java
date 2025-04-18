package tn.esprit.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Event;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class EventListCellController extends ListCell<Event> {

    private final HBox content;
    private final ImageView imageView;
    private final Label nomLabel;
    private final Label dateLabel;
    private final Button detailsButton;


    public EventListCellController() {
        super();

        imageView = new ImageView();
        imageView.setFitHeight(100); // Adjusted size
        imageView.setFitWidth(100);  // Adjusted size
        imageView.setPreserveRatio(true);

        detailsButton = new Button("Voir Détails");
        detailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-font-size: 12px;");

        nomLabel = new Label();
        nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        dateLabel = new Label();
        dateLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        detailsButton.setOnAction(event -> {
            if (getItem() != null) {
                openEventDetail(getItem());
            }
        });

        VBox textContainer = new VBox(nomLabel, dateLabel, detailsButton);
        textContainer.setSpacing(5);

        content = new HBox(imageView, textContainer);
        content.setSpacing(10);
        content.setMouseTransparent(false); // Explicitly set to false to allow clicks
    }

    private AfficherEventController afficherController;

    public void setAfficherController(AfficherEventController afficherController) {
        this.afficherController = afficherController;
    }

    private void openEventDetail(Event selectedEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetailsEvent.fxml"));
            Parent root = loader.load();

            DetailsEventController controller = loader.getController();
            controller.setEvent(selectedEvent);
            if (afficherController != null) {
                controller.setAfficherEventController(afficherController);
            } else {
                System.out.println("⚠️ afficherController est NULL, impossible de rafraîchir !");
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'Événement");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Event event, boolean empty) {
        super.updateItem(event, empty);
        if (empty || event == null) {
            setGraphic(null);
        } else {
            nomLabel.setText(event.getNom());
            dateLabel.setText("📅 " + event.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            String imagePath = event.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    System.err.println("❌ Image introuvable : " + imagePath);
                    imageView.setImage(null);
                }
            } else {
                imageView.setImage(null); // Clear image if no path
            }

            setGraphic(content);
        }
    }
}