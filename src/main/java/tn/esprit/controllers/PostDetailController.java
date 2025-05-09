package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Commentaire;
import tn.esprit.entities.Post;
import tn.esprit.services.CommentaireService;
import tn.esprit.services.PostService;
import tn.esprit.services.ReactionService;
import tn.esprit.services.TranslationService;
import tn.esprit.util.MaConnexion;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class PostDetailController {
    @FXML private Label postTitle;
    @FXML private Label postContent;
    @FXML private Label postDate;
    @FXML private ListView<Commentaire> commentsList;
    @FXML private TextArea commentField;
    @FXML private HBox reactionsContainer;
    @FXML private Label reactionsSummaryLabel;

    private Post currentPost;
    private final PostService postService = new PostService();
    private final CommentaireService commentService = new CommentaireService();
    private final ReactionService reactionService = new ReactionService();
    private final TranslationService translationService = new TranslationService();
    private AfficherPostController afficherPostController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final int currentUserId = 1;
    private final String[] availableEmojis = {"👍", "👎", "❤️", "😂", "😮", "😢", "😠"};

    public void setPost(Post post) {
        this.currentPost = post;
        updatePostDisplay();
        loadComments();
        setupListView();
        setupReactions();
    }

    public void setAfficherPostController(AfficherPostController controller) {
        this.afficherPostController = controller;
    }

    private void updatePostDisplay() {
        postTitle.setText(currentPost.getTitle());
        postContent.setText(currentPost.getContent());
        postDate.setText("Posté le: " + currentPost.getCreatedAt().format(dateFormatter));
    }

    private void setupReactions() {
        reactionsContainer.getChildren().clear();

        for (String emoji : availableEmojis) {
            Button reactionBtn = new Button(emoji);
            reactionBtn.getStyleClass().add("reaction-btn");
            reactionBtn.setUserData(emoji);

            reactionBtn.setOnAction(e -> {
                String selectedEmoji = (String) reactionBtn.getUserData();
                handleReaction(selectedEmoji);
            });

            reactionsContainer.getChildren().add(reactionBtn);
        }

        updateReactionsDisplay();
    }

    private void handleReaction(String emoji) {
        boolean success = reactionService.toggleReaction(currentUserId, currentPost.getId(), emoji);
        if (success) {
            updateReactionsDisplay();
        } else {
            showAlert("Erreur", "Échec de l'enregistrement de la réaction", Alert.AlertType.ERROR);
        }
    }

    private void updateReactionsDisplay() {
        Map<String, Integer> reactionCounts = reactionService.getReactionCountsForPost(currentPost.getId());

        StringBuilder summary = new StringBuilder("Réactions: ");
        reactionCounts.forEach((emoji, count) -> {
            summary.append(emoji).append(" (").append(count).append(") ");
        });

        reactionsSummaryLabel.setText(summary.toString().trim());

        String userReaction = reactionService.getUserReaction(currentUserId, currentPost.getId());

        for (Node node : reactionsContainer.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                String btnEmoji = (String) btn.getUserData();

                if (btnEmoji.equals(userReaction)) {
                    if (!btn.getStyleClass().contains("active-reaction")) {
                        btn.getStyleClass().add("active-reaction");
                    }
                } else {
                    btn.getStyleClass().remove("active-reaction");
                }
            }
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPost.fxml"));
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
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Commentaire comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);

                    // Commentaire principal
                    HBox mainCommentBox = new HBox(10);
                    Label contentLabel = new Label(comment.getContent());
                    Label dateLabel = new Label(comment.getCreatedAt().format(formatter));
                    dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");

                    Button replyButton = new Button("Répondre");
                    replyButton.setStyle("-fx-font-size: 11px;");
                    replyButton.setOnAction(e -> openReplyDialog(comment));

                    mainCommentBox.getChildren().addAll(contentLabel, dateLabel, replyButton);

                    // Réponses
                    VBox repliesContainer = new VBox(5);
                    repliesContainer.setStyle("-fx-padding: 0 0 0 20; -fx-border-color: #ddd; -fx-border-width: 0 0 0 2;");

                    commentService.getReponsesParCommentaire(comment.getId()).forEach(reply -> {
                        HBox replyBox = new HBox(10);
                        Label replyContent = new Label("↳ " + reply.getContent());
                        Label replyDate = new Label(reply.getCreatedAt().format(formatter));
                        replyDate.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
                        replyBox.getChildren().addAll(replyContent, replyDate);
                        repliesContainer.getChildren().add(replyBox);
                    });

                    container.getChildren().addAll(mainCommentBox, repliesContainer);
                    setGraphic(container);
                }
            }
        });

        commentsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                commentField.setText(newVal.getContent());
            }
        });
    }

    private void openReplyDialog(Commentaire comment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RepondreCommentaire.fxml"));
            Parent root = loader.load();

            RepondreCommentaireController controller = loader.getController();
            controller.setCommentaireParent(comment);
            controller.setRefreshCallback(this::loadComments);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Répondre au commentaire");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de réponse", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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