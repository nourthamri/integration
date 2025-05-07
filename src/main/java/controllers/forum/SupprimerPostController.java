package controllers.forum;

import entities.Post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.PostService;

public class SupprimerPostController {
    @FXML private Label confirmationLabel;

    private Post currentPost;
    private Stage dialogStage;
    private AfficherPostController afficherPostController;
    private final PostService postService = new PostService();

    public void setPostData(Post post) {
        this.currentPost = post;
        confirmationLabel.setText("Voulez-vous vraiment supprimer le post :\n" + post.getTitle());
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setAfficherPostController(AfficherPostController controller) {
        this.afficherPostController = controller;
    }

    @FXML
    private void handleConfirm() {
        postService.delete(currentPost.getId());
        afficherPostController.loadPosts();
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}