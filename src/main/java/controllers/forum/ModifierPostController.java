package controllers.forum;

import entities.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.PostService;
import utils.MaConnexion;


public class ModifierPostController {

    @FXML private TextField titreField;
    @FXML private TextArea contenuField;
    @FXML private Label titreError;
    @FXML private Label contentError;

    private Post currentPost;
    private AfficherPostController afficherPostController;
    private final PostService postService = new PostService();

    public void setPostData(Post post) {
        this.currentPost = post;
        titreField.setText(post.getTitle());
        contenuField.setText(post.getContent());
    }

    public void setAfficherPostController(AfficherPostController controller) {
        this.afficherPostController = controller;
    }

    @FXML
    public void initialize() {
        // Validation en temps réel
        titreField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty() || newVal.trim().matches("^\\s*$")) {
                titreError.setText("Le titre ne peut pas être vide ou contenir uniquement des espaces");
            } else {
                titreError.setText("");
            }
        });

        contenuField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty() || newVal.trim().matches("^\\s*$")) {
                contentError.setText("Le contenu ne peut pas être vide ou contenir uniquement des espaces");
            } else {
                contentError.setText("");
            }
        });
    }

    @FXML
    private void handleUpdate() {
        String titre = titreField.getText().trim();
        String content = contenuField.getText().trim();

        if (!validateInput()) return;
        if (!validateContent(titre) || !validateContent(content)) return;

        currentPost.setTitle(titre);
        currentPost.setContent(content);
        postService.update(currentPost);
        afficherPostController.loadPosts();
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validation du titre
        String titre = titreField.getText().trim();
        if (titre.isEmpty() || titre.matches("^\\s*$")) {
            titreError.setText("Le titre ne peut pas être vide ou contenir uniquement des espaces");
            isValid = false;
        }

        // Validation du contenu
        String content = contenuField.getText().trim();
        if (content.isEmpty() || content.matches("^\\s*$")) {
            contentError.setText("Le contenu ne peut pas être vide ou contenir uniquement des espaces");
            isValid = false;
        }

        return isValid;
    }

    private void closeWindow() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean validateContent(String text) {
        if (MaConnexion.containsBadWords(text)) {
            showAlert("Contenu inapproprié", "Votre texte contient des mots inappropriés");
            return false;
        }
        return true;
    }
}