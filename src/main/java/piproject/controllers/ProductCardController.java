package piproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import piproject.models.Product;

import java.util.function.Consumer;

public class ProductCardController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label valueLabel;

    private Product product; // ✅ Store the product here

    private Consumer<Product> onProductSelected;

    public void setProduct(Product product) {
        this.product = product; // ✅ Save the reference
        nameLabel.setText(product.getNom());
        descriptionLabel.setText(product.getDescription());
        valueLabel.setText(product.getValeur() + "€");
    }

    public void setOnProductSelected(Consumer<Product> callback) {
        this.onProductSelected = callback;
    }

    @FXML
    private void onCardClicked() {
        if (onProductSelected != null && product != null) {
            onProductSelected.accept(product);
        }
    }
}
