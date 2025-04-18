package gui.reclamation;

import gui.reponse.Createrep;
import gui.reponse.Listerep;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import entities.reclamation;
import entities.reponse;
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

    private ObservableList<HBox> reclamationList = FXCollections.observableArrayList();
    private final reclamationC service = new reclamationC();
    private final services.CategorieReclamationC catService = new CategorieReclamationC();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewReclamations.setItems(reclamationList);
        loadReclamations();

        btnRefresh.setOnAction(event -> loadReclamations());
    }


    private void loadReclamations() {
        try {
            reclamationList.clear();
            List<reclamation> reclamations = service.readAll();

            for (reclamation r : reclamations) {
                String nomCategorie = catService.getNomCategorieById(r.getCategorieId());

                Text info = new Text(
                        "Titre : " + r.getTitre() +
                                "\nDescription : " + r.getDescription() +
                                "\nStatus : " + r.getStatus() +
                                "\nDate : " + (r.getDate() != null ? r.getDate().toString() : "Non précisée") +
                                "\nCatégorie : " + nomCategorie
                );

                Button btnModifier = new Button("Modifier");
                Button btnSupprimer = new Button("Supprimer");


                btnModifier.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/updaterec.fxml"));
                        Parent root = loader.load(); // ✅ Ici, on utilise Parent (classe plus générale)

                        // Récupérer le contrôleur de la fenêtre de modification
                        Updaterec controller = loader.getController();
                        controller.setReclamation(r); // Passer l'objet réclamation à la fenêtre de modification
                        controller.setOnUpdateSuccess(this::loadReclamations); // Rafraîchir la liste après modification

                        // Créer et afficher la nouvelle fenêtre
                        Stage stage = new Stage();
                        stage.setTitle("Modifier Réclamation");
                        stage.setScene(new Scene(root));
                        stage.showAndWait(); // Attendre la fermeture de la fenêtre
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Gérer l'exception si le fichier FXML n'est pas trouvé ou autre erreur
                    }
                });


                btnSupprimer.setOnAction(e -> {
                    try {
                        service.delete(r);
                        loadReclamations(); // Rafraîchir la liste après suppression
                    } catch (SQLException ex) {
                        System.err.println("Erreur lors de la suppression : " + ex.getMessage());
                    }
                });

                Button btnRepondre = new Button("Répondre");

                btnRepondre.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reponse/createrep.fxml"));
                        Parent root = loader.load();

                        // Récupérer le contrôleur de la fenêtre createrep
                        Createrep controller = loader.getController();
                        controller.setReclamation(r); // ← transmettre la réclamation à laquelle on répond

                        Stage stage = new Stage();
                        stage.setTitle("Ajouter une réponse");
                        stage.setScene(new Scene(root));
                        stage.showAndWait(); // Attend la fermeture avant de continuer

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                Button btnliste = new Button("Voir reponse");

                btnliste.setOnAction(e -> {
                    try {
                     // href
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reponse/Listerep.fxml"));
                        Parent root = loader.load();

                        // Récupérer le contrôleur de la fenêtre createrep
                        Listerep controller = loader.getController();
                        controller.setReclamation(r); // ← transmettre la réclamation à laquelle on répond

                        Stage stage = new Stage();
                        stage.setTitle("Ajouter une réponse");
                        stage.setScene(new Scene(root));
                        stage.showAndWait(); // Attend la fermeture avant de continuer

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                Button btnRemboursements = new Button("Voir remboursements");

                btnRemboursements.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/listeremboursement.fxml")); // ✅ chemin corrigé
                        Parent root = loader.load();

                        // Récupérer le contrôleur
                        gui.remboursement.listeremboursement controller = loader.getController();
                        controller.setReclamationId(r.getId()); // ← passe l'ID de la réclamation sélectionnée

                        Stage stage = new Stage();
                        stage.setTitle("Liste des remboursements");
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                VBox buttonBox = new VBox(10);
                buttonBox.getChildren().addAll(btnModifier, btnSupprimer, btnRepondre, btnliste, btnRemboursements);

                HBox ligne = new HBox(20);
                ligne.getChildren().addAll(info, buttonBox);

                reclamationList.add(ligne);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des réclamations : " + e.getMessage());
        }
    }
}
