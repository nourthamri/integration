package controllers.forum;

import entities.Commentaire;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CommentaireService;
import utils.MaConnexion;


public class CreerCommentaire {
    @FXML private TextField contentField;
    @FXML private TextField postIdField;

    private Runnable refreshCallback;
    private final CommentaireService service = new CommentaireService();

    @FXML
    private void handleAdd() {
        try {
            String content = contentField.getText().trim();

            if (!validateInput()) return;
            if (!validateContent(content)) return;

            Commentaire c = new Commentaire();
            c.setContent(content);
            c.setPostId(Integer.parseInt(postIdField.getText().trim()));

            if (service.add(c)) {
                showAlert("Succès", "Commentaire ajouté");
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                closeWindow();
            } else {
                showAlert("Erreur", "Échec de l'ajout");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID post invalide");
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    private boolean validateInput() {
        if (contentField.getText() == null || contentField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le contenu ne peut pas être vide");
            return false;
        }

        if (postIdField.getText() == null || postIdField.getText().trim().isEmpty()) {
            showAlert("Erreur", "L'ID du post est obligatoire");
            return false;
        }

        try {
            Integer.parseInt(postIdField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID du post doit être un nombre valide");
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}