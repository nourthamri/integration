package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Sidebar {

    private void switchTo(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    @FXML
    public void allerAccueil() throws IOException {
        switchTo("/accueil.fxml");
    }

    @FXML
    public void allerReclamations() throws IOException {
        switchTo("/reclamation/reclamationliste.fxml");
    }

    @FXML
    public void allerUsers() throws IOException {
        switchTo("/user/userlist.fxml");
    }

    @FXML
    public void allerEvents() throws IOException {
        switchTo("/event/listevent.fxml");
    }

    @FXML
    public void allerForum() throws IOException {
        switchTo("/forum/AfficherPost.fxml");
    }

    @FXML
    public void allerProduits() throws IOException {
        switchTo("/produit/listproduit.fxml");
    }

    @FXML
    public void allerAnnonces() throws IOException {
        switchTo("/annonce/listeannonce.fxml");
    }
}
