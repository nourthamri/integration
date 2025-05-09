package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.entities.Commentaire;
import tn.esprit.services.CommentaireService;

public class RepondreCommentaireController {
    @FXML private TextArea reponseText;

    private Commentaire commentaireParent;
    private Runnable refreshCallback;

    public void setCommentaireParent(Commentaire commentaire) {
        this.commentaireParent = commentaire;
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void handleReponse() {
        String contenu = reponseText.getText().trim();
        if (contenu.isEmpty()) {
            return;
        }

        Commentaire reponse = new Commentaire();
        reponse.setContent(contenu);
        reponse.setPostId(commentaireParent.getPostId());
        reponse.setParentId(commentaireParent.getId());

        CommentaireService service = new CommentaireService();
        if (service.add(reponse)) {
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            closeWindow();
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) reponseText.getScene().getWindow();
        stage.close();
    }
}