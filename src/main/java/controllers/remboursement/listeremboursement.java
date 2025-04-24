package controllers.remboursement;

import models.remboursement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.remboursementC;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class listeremboursement implements Initializable {

    @FXML
    private ListView<HBox> listViewRemboursements;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnAdd;

    private final ObservableList<HBox> remboursementList = FXCollections.observableArrayList();
    private final remboursementC service = new remboursementC();
    private int reclamationId;

    public void setReclamationId(int id) {
        this.reclamationId = id;
        loadRemboursements();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewRemboursements.setItems(remboursementList);

        btnRefresh.setOnAction(event -> loadRemboursements());

        btnAdd.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/createremboursement.fxml"));
                Parent root = loader.load();

                CreateRemboursement controller = loader.getController();
                controller.setReclamationId(reclamationId);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Ajouter un remboursement");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                loadRemboursements();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadRemboursements() {
        remboursementList.clear();
        try {
            List<remboursement> remboursements = service.readByReclamationId(reclamationId);

            for (remboursement r : remboursements) {
                Text info = new Text("Montant : " + r.getMontant() +
                        "\nDate : " + r.getDate() +
                        "\nRéclamation ID : " + r.getReclamationId());

                VBox box = new VBox(info);
                box.setSpacing(5);

                // Bouton de suppression
                Button btnDelete = new Button("Supprimer");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnDelete.setOnAction(event -> {
                    // Confirmation avant suppression
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("Voulez-vous vraiment supprimer ce remboursement ?");
                    Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        try {
                            service.delete(r.getId());
                            loadRemboursements();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                VBox rightBox = new VBox(btnDelete);
                rightBox.setSpacing(10);
                rightBox.setMinWidth(100);
                rightBox.setStyle("-fx-alignment: center-right;");

                HBox ligne = new HBox(box, rightBox);
                ligne.setSpacing(20);
                ligne.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 6;");

                remboursementList.add(ligne);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
