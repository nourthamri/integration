package services;


import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;
import org.controlsfx.control.Notifications;
import javafx.application.Platform;
import javafx.util.Duration;
import controllers.reclamation.DetailsController;


public class NotificationService {
    private static NotificationService instance;

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void showNotification(String title, String message, int idReclamation) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .hideAfter(Duration.seconds(5))
                    .onAction(e -> openReclamationDetails(idReclamation)) // 👈 Action au clic
                    .show();
        });
    }
    private void openReclamationDetails(int idReclamation) {
        // Exemple : Charger la vue des détails
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/details.fxml"));
            Parent root = loader.load();
            DetailsController controller = loader.getController();
            controller.loadData(idReclamation);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}