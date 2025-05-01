package controllers.reclamation;

import javafx.scene.control.*;
import models.reclamation;
import services.reclamationC;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import java.sql.SQLException;

public class DetailsController {
    @FXML private Label lblTitre;
    @FXML private TextArea txtDescription;
    @FXML private Label lblStatus;
    @FXML private Label lblDate;
    @FXML private ProgressBar progressBar;
    @FXML private Button btnRefresh;
    private int currentRecId;// 👈 Nouvel élément

    private reclamation currentRec;

    public void loadData(int idReclamation) {
        this.currentRecId = idReclamation;
        reclamationC service = new reclamationC();
        currentRec = service.getById(idReclamation);

        // Afficher les données existantes
        lblTitre.setText(currentRec.getTitre());
        txtDescription.setText(currentRec.getDescription());
        lblStatus.setText(currentRec.getStatus());
        lblDate.setText(currentRec.getDate().toString());

        // Mettre à jour la ProgressBar
        updateProgressBar();
    }

    private void updateProgressBar() {
        reclamationC service = new reclamationC();
        try {
            boolean hasReponse = service.hasReponse(currentRec.getId());
            boolean hasRemboursement = service.hasRemboursement(currentRec.getId());

            int completedSteps = 0;
            if (currentRec.isRecCompleted()) completedSteps++; // Création
            if (hasReponse) completedSteps++;                   // Réponse existante
            if (hasRemboursement) completedSteps++;             // Remboursement existant
            if (currentRec.isCategorieCompleted()) completedSteps++; // Catégorisation

            progressBar.setProgress(completedSteps / 4.0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    // Méthode pour gérer le remboursement
//    @FXML
//    private void handleRemboursementComplete(ActionEvent event) {
//        currentRec.setRemboursementCompleted(true); // 👈 Marquer l'étape
//
//        try {
//            // Mettre à jour la base de données
//            new reclamationC().update(currentRec);
//
//            // Actualiser l'UI
//            updateProgressBar();
//            lblStatus.setText("Statut : Remboursement effectué ✅");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Erreur lors de la mise à jour du remboursement.");
//        }
//    }
//
//    // Méthode d'alerte
//    private void showAlert(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Erreur");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    // Exemple : Marquer la réponse comme complète
//    @FXML
//    private void handleReponseComplete(ActionEvent event) {
//        currentRec.setReponseCompleted(true);
//        try {
//            new reclamationC().update(currentRec);
//            updateProgressBar(); // Actualiser l'UI
//            lblStatus.setText("Statut : Réponse envoyée"); // Mettre à jour le statut
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    @FXML
    private void handleClose(ActionEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML

    private void handleRefresh(ActionEvent event) {
        // Recharger depuis la base
        reclamationC service = new reclamationC();
        currentRec = service.getById(currentRecId);

        // Mettre à jour l'UI
        lblTitre.setText(currentRec.getTitre());
        txtDescription.setText(currentRec.getDescription());
        lblStatus.setText(currentRec.getStatus());
        lblDate.setText(currentRec.getDate().toString());
        updateProgressBar();
    }
}