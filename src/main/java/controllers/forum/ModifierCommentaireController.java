package controllers.forum;

import entities.Commentaire;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CommentaireService;
import utils.MaConnexion;

public class ModifierCommentaireController {
    @FXML private TextField contentField;
    private Commentaire commentaire;
    private Runnable refreshCallback;
    private final CommentaireService service = new CommentaireService();

    public void setCommentaire(Commentaire commentaire) {
        this.commentaire = commentaire;
        contentField.setText(commentaire.getContent());
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void handleUpdate() {
        try {
            String content = contentField.getText().trim();

            if (!validateInput(content)) return;
            if (!validateContent(content)) return;

            commentaire.setContent(content);
            service.update(commentaire);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private boolean validateInput(String content) {
        if (content.isEmpty() || content.matches("^\\s*$")) {
            showAlert("Erreur", "Le contenu ne peut pas être vide ou contenir uniquement des espaces");
            return false;
        }
        return true;
    }

    private boolean validateContent(String text) {
        if (MaConnexion.containsBadWords(text)) {
            showAlert("Contenu inapproprié", "Votre texte contient des mots inappropriés");
            return false;
        }
        return true;
    }

    @FXML
    private void closeWindow() {
        ((Stage) contentField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}