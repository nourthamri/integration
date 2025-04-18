package piproject.services;

import piproject.interfaces.iservice;
import piproject.models.Product;
import piproject.models.Category;
import piproject.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements iservice<Product> {

    private final Connection connection;
    private final CategoryService categoryService;

    public ProductService() {
        this.connection = Myconnection.getInstance();
        this.categoryService = new CategoryService();  // Initialize CategoryService to retrieve categories
    }

    @Override
    public void add(Product p) {
        String sql = "INSERT INTO product (nom, description, coupon, valeur, etat, dispo, categorie, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getDescription());
            stmt.setInt(3, p.getCoupon());
            stmt.setFloat(4, p.getValeur());
            stmt.setString(5, p.getEtat());
            stmt.setString(6, p.getDispo());

            // Save the category name or ID to the database
            stmt.setInt(7, p.getCategorie().getCategory_id());  // Assuming the category is a Category object now
            stmt.setString(8, p.getImage());
            stmt.executeUpdate();
            System.out.println("✅ Product added successfully");
        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
        }
    }

    @Override
    public void delete(Product p) {
        String sql = "DELETE FROM product WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
            System.out.println("🗑️ Product deleted");
        } catch (SQLException e) {
            System.err.println("❌ Error deleting product: " + e.getMessage());
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE product SET nom = ?, description = ?, coupon = ?, valeur = ?, etat = ?, dispo = ?, categorie = ?, image = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getDescription());
            stmt.setInt(3, p.getCoupon());
            stmt.setFloat(4, p.getValeur());
            stmt.setString(5, p.getEtat());
            stmt.setString(6, p.getDispo());

            // Update the category ID in the database
            stmt.setInt(7, p.getCategorie().getCategory_id());  // Assuming the category is a Category object now
            stmt.setString(8, p.getImage());
            stmt.setInt(9, p.getId());
            stmt.executeUpdate();
            System.out.println("✏️ Product updated");
        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
        }
    }

    @Override
    public Product find(Product p) {
        return find(String.valueOf(p.getId()));
    }

    @Override
    public Product find(String s) {
        String sql = "SELECT * FROM product WHERE nom = ? OR id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, s);
            stmt.setString(2, s);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding product: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching products: " + e.getMessage());
        }
        return list;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        // Extract category from the result set using category_id
        int categoryId = rs.getInt("categorie");
        Category category = categoryService.getById(categoryId);

        return new Product(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getInt("coupon"),
                rs.getFloat("valeur"),
                rs.getString("etat"),
                rs.getString("dispo"),
                category,  // Set category object
                rs.getString("image")
        );
    }

    public Product getById(int id) {
        try {
            String req = "SELECT * FROM product WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setDescription(rs.getString("description"));
                p.setCoupon(rs.getInt("coupon"));
                p.setValeur(rs.getFloat("valeur"));
                p.setEtat(rs.getString("etat"));
                p.setDispo(rs.getString("dispo"));

                // Fetch the category object by category_id
                int categoryId = rs.getInt("categorie");
                Category category = categoryService.getById(categoryId);
                p.setCategorie(category);  // Set the category object

                p.setImage(rs.getString("image"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
