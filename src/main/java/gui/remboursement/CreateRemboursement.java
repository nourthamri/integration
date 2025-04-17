package gui.remboursement;

import entities.remboursement;
import javafx.fxml.FXML;
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

    private int reclamationId; // pour lier au bon ID

    public void setReclamationId(int id) {
        this.reclamationId = id;
    }

    @FXML
    public void initialize() {
        addButton.setOnAction(e -> {
            double m = Double.parseDouble(montant.getText());
            LocalDate d = date.getValue();

            remboursement r = new remboursement(reclamationId, m, d);
            remboursementC service = new remboursementC();

            try {
                service.create(r);
                System.out.println("Remboursement ajouté !");
                ((Stage) addButton.getScene().getWindow()).close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
