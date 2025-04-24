package controllers;

import models.reclamation;
import models.CategorieReclamation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.reclamationC;
import services.CategorieReclamationC;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class Listereclamationsfront implements Initializable {

    @FXML
    private ListView<HBox> listViewReclamations;

    @FXML
    private Button btnRetour;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ComboBox<String> categorieFilter;


    private List<reclamation> toutesLesReclamations;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            categorieFilter.getItems().add("Toutes les catégories");
            categorieFilter.getSelectionModel().selectFirst(); // sélectionne par défaut

            CategorieReclamationC catService = new CategorieReclamationC();
            for (CategorieReclamation cat : catService.readAll()) {
                categorieFilter.getItems().add(cat.getNom());
            }

            toutesLesReclamations = new reclamationC().readByUserId(1);
            listViewReclamations.setItems(FXCollections.observableArrayList());
            statusFilter.getItems().addAll("en attente", "resolved");
            afficherReclamations(toutesLesReclamations);
            ajouterListeners();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ajouterListeners() {
        searchField.textProperty().addListener((obs, old, nouv) -> filtrer());
        statusFilter.valueProperty().addListener((obs, old, nouv) -> filtrer());
    }

    private void filtrer() {
        String motCle = searchField.getText().toLowerCase().trim();
        String statut = statusFilter.getValue();
        String categorieChoisie = categorieFilter.getValue();

        List<reclamation> filtrees = toutesLesReclamations.stream()
                .filter(r -> r.getTitre().toLowerCase().contains(motCle))
                .filter(r -> statut == null || r.getStatus().equalsIgnoreCase(statut))
                .filter(r -> {
                    if (categorieChoisie == null || categorieChoisie.equals("Toutes les catégories")) {
                        return true;
                    }
                    String nomCategorie = new CategorieReclamationC().getNomCategorieById(r.getCategorieId());
                    return nomCategorie.equalsIgnoreCase(categorieChoisie);
                })
                .toList();

        afficherReclamations(filtrees);
    }



    private void afficherReclamations(List<reclamation> liste) {
        listViewReclamations.getItems().clear();

        CategorieReclamationC catService = new CategorieReclamationC();

        for (reclamation r : liste) {
            String nomCategorie = catService.getNomCategorieById(r.getCategorieId());

            Text info = new Text("📝 " + r.getTitre() +
                    "\n📅 " + r.getDate() +
                    " | Statut : " + r.getStatus() +
                    " | Catégorie : " + nomCategorie);

            Button btnDetail = new Button("Détails");
            btnDetail.setOnAction(ev -> ouvrirDetails(r));

            HBox ligne = new HBox(10, info, btnDetail);
            ligne.setStyle("-fx-padding: 10; -fx-background-color: #f1f5f9; -fx-background-radius: 8;");
            listViewReclamations.getItems().add(ligne);
        }
    }


    private void ouvrirDetails(reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detailsreclamation.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            Detailsreclamation controller = loader.getController();
            controller.setReclamation(r);
            stage.setTitle("Détails de la réclamation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rafraichir() {
        searchField.clear();
        statusFilter.setValue(null);
        afficherReclamations(toutesLesReclamations);
    }

    @FXML
    private void retourAccueil() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accueil.fxml"));
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}
