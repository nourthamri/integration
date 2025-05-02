package piproject.services;
import piproject.interfaces.iservice;
import piproject.models.Panier;
import piproject.models.Product;
import piproject.models.Category;
import piproject.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierService {
    private Connection connection;

    public PanierService() {
        this.connection = Myconnection.getInstance(); // Adjust if needed
    }

    // Add product to cart
    public boolean isProductInPanier(Product product) throws SQLException {
        String query = "SELECT COUNT(*) FROM panier WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, product.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void addToPanier(Panier panier) throws SQLException {
        if (isProductInPanier(panier.getProduct())) {
            throw new SQLException("Product already in cart!");
        }
        // Otherwise, proceed with insertion
        String query = "INSERT INTO panier (product_id, quantity) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, panier.getProduct().getId());
            stmt.setInt(2, panier.getQuantity());
            stmt.executeUpdate();
        }
    }

    // Remove one item by id
    public void removeFromPanier(int panierId) throws SQLException {
        String sql = "DELETE FROM panier WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, panierId);
        stmt.executeUpdate();
    }

    // Clear the entire cart
    public void clearPanier() throws SQLException {
        String sql = "DELETE FROM panier";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
    }

    // List all products in the cart
    public List<Panier> getPanierList() throws SQLException {
        List<Panier> panierList = new ArrayList<>();
        String sql = "SELECT p.id as panier_id, p.quantity, pr.* FROM panier p JOIN product pr ON p.product_id = pr.id";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getInt("coupon"),
                    rs.getFloat("valeur"),
                    rs.getString("etat"),
                    rs.getString("dispo"),
                    null, // handle category if needed
                    rs.getString("image")
            );

            Panier panierItem = new Panier(rs.getInt("panier_id"), product, rs.getInt("quantity"));
            panierList.add(panierItem);
        }
        return panierList;
    }
}

