package controllers.reclamation;

import controllers.reponse.Createrep;
import controllers.reponse.Listerep;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.reclamation;
import services.reclamationC;
import services.CategorieReclamationC;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class listereclamation implements Initializable {

    @FXML
    private Button btnRefresh;
    @FXML
    private ListView<HBox> listViewReclamations;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private DatePicker dateFilter;
    @FXML
    private Button btnApplyFilters;

    private ObservableList<HBox> reclamationList = FXCollections.observableArrayList();
    private final reclamationC service = new reclamationC();
    private final CategorieReclamationC catService = new CategorieReclamationC();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewReclamations.setItems(reclamationList);
        initFilters();
        loadReclamations();

        btnRefresh.setOnAction(event -> clearFiltersAndReload());
        btnApplyFilters.setOnAction(event -> loadReclamations());
    }

    private void initFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("Tous", "en attente", "resolved"));
        statusFilter.setValue("Tous");

        categoryFilter.setItems(FXCollections.observableArrayList("Toutes"));
        categoryFilter.setValue("Toutes");
        try {
            List<String> categories = catService.getAllCategoryNames();
            categoryFilter.getItems().addAll(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFiltersAndReload() {
        searchField.clear();
        statusFilter.setValue("Tous");
        categoryFilter.setValue("Toutes");
        dateFilter.setValue(null);
        loadReclamations();
    }

    private void loadReclamations() {
        try {
            reclamationList.clear();
            List<reclamation> reclamations = service.readAll();

            for (reclamation r : reclamations) {

                // FILTRAGE
                String keyword = searchField.getText().toLowerCase();
                if (!keyword.isEmpty() &&
                        !(r.getTitre().toLowerCase().contains(keyword) ||
                                r.getDescription().toLowerCase().contains(keyword))) {
                    continue;
                }

                if (!statusFilter.getValue().equals("Tous") &&
                        !r.getStatus().equalsIgnoreCase(statusFilter.getValue())) {
                    continue;
                }

                if (!categoryFilter.getValue().equals("Toutes")) {
                    String catName = catService.getNomCategorieById(r.getCategorieId());
                    if (!catName.equalsIgnoreCase(categoryFilter.getValue())) {
                        continue;
                    }
                }

                if (dateFilter.getValue() != null &&
                        (r.getDate() == null || !r.getDate().equals(dateFilter.getValue()))) {
                    continue;
                }

                String nomCategorie = catService.getNomCategorieById(r.getCategorieId());

                Text info = new Text(
                        "Titre : " + r.getTitre() +
                                "\nDescription : " + r.getDescription() +
                                "\nStatus : " + r.getStatus() +
                                "\nDate : " + (r.getDate() != null ? r.getDate().toString() : "Non précisée") +
                                "\nCatégorie : " + nomCategorie
                );

                Button btnModifier = new Button("Modifier");
                btnModifier.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/updaterec.fxml"));
                        Parent root = loader.load();
                        Updaterec controller = loader.getController();
                        controller.setReclamation(r);
                        controller.setOnUpdateSuccess(this::loadReclamations);
                        Stage stage = new Stage();
                        stage.setTitle("Modifier Réclamation");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setOnAction(e -> {
                    try {
                        service.delete(r);
                        loadReclamations();
                    } catch (SQLException ex) {
                        System.err.println("Erreur lors de la suppression : " + ex.getMessage());
                    }
                });

                Button btnRepondre = new Button("Répondre");
                btnRepondre.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reponse/createrep.fxml"));
                        Parent root = loader.load();
                        Createrep controller = loader.getController();
                        controller.setReclamation(r);
                        Stage stage = new Stage();
                        stage.setTitle("Ajouter une réponse");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                Button btnliste = new Button("Voir réponse");
                btnliste.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reponse/Listerep.fxml"));
                        Parent root = loader.load();
                        Listerep controller = loader.getController();
                        controller.setReclamation(r);
                        Stage stage = new Stage();
                        stage.setTitle("Liste des réponses");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                Button btnRemboursements = new Button("Voir remboursements");
                btnRemboursements.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/listeremboursement.fxml"));
                        Parent root = loader.load();
                        controllers.remboursement.listeremboursement controller = loader.getController();
                        controller.setReclamationId(r.getId());
                        Stage stage = new Stage();
                        stage.setTitle("Liste des remboursements");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                VBox buttonBox = new VBox(10, btnModifier, btnSupprimer, btnRepondre, btnliste, btnRemboursements);
                HBox ligne = new HBox(20, info, buttonBox);
                reclamationList.add(ligne);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des réclamations : " + e.getMessage());
        }
    }
}
