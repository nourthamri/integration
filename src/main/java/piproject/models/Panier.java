package piproject.models;
import java.util.Objects;


public class Panier {
    private int id;
    private Product product;
    private int quantity;

    public Panier(int id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public Panier(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setProduct(Product product) { this.product = product; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

