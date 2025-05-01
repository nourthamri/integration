package controllers.reclamation;

import models.reclamation;
import services.reclamationC;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class DetailsController {
    @FXML private Label lblTitre;
    @FXML private TextArea txtDescription;
    @FXML private Label lblStatus; // 👈 Vérifiez l'annotation @FXML
    @FXML private Label lblDate;

    public void loadData(int idReclamation) {
        reclamationC service = new reclamationC(); // Classe avec majuscule
        reclamation rec = service.getById(idReclamation);

        lblTitre.setText(rec.getTitre());
        txtDescription.setText(rec.getDescription());
        lblStatus.setText(rec.getStatus()); // 👈 Afficher le statut
        lblDate.setText(rec.getDate().toString());
    }

    @FXML
    private void handleClose(ActionEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}