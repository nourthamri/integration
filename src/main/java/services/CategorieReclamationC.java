package services;

import entities.CategorieReclamation;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieReclamationC {

    private final Connection cnx;

    public CategorieReclamationC() {
        this.cnx = MyConnection.getInstance().getConnection();
    }

    // ✅ Ajouter une nouvelle catégorie
    public void create(CategorieReclamation c) throws SQLException {
        String sql = "INSERT INTO categorie_reclamation (nom) VALUES (?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.executeUpdate();
        }
    }

    // ✅ Lire toutes les catégories
    public List<CategorieReclamation> readAll() throws SQLException {
        List<CategorieReclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM categorie_reclamation";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                CategorieReclamation c = new CategorieReclamation(
                        rs.getInt("id"),
                        rs.getString("nom")
                );
                list.add(c);
            }
        }

        return list;
    }

    // ✅ Mettre à jour une catégorie
    public void update(CategorieReclamation c) throws SQLException {
        String sql = "UPDATE categorie_reclamation SET nom = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setInt(2, c.getId());
            ps.executeUpdate();
        }
    }

    // ✅ Supprimer une catégorie par ID
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM categorie_reclamation WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ✅ (Optionnel) Trouver une catégorie par ID
    public CategorieReclamation findById(int id) throws SQLException {
        String sql = "SELECT * FROM categorie_reclamation WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CategorieReclamation(rs.getInt("id"), rs.getString("nom"));
                }
            }
        }
        return null;
    }
    public String getNomCategorieById(int id) {
        String nom = "Non définie";
        String sql = "SELECT nom FROM categorie_reclamation WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nom = rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nom;
    }
    public boolean nomCategorieExiste(String nom) throws SQLException {
        String query = "SELECT COUNT(*) FROM categorie_reclamation WHERE LOWER(nom) = LOWER(?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setString(1, nom.trim());
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }
    public int getIdByNom(String nom) throws SQLException {
        String req = "SELECT id FROM categorie_reclamation WHERE nom = ?";
        try (java.sql.PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, nom);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }


}
