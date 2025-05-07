package controllers.forum;

import entities.Commentaire;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.CommentaireService;


import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AfficherCommentaireController {
    @FXML private TableView<Commentaire> tableView;
    @FXML private TableColumn<Commentaire, String> colContent;
    @FXML private TableColumn<Commentaire, Integer> colPostId;
    @FXML private TableColumn<Commentaire, String> colDate;
    @FXML private Button btnAjouter;

    private final CommentaireService service = new CommentaireService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colPostId.setCellValueFactory(new PropertyValueFactory<>("postId"));
        colDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCreatedAt().format(formatter)));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() {
        ObservableList<Commentaire> commentaires = service.getAll();
        tableView.setItems(commentaires);
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/CreerCommentaire.fxml"));
            Parent root = loader.load();

            CreerCommentaire controller = loader.getController();
            controller.setRefreshCallback(this::loadData);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un commentaire");
            stage.showAndWait();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        Commentaire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/ModifierCommentaire.fxml"));
                Parent root = loader.load();

                ModifierCommentaireController controller = loader.getController();
                controller.setCommentaire(selected);
                controller.setRefreshCallback(this::loadData);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier le commentaire");
                stage.showAndWait();

            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir l'éditeur: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un commentaire à modifier");
        }
    }

    @FXML
    private void handleSupprimer() {
        Commentaire selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le commentaire");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                if (service.delete(selected.getId())) {
                    showAlert("Succès", "Commentaire supprimé avec succès");
                    loadData();
                } else {
                    showAlert("Erreur", "Échec de la suppression");
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un commentaire à supprimer");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}