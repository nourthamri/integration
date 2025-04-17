package services;

import entities.CategorieReclamation;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieReclamationC {

    private Connection cnx = MyConnection.getInstance().getConnection();

    public void create(CategorieReclamation c) throws SQLException {
        String sql = "INSERT INTO categorie_reclamation (nom) VALUES (?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, c.getNom());
        ps.executeUpdate();
    }

    public List<CategorieReclamation> readAll() throws SQLException {
        List<CategorieReclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM categorie_reclamation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            list.add(new CategorieReclamation(rs.getInt("id"), rs.getString("nom")));
        }
        return list;
    }

    public void update(CategorieReclamation c) throws SQLException {
        String sql = "UPDATE categorie_reclamation SET nom=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, c.getNom());
        ps.setInt(2, c.getId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM categorie_reclamation WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}
