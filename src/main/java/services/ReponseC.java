package services;

import models.reponse;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseC implements IService<reponse> {

    Connection cnx = MyConnection.getInstance().getConnection();

    @Override
    public void create(reponse r) throws SQLException {
        String query = "INSERT INTO reponse (reclamation_id, contenu, date) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, r.getReclamation_id());
        ps.setString(2, r.getContenu());
        ps.setDate(3, Date.valueOf(r.getDate()));
        ps.executeUpdate();
    }

    @Override
    public void update(reponse r) throws SQLException {
        String sql = "UPDATE reponse SET reclamation_id=?, contenu=?, date=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, r.getReclamation_id());
        ps.setString(2, r.getContenu());
        ps.setDate(3, Date.valueOf(r.getDate()));
        ps.setInt(4, r.getId());
        ps.executeUpdate();
    }


    @Override
    public void delete(reponse r) throws SQLException {
        String sql = "DELETE FROM reponse WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, r.getId());
        ps.executeUpdate();
    }



    @Override
    public List<reponse> readAll() throws SQLException {
        List<reponse> list = new ArrayList<>();
        String query = "SELECT * FROM reponse";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            reponse r = new reponse(
                    rs.getInt("id"),
                    rs.getInt("reclamation_id"),
                    rs.getString("contenu"),
                    rs.getDate("date").toLocalDate()
            );
            list.add(r);
        }

        return list;
    }
    public List<reponse> readByReclamationId(int idReclamation) throws SQLException {
        List<reponse> list = new ArrayList<>();
        String query = "SELECT * FROM reponse WHERE reclamation_id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, idReclamation);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            reponse r = new reponse(
                    rs.getInt("id"),
                    rs.getInt("reclamation_id"),
                    rs.getString("contenu"),
                    rs.getDate("date").toLocalDate()
            );
            list.add(r);
        }

        return list;
    }

}
