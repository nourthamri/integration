package controllers.reponse;

import models.reponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ReponseC;
import javafx.scene.Parent;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Listerep {

    @FXML
    private ListView<HBox> listViewReclamations;

    private final ReponseC service = new ReponseC();

    public void setReclamationId(int idReclamation) {
        listViewReclamations.getItems().clear();

        try {
            List<reponse> reponses = service.readByReclamationId(idReclamation);

            if (reponses.isEmpty()) {
                Text noResult = new Text("❗ Aucune réponse trouvée pour cette réclamation.");
                listViewReclamations.getItems().add(new HBox(noResult));
            } else {
                for (reponse rep : reponses) {
                    Text contenu = new Text("➤ " + rep.getContenu() + " (le " + rep.getDate() + ")");
                    Button btnUpdate = new Button("Modifier");
                    Button btnDelete = new Button("Supprimer");

                    btnUpdate.setOnAction(e -> {
                        try {
                            var url = getClass().getResource("/reponse/updaterep.fxml");
                            if (url == null) throw new IOException("FXML introuvable");

                            FXMLLoader loader = new FXMLLoader(url);
                            Parent root = loader.load(); // ✅ plus de cast vers VBox

                            Updaterep controller = loader.getController();
                            controller.setReponse(rep);
                            controller.setOnUpdateSuccess(() -> setReclamationId(idReclamation));

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Modifier la réponse");
                            stage.showAndWait();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });


                    btnDelete.setOnAction(e -> {
                        try {
                            service.delete(rep);
                            setReclamationId(idReclamation); // rafraîchir
                        } catch (SQLException ex) {
                            System.err.println("Erreur lors de la suppression : " + ex.getMessage());
                        }
                    });

                    HBox ligne = new HBox(15, contenu, btnUpdate, btnDelete);
                    listViewReclamations.getItems().add(ligne);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
    }

    public void setReclamation(models.reclamation r) {
        if (r != null) {
            setReclamationId(r.getId());
        }
    }
}
