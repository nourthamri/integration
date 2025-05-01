package controllers;

import models.CategorieReclamation;
import models.User;
import models.reclamation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CategorieReclamationC;
import services.reclamationC;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Createrecfront implements Initializable {

    @FXML private TextField titre;
    @FXML private TextArea desc;
    @FXML private DatePicker date;
    @FXML private ComboBox<CategorieReclamation> categorieCombo;
    @FXML private Button btnAjouter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            categorieCombo.getItems().addAll(new CategorieReclamationC().readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterReclamation() throws SQLException {
        String titre = this.titre.getText();
        String desc = this.desc.getText();
        LocalDate date = this.date.getValue();
        CategorieReclamation categorie = categorieCombo.getValue();

        // Contrôle de saisie...
        if (titre.isEmpty() || desc.isEmpty() || date == null || categorie == null) {
            showAlert("Veuillez remplir tous les champs !");
            return;
        }

        // Récupération de l’utilisateur connecté
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Aucun utilisateur connecté !");
            return;
        }

        // Création de l’objet réclamation
        reclamation r = new reclamation(currentUser.getId(), titre, desc, "en attente", date);
        r.setCategorieId(categorie.getId());
        r.setEmailUtilisateur(currentUser.getEmail());  // ✅ IMPORTANT pour éviter l’erreur SQL

        // Envoi à la base
        reclamationC rc = new reclamationC();
        rc.create(r);

        // Nettoyage
        this.titre.clear();
        this.desc.clear();
        this.date.setValue(null);
        this.categorieCombo.setValue(null);

        System.out.println("Réclamation créée !");
    }

//    private void ajouterReclamation() {
//        String t = titre.getText();
//        String d = desc.getText();
//        LocalDate ld = date.getValue();
//        CategorieReclamation cat = categorieCombo.getValue();
//
//        // Contrôle de saisie
//        if (t.isEmpty() || d.isEmpty()) {
//            showAlert("Veuillez remplir tous les champs.");
//            return;
//        }
//        if (ld == null || ld.isBefore(LocalDate.now())) {
//            showAlert("La date doit être aujourd’hui ou ultérieure.");
//            return;
//        }
//        if (cat == null) {
//            showAlert("Veuillez choisir une catégorie.");
//            return;
//        }
//
//
//        // Création de la réclamation (userId fictif = 1)
//        reclamation r = new reclamation(1, t, d, "en attente", ld);
//        r.setCategorieId(cat.getId());
//
//        try {
//            new reclamationC().create(r);
//            showSuccess("Réclamation envoyée !");
//            // Fermer la fenêtre
//            Stage stage = (Stage) btnAjouter.getScene().getWindow();
//            stage.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            showAlert("Erreur lors de l’ajout.");
//        }
//    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
