package controllers.forum;

import entities.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.PostService;


import java.io.IOException;
import java.time.LocalDate;

public class AfficherPostController {

    @FXML private FlowPane postsContainer;
    @FXML private TextField searchField;
    @FXML private DatePicker dateFilter;
    @FXML private ComboBox<String> sortComboBox;

    private Post selectedPost = null;
    private final PostService postService = new PostService();

    @FXML
    public void initialize() {
        sortComboBox.getItems().addAll("Plus récent", "Plus ancien");
        sortComboBox.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterPosts());
        dateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPosts());
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterPosts());

        loadPosts();
    }

    private void filterPosts() {
        if (searchField.getText().isEmpty() && dateFilter.getValue() == null) {
            loadPosts();
        } else {
            searchPosts();
        }
    }

    public void loadPosts() {
        postsContainer.getChildren().clear();
        boolean ascending = "Plus ancien".equals(sortComboBox.getValue());
        postService.getAllSorted(ascending).forEach(this::createAndAddPostCard);
    }

    private void searchPosts() {
        postsContainer.getChildren().clear();
        String keyword = searchField.getText();
        LocalDate date = dateFilter.getValue();
        postService.searchPosts(keyword, date).forEach(this::createAndAddPostCard);
    }

    private void createAndAddPostCard(Post post) {
        VBox card = new VBox(10);
        card.getStyleClass().add("post-card");
        card.setStyle("-fx-background-color: #F8F2EC; -fx-background-radius: 10; -fx-padding: 15;");
        card.setPrefWidth(300);

        Label title = new Label(post.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text content = new Text(post.getContent());
        content.setWrappingWidth(280);

        Label date = new Label("Créé le: " + post.getCreatedAt().toString());

        card.getChildren().addAll(title, content, date);

        card.setOnMouseClicked(e -> {
            resetCardStyles();
            card.setStyle("-fx-background-color: #E0D5CD; -fx-border-color: #6D4C41;");
            selectedPost = post;
            showPostDetails(post);
        });

        postsContainer.getChildren().add(card);
    }

    private void showPostDetails(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/PostDetailView.fxml"));
            Parent root = loader.load();

            PostDetailController controller = loader.getController();
            controller.setPost(post);
            controller.setAfficherPostController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du Post");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreatePost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/CreerPost.fxml"));
            Parent root = loader.load();

            CreerPost controller = loader.getController();
            controller.setAfficherPostController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nouveau Post");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/StatsView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les statistiques: " + e.getMessage());
        }
    }

    private void resetCardStyles() {
        postsContainer.getChildren().forEach(node ->
                node.setStyle("-fx-background-color: #F8F2EC;"));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setPrimaryStage(Stage primaryStage) {

    }
}