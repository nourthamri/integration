package gui.reponse;

import entities.reclamation;
import entities.reponse;
import gui.remboursement.CreateRemboursement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import services.ReponseC;
import services.reclamationC;
import services.remboursementC;
import utils.MyConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class Createrep implements Initializable {

    @FXML
    private TextField cont;

    @FXML
    private DatePicker Date;

    @FXML
    private Button Add;

    private reclamation reclamation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Add.setOnAction(event -> {
            try {
                ajouterReponse();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void ajouterReponse() throws SQLException {
        String contenuTexte = cont.getText();
        LocalDate dateReponse = Date.getValue();

        if (reclamation == null) {
            System.err.println("Erreur : aucune réclamation liée !");
            return;
        }

        int recId = reclamation.getId();

        reponse r = new reponse(recId, contenuTexte, dateReponse);
        ReponseC rc = new ReponseC();
        rc.create(r);

// Vérifiez si la réclamation est correctement obtenue
        System.out.println("Réclamation avant mise à jour : " + reclamation.getStatus());

// Mettre à jour le statut
        reclamation.setStatus("resolved");
        new reclamationC().update(reclamation);

// Vérifiez si la réclamation a bien été mise à jour
        System.out.println("Réclamation après mise à jour : " + reclamation.getStatus());


        cont.clear();
        Date.setValue(null);

        System.out.println("Réponse ajoutée à la réclamation ID " + recId);

        // 🔍 Ouvre la fenêtre de remboursement seulement si éligible
        ouvrirFenetreRemboursement();
    }

    private void ouvrirFenetreRemboursement() {
        try {
            remboursementC service = new remboursementC();

            // Vérifie si un remboursement existe déjà
            if (service.remboursementExistePour(reclamation.getId())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Remboursement déjà existant");
                alert.setHeaderText(null);
                alert.setContentText("Un remboursement existe déjà pour cette réclamation.");
                alert.showAndWait();
                return;
            }
            // 🔄 Vérifier la catégorie
            String categorieNom = getCategorieNomById(reclamation.getCategorieId());
            if (categorieNom.equalsIgnoreCase("Service")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Non éligible");
                alert.setContentText("Les réclamations de type 'Service' ne donnent pas lieu à un remboursement.");
                alert.showAndWait();
                return;
            }


            // ⚠️ Logique métier simple : vérifier si le contenu de la réclamation contient des mots-clés
            String contenu = (reclamation.getTitre() + " " + reclamation.getDescription()).toLowerCase();
            boolean estEligible = contenu.contains("défectueux")
                    || contenu.contains("non reçu")
                    || contenu.contains("retour")
                    || contenu.contains("remboursement");

            if (!estEligible) {
                System.out.println("Réclamation non éligible au remboursement.");
                return;
            }

            // ✅ Ouvre la fenêtre de remboursement
            URL fxmlLocation = getClass().getResource("/reclamation/createremboursement.fxml");
            if (fxmlLocation == null) {
                System.err.println("Le fichier FXML de remboursement n'a pas été trouvé !");
            } else {
                FXMLLoader loader = new FXMLLoader(fxmlLocation);
                Parent root = loader.load();

                CreateRemboursement controller = loader.getController();
                controller.setReclamationId(reclamation.getId());

                Stage stage = new Stage();
                stage.setTitle("Ajouter un remboursement");
                stage.setScene(new Scene(root));
                stage.showAndWait();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCategorieNomById(int idCategorie) throws SQLException {
        Connection cnx = MyConnection.getInstance().getConnection();
        String query = "SELECT nom FROM categorie_reclamation WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, idCategorie);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("nom");
        }
        return "";
    }
    public void setReclamation(reclamation r) {
        this.reclamation = r;
        System.out.println("Répondre à la réclamation ID : " + r.getId());
    }
}
