package controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilFront {
    @FXML
    private Button btnCreer;

    @FXML
    private Button btnVoir;

    @FXML
    private void allerVersCreation() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/createrec.fxml"));
        Stage stage = (Stage) btnCreer.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    @FXML
    private void allerVersListe() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/listereclamationsfront.fxml"));
        Stage stage = (Stage) btnVoir.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));

    }

    @FXML
    private void loadDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/reclamation/dashboard.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur de chargement du dashboard : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
