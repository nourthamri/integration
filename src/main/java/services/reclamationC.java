package services;

import entities.reclamation;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class reclamationC implements IService<reclamation> {

    Connection cnx = MyConnection.getInstance().getConnection();

    @Override
    public void create(reclamation r) throws SQLException {
        String query = "INSERT INTO reclamation (user_id, titre, description, status, date, categorie_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, r.getId_user());
        ps.setString(2, r.getTitre());
        ps.setString(3, r.getDescription());
        ps.setString(4, r.getStatus());
        ps.setDate(5, Date.valueOf(r.getDate()));
        ps.setInt(6, r.getCategorieId()); // 👈 ajout ici
        ps.executeUpdate();
    }

    @Override
    public void update(reclamation r) throws SQLException {
        String sql = "UPDATE reclamation SET titre=?, description=?, status=?, date=?, categorie_id=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, r.getTitre());
        ps.setString(2, r.getDescription());
        ps.setString(3, r.getStatus());
        ps.setDate(4, Date.valueOf(r.getDate()));
        ps.setInt(5, r.getCategorieId()); // 👈 mise à jour catégorie aussi
        ps.setInt(6, r.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(reclamation r) throws SQLException {
        String query = "DELETE FROM reclamation WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, r.getId());
        ps.executeUpdate();
    }

    @Override
    public List<reclamation> readAll() throws SQLException {
        List<reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            reclamation r = new reclamation(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("categorie_id") // 👈 ajout ici
            );
            list.add(r);
        }

        return list;
    }
}
