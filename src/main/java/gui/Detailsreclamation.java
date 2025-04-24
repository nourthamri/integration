package gui;

import entities.remboursement;
import entities.reclamation;
import entities.reponse;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import services.reclamationC;
import services.remboursementC;
import services.ReponseC;

import java.sql.SQLException;
import java.util.List;

public class Detailsreclamation {

    @FXML
    private TextArea txtReponse;

    @FXML
    private TextArea txtRemboursement;

    private reclamation reclamation;

    public void setReclamation(reclamation r) {
        this.reclamation = r;

        try {
            ReponseC reponseService = new ReponseC();
            List<reponse> reponses = reponseService.readByReclamationId(r.getId());
            if (!reponses.isEmpty()) {
                reponse rep = reponses.get(0);
                txtReponse.setText("📅 " + rep.getDate() + "\n\n" + rep.getContenu());
            }

            remboursementC remService = new remboursementC();
            List<remboursement> remboursements = remService.readByReclamationId(r.getId());
            if (!remboursements.isEmpty()) {
                remboursement rem = remboursements.get(0);
                txtRemboursement.setText("💰 " + rem.getMontant() + "€\n📅 " + rem.getDate());
            }

        } catch (SQLException e) {
            txtReponse.setText("Erreur chargement réponse.");
            txtRemboursement.setText("Erreur chargement remboursement.");
            e.printStackTrace();
        }
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) txtReponse.getScene().getWindow();
        stage.close();
    }
}
