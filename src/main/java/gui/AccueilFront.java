package gui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/createrecfront.fxml"));
        Stage stage = (Stage) btnCreer.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    @FXML
    private void allerVersListe() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/listereclamationsfront.fxml"));
        Stage stage = (Stage) btnVoir.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

}
