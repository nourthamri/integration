package gui.remboursement;

import entities.remboursement;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.remboursementC;

import java.sql.SQLException;
import java.time.LocalDate;

public class CreateRemboursement {

    @FXML
    private TextField montant;

    @FXML
    private DatePicker date;

    @FXML
    private Button addButton;

    private int reclamationId;

    public void setReclamationId(int id) {
        this.reclamationId = id;
    }

    @FXML
    public void initialize() {
        addButton.setOnAction(e -> {
            try {
                // Contrôle de saisie
                String montantText = montant.getText().trim();
                if (montantText.isEmpty()) {
                    showAlert("Veuillez saisir un montant.");
                    return;
                }

                double m = Double.parseDouble(montantText);
                if (m <= 0) {
                    showAlert("Le montant doit être un nombre positif.");
                    return;
                }

                LocalDate d = date.getValue();
                if (d == null || d.isBefore(LocalDate.now())) {
                    showAlert("La date doit être aujourd'hui ou ultérieure.");
                    return;
                }

                remboursement r = new remboursement(reclamationId, m, d);
                remboursementC service = new remboursementC();
                service.create(r);
                System.out.println("Remboursement ajouté !");
                ((Stage) addButton.getScene().getWindow()).close();

            } catch (NumberFormatException ex) {
                showAlert("Le montant doit être un nombre valide.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur lors de l'ajout du remboursement.");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
