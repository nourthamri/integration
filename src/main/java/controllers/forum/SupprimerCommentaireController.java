package controllers.forum;

import entities.Commentaire;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.CommentaireService;

public class SupprimerCommentaireController {
    @FXML private Label confirmationLabel;
    @FXML private Button confirmerButton;
    @FXML private Button annulerButton;

    private Commentaire commentaire;
    private final CommentaireService service = new CommentaireService();
    private Runnable refreshCallback;

    public void setCommentaire(Commentaire commentaire) {
        this.commentaire = commentaire;
        confirmationLabel.setText("Voulez-vous vraiment supprimer ce commentaire : \"" + commentaire.getContent() + "\" ?");
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void confirmerSuppression() {
        if (service.delete(commentaire.getId())) {
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        }
        close();
    }

    @FXML
    private void annulerSuppression() {
        close();
    }

    private void close() {
        Stage stage = (Stage) confirmerButton.getScene().getWindow();
        stage.close();
    }
}