package controllers.reclamation;

import models.CategorieReclamation;
import models.reclamation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.reclamationC;
import services.CategorieReclamationC;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Createrec implements Initializable {

    @FXML
    private TextField titre;

    @FXML
    private TextArea desc;

    @FXML
    private DatePicker date;

    @FXML
    private Button add;

    @FXML
    private ComboBox<CategorieReclamation> categorieCombo;

    @FXML
    public void buttonHover(MouseEvent event) {
        add.setStyle("-fx-background-color: #4a82d1; -fx-text-fill: white; -fx-padding: 12px 24px;");
    }

    @FXML
    public void buttonExit(MouseEvent event) {
        add.setStyle("-fx-background-color: #6fa3ef; -fx-text-fill: white; -fx-padding: 12px 24px;");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CategorieReclamationC service = new CategorieReclamationC();
        try {
            categorieCombo.getItems().addAll(service.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        add.setOnAction(event -> {
            try {
                ajouterReclamation();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void ajouterReclamation() throws SQLException {
        String t = titre.getText();
        String d = desc.getText();
        LocalDate ld = date.getValue();
        CategorieReclamation categorie = categorieCombo.getValue();

        // === CONTRÔLE DE SAISIE ===
        if (t.isEmpty() || d.isEmpty()) {
            showAlert("Veuillez remplir tous les champs de texte.");
            return;
        }

        if (ld == null || ld.isBefore(LocalDate.now())) {
            showAlert("La date doit être aujourd'hui ou dans le futur.");
            return;
        }

        if (categorie == null) {
            showAlert("Veuillez sélectionner une catégorie.");
            return;
        }

        // Données par défaut
        String statut = "en attente";
        int userId = 1;
        int categorieId = categorie.getId();

        reclamation r = new reclamation(userId, t, d, statut, ld);
        r.setCategorieId(categorieId);

        reclamationC rc = new reclamationC();
        rc.create(r);

        // Nettoyage du formulaire
        titre.clear();
        desc.clear();
        date.setValue(null);
        categorieCombo.setValue(null);

        System.out.println("Réclamation ajoutée !");

        // Changement de scène vers la liste des réclamations
        switchScene("/reclamation/reclamationliste.fxml", "Liste des Réclamations");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) add.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur de navigation vers : " + fxmlPath);
        }
    }
}
