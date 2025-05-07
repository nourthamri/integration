package controllers.forum;

import entities.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PostController {
    @FXML private TableView<Post> postsTable;

    @FXML
    private void handleCreatePost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/CreerPost.fxml"));
            Parent root = loader.load();

            CreerPost controller = loader.getController();
            controller.setAfficherPostController((AfficherPostController) postsTable.getScene().getUserData());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Nouveau Post");
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de création");
        }
    }

    @FXML
    private void handleUpdatePost() {
        Post selected = postsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/ModifierPost.fxml"));
                Parent root = loader.load();

                ModifierPostController controller = loader.getController();
                controller.setPostData(selected);
                controller.setAfficherPostController((AfficherPostController) postsTable.getScene().getUserData());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setTitle("Modifier Post");
                stage.showAndWait();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir l'éditeur");
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un post à modifier");
        }
    }

    @FXML
    private void handleDeletePost() {
        Post selected = postsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/SupprimerPost.fxml"));
                Parent root = loader.load();

                SupprimerPostController controller = loader.getController();
                controller.setPostData(selected);
                controller.setAfficherPostController((AfficherPostController) postsTable.getScene().getUserData());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setTitle("Confirmer suppression");
                stage.showAndWait();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir la confirmation");
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un post à supprimer");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}