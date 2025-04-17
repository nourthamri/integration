package gui.reclamation;

import entities.CategorieReclamation;
import entities.reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CategorieReclamationC;
import services.reclamationC;

import java.sql.SQLException;
import java.util.List;

public class Updaterec {

    @FXML
    private TextField titre;

    @FXML
    private TextField desc;

    @FXML
    private TextField status;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private Button update;

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

        // Si tu veux stocker la catégorie dans la réclamation, fais ici :
        // categorieCombo.setValue(r.getCategorie());
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
            reclamation.setTitre(titre.getText());
            reclamation.setDescription(desc.getText());
            reclamation.setStatus(status.getText());
            reclamation.setDate(date.getValue());

            // Si tu veux stocker la catégorie sélectionnée
            // reclamation.setCategorie(categorieCombo.getValue());

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
}
