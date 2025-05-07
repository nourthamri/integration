package controllers.forum;

import entities.Commentaire;
import entities.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CommentaireService;
import services.PostService;
import services.TranslationService;
import utils.MaConnexion;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PostDetailController {
    @FXML private Label postTitle;
    @FXML private Label postContent;
    @FXML private Label postDate;
    @FXML private ListView<Commentaire> commentsList;
    @FXML private TextArea commentField;

    private Post currentPost;
    private final PostService postService = new PostService();
    private final CommentaireService commentService = new CommentaireService();
    private final TranslationService translationService = new TranslationService();
    private AfficherPostController afficherPostController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setPost(Post post) {
        this.currentPost = post;
        updatePostDisplay();
        loadComments();
        setupListView();
    }

    public void setAfficherPostController(AfficherPostController controller) {
        this.afficherPostController = controller;
    }

    private void updatePostDisplay() {
        postTitle.setText(currentPost.getTitle());
        postContent.setText(currentPost.getContent());
        postDate.setText("Posté le: " + currentPost.getCreatedAt().format(dateFormatter));
    }

    @FXML
    private void handleTranslatePost() {
        if (currentPost != null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("fr", "en", "es", "de");
            dialog.setTitle("Traduction");
            dialog.setHeaderText("Langue cible:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(lang -> {
                String translated = translationService.translateText(currentPost.getContent(), lang);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Traduction");
                alert.setHeaderText("Résultat (" + lang + "):");
                alert.setContentText(translated);
                alert.showAndWait();
            });
        } else {
            showAlert("Erreur", "Aucun post disponible pour traduction", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdatePost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/ModifierPost.fxml"));
            Parent root = loader.load();

            ModifierPostController controller = loader.getController();
            controller.setPostData(currentPost);
            controller.setAfficherPostController(afficherPostController);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Post");
            stage.showAndWait();

            updatePostDisplay();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeletePost() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le post: " + currentPost.getTitle());
        confirm.setContentText("Êtes-vous sûr ? Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (postService.delete(currentPost.getId())) {
                    showAlert("Succès", "Post supprimé avec succès", Alert.AlertType.INFORMATION);
                    closeWindow();
                    if (afficherPostController != null) {
                        afficherPostController.loadPosts();
                    }
                } else {
                    showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void loadComments() {
        commentsList.getItems().clear();
        commentsList.getItems().addAll(commentService.getCommentairesParPost(currentPost.getId()));
    }

    private void setupListView() {
        commentsList.setCellFactory(lv -> new ListCell<Commentaire>() {
            @Override
            protected void updateItem(Commentaire comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setText(null);
                } else {
                    setText(String.format("%s\nPosté le: %s",
                            comment.getContent(),
                            comment.getCreatedAt().format(dateFormatter)));
                }
            }
        });

        commentsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                commentField.setText(newVal.getContent());
            }
        });
    }

    @FXML
    private void handleAddComment() {
        try {
            String content = commentField.getText().trim();
            validateComment(content);

            Commentaire newComment = new Commentaire();
            newComment.setContent(content);
            newComment.setPostId(currentPost.getId());

            if (commentService.add(newComment)) {
                commentField.clear();
                loadComments();
                showAlert("Succès", "Commentaire ajouté avec succès", Alert.AlertType.INFORMATION);
            } else {
                throw new RuntimeException("Échec de l'ajout du commentaire");
            }
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateComment() {
        try {
            Commentaire selected = commentsList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Aucun commentaire sélectionné");
            }

            String newContent = commentField.getText().trim();
            validateComment(newContent);

            selected.setContent(newContent);

            if (commentService.update(selected)) {
                commentField.clear();
                loadComments();
                showAlert("Succès", "Commentaire mis à jour avec succès", Alert.AlertType.INFORMATION);
            } else {
                throw new RuntimeException("Aucun commentaire n'a été modifié");
            }
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteComment() {
        try {
            Commentaire selected = commentsList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Aucun commentaire sélectionné");
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Supprimer le commentaire");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (commentService.delete(selected.getId())) {
                        loadComments();
                        commentField.clear();
                        showAlert("Succès", "Commentaire supprimé avec succès", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Erreur", "Échec de la suppression du commentaire", Alert.AlertType.ERROR);
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void validateComment(String content) throws IllegalArgumentException {
        if (content.isEmpty()) {
            throw new IllegalArgumentException("Le commentaire ne peut pas être vide");
        }

        if (MaConnexion.containsBadWords(content)) {
            throw new IllegalArgumentException("Le commentaire contient des mots inappropriés");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        ((Stage) postTitle.getScene().getWindow()).close();
    }
}