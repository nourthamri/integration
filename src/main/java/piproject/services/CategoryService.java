package piproject.services;

import piproject.interfaces.iservice;
import piproject.models.Category;
import piproject.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService implements iservice<Category> {

    private final Connection connection;

    public CategoryService() {
        this.connection = Myconnection.getInstance();
    }

    @Override
    public void add(Category c) {
        String sql = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, c.getCategory_name());
            stmt.setString(2, c.getCategory_description());
            stmt.executeUpdate();
            System.out.println("✅ Category added successfully");
        } catch (SQLException e) {
            System.err.println("❌ Error adding category: " + e.getMessage());
        }
    }

    @Override
    public void delete(Category c) {
        String sql = "DELETE FROM category WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, c.getCategory_id());
            stmt.executeUpdate();
            System.out.println("🗑️ Category deleted");
        } catch (SQLException e) {
            System.err.println("❌ Error deleting category: " + e.getMessage());
        }
    }

    @Override
    public void update(Category c) {
        String sql = "UPDATE category SET category_name = ?, category_description = ? WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, c.getCategory_name());
            stmt.setString(2, c.getCategory_description());
            stmt.setInt(3, c.getCategory_id());
            stmt.executeUpdate();
            System.out.println("✏️ Category updated");
        } catch (SQLException e) {
            System.err.println("❌ Error updating category: " + e.getMessage());
        }
    }

    @Override
    public Category find(Category c) {
        return find(String.valueOf(c.getCategory_id()));
    }

    @Override
    public Category find(String s) {
        String sql = "SELECT * FROM category WHERE category_name = ? OR category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, s);
            stmt.setString(2, s);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractCategory(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding category: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractCategory(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching categories: " + e.getMessage());
        }
        return list;
    }

    // Extract Category from ResultSet
    private Category extractCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("category_id"),
                rs.getString("category_name"),
                rs.getString("category_description")
        );
    }

    // Method to get Category by ID (used for specific fetching)
    public Category getById(int id) {
        try {
            String req = "SELECT * FROM category WHERE category_id = ?";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ " + e.getMessage());
        }
        return null;
    }
}
