package gui.reponse;

import entities.reponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ReponseC;

import java.sql.SQLException;

public class Updaterep {

    @FXML
    private TextField contenu;

    @FXML
    private DatePicker date;

    @FXML
    private Button update;

    private reponse reponse; // la réponse à modifier
    private final ReponseC service = new ReponseC();
    private Runnable onUpdateSuccess; // callback pour rafraîchir la liste

    // Cette méthode est appelée pour transmettre la réponse à modifier
    public void setReponse(reponse r) {
        this.reponse = r;
        contenu.setText(r.getContenu());
        date.setValue(r.getDate());
    }

    public void setOnUpdateSuccess(Runnable callback) {
        this.onUpdateSuccess = callback;
    }

    @FXML
    public void initialize() {
        update.setOnAction(e -> {
            reponse.setContenu(contenu.getText());
            reponse.setDate(date.getValue());

            try {
                service.update(reponse);
                if (onUpdateSuccess != null) {
                    onUpdateSuccess.run();
                }
                ((Stage) update.getScene().getWindow()).close();
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la mise à jour de la réponse : " + ex.getMessage());
            }
        });
    }
}
