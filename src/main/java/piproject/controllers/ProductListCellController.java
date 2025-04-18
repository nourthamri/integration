package piproject.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import piproject.models.Product;

import java.io.File;
import java.io.IOException;

public class ProductListCellController extends ListCell<Product> {

    private final HBox content;
    private final ImageView imageView;
    private final Label nameLabel;
    private final Label valueLabel;
    private final Button detailsButton;

    public ProductListCellController() {
        imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        nameLabel = new Label();
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        valueLabel = new Label();
        valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        detailsButton = new Button("Voir Détails");
        detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");

        VBox vbox = new VBox(nameLabel, valueLabel, detailsButton);
        vbox.setSpacing(5);

        content = new HBox(imageView, vbox);
        content.setSpacing(15);


    }

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);
        if (empty || product == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(product.getNom());
            valueLabel.setText("Valeur: " + product.getValeur());

            String imagePath = product.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    imageView.setImage(null);
                }
            } else {
                imageView.setImage(null);
            }

            setGraphic(content);
        }
    }


}
