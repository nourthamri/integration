package controllers.reclamation;

import models.CategorieReclamation;
import models.reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CategorieReclamationC;
import services.reclamationC;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Updaterec {

    @FXML
    private TextField titre;

    @FXML
    private TextArea desc;


    @FXML
    private TextField status;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private Button update;

    @FXML
    private void buttonHover() {
        update.setStyle("-fx-background-color: #5591e6; -fx-text-fill: white;");
    }

    @FXML
    private void buttonExit() {
        update.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white;");
    }


    private reclamation reclamation;
    private final reclamationC service = new reclamationC();
    private final CategorieReclamationC catService = new CategorieReclamationC();

    private Runnable onUpdateSuccess;

    public void setReclamation(reclamation r) {
        this.reclamation = r;
        titre.setText(r.getTitre());
        desc.setText(r.getDescription());
        date.setValue(r.getDate());
        status.setText(r.getStatus());
    }

    public void setOnUpdateSuccess(Runnable callback) {
        this.onUpdateSuccess = callback;
    }

    @FXML
    public void initialize() {
        try {
            List<CategorieReclamation> categories = catService.readAll();
            ObservableList<String> noms = FXCollections.observableArrayList();
            for (CategorieReclamation c : categories) {
                noms.add(c.getNom());
            }
            categorieCombo.setItems(noms);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des catégories : " + e.getMessage());
        }

        update.setOnAction(e -> {
            // === CONTRÔLE DE SAISIE ===
            if (titre.getText().isEmpty() || desc.getText().isEmpty() || status.getText().isEmpty()) {
                showAlert("Veuillez remplir tous les champs de texte.");
                return;
            }

            if (date.getValue() == null || date.getValue().isBefore(LocalDate.now())) {
                showAlert("La date doit être aujourd'hui ou dans le futur.");
                return;
            }

            if (categorieCombo.getValue() == null) {
                showAlert("Veuillez sélectionner une catégorie.");
                return;
            }

            // === SI OK, ENREGISTRER ===
            reclamation.setTitre(titre.getText());
            reclamation.setDescription(desc.getText());
            reclamation.setStatus(status.getText());
            reclamation.setDate(date.getValue());

            try {
                service.update(reclamation);
                if (onUpdateSuccess != null) {
                    onUpdateSuccess.run();
                }
                ((Stage) update.getScene().getWindow()).close();
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la mise à jour : " + ex.getMessage());
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
