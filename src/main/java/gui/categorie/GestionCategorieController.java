package gui.categorie;

import entities.CategorieReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.CategorieReclamationC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestionCategorieController {

    @FXML
    private ListView<CategorieReclamation> listCategorie;

    @FXML
    private TextField nomField;

    @FXML
    private Button btnAjouter, btnModifier, btnSupprimer, btnRafraichir;

    private final CategorieReclamationC service = new CategorieReclamationC();
    private ObservableList<CategorieReclamation> categories = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        try {
            categories.addAll(service.readAll());
            listCategorie.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnAjouter.setOnAction(event -> ajouterCategorie());
        btnModifier.setOnAction(event -> modifierCategorie());
        btnSupprimer.setOnAction(event -> supprimerCategorie());
        btnRafraichir.setOnAction(event -> rafraichirCategories());
    }

    private void ajouterCategorie() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir un nom de catégorie !");
            return;
        }

        try {
            if (service.nomCategorieExiste(nom)) {
                showAlert(Alert.AlertType.ERROR, "Doublon", "Cette catégorie existe déjà !");
                return;
            }

            CategorieReclamation cat = new CategorieReclamation(nom);
            service.create(cat);
            rafraichirCategories();
            nomField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifierCategorie() {
        CategorieReclamation selected = listCategorie.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une catégorie à modifier.");
            return;
        }

        String nouveauNom = nomField.getText().trim();
        if (nouveauNom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez saisir un nouveau nom !");
            return;
        }

        try {
            if (service.nomCategorieExiste(nouveauNom)) {
                showAlert(Alert.AlertType.ERROR, "Doublon", "Une catégorie avec ce nom existe déjà !");
                return;
            }

            selected.setNom(nouveauNom);
            service.update(selected);
            rafraichirCategories();
            nomField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void supprimerCategorie() {
        CategorieReclamation selected = listCategorie.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        try {
            service.delete(selected.getId());
            rafraichirCategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rafraichirCategories() {
        try {
            categories.clear();
            categories.addAll(service.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
