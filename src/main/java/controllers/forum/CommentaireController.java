package controllers.forum;

import entities.Commentaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.CommentaireService;


import java.time.format.DateTimeFormatter;

public class CommentaireController {
    @FXML private TableView<Commentaire> commentaireTable;
    @FXML private TableColumn<Commentaire, String> contenuCol;
    @FXML private TableColumn<Commentaire, String> dateCol;
    @FXML private TextArea commentaireInput;
    @FXML private Button ajouterBtn;

    private final CommentaireService commentaireService = new CommentaireService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private int postId; // Ajouté pour gérer l'ID du post associé

    public void setPostId(int postId) {
        this.postId = postId;
        loadCommentaires();
    }

    @FXML
    private void initialize() {
        contenuCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getContent()));
        dateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCreatedAt().format(formatter))
        );
    }

    @FXML
    private void ajouterCommentaire() {
        String contenu = commentaireInput.getText().trim();
        if (contenu.isEmpty()) {
            showAlert("Champ vide", "Veuillez saisir un commentaire.");
            return;
        }

        Commentaire commentaire = new Commentaire();
        commentaire.setContent(contenu);
        commentaire.setPostId(postId);

        if (commentaireService.add(commentaire)) {
            commentaireInput.clear();
            loadCommentaires();
        }
    }

    private void loadCommentaires() {
        ObservableList<Commentaire> commentaires = FXCollections.observableArrayList(
                commentaireService.getCommentairesParPost(postId)
        );
        commentaireTable.setItems(commentaires);
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}