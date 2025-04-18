package gui.reponse;

import entities.reponse;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ReponseC;

import java.sql.SQLException;
import java.time.LocalDate;

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
            // Contrôle de saisie sur le contenu
            String contenuTexte = contenu.getText().trim();
            if (contenuTexte.isEmpty()) {
                showAlert("Le contenu ne peut pas être vide.");
                return;
            }
            if (contenuTexte.length() < 10) {
                showAlert("Le contenu de la réponse doit comporter au moins 10 caractères.");
                return;
            }

            // Contrôle de saisie sur la date
            LocalDate dateReponse = date.getValue();
            if (dateReponse == null || dateReponse.isBefore(LocalDate.now())) {
                showAlert("La date de la réponse doit être aujourd'hui ou dans le futur.");
                return;
            }

            // Si les contrôles passent, on met à jour la réponse
            reponse.setContenu(contenuTexte);
            reponse.setDate(dateReponse);

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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
