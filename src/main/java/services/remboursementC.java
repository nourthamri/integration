package services;

import models.remboursement;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class remboursementC {

    Connection cnx = MyConnection.getInstance().getConnection();

    public void create(remboursement r) throws SQLException {
        String query = "INSERT INTO remboursement (reclamation_id, montant, date) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, r.getReclamationId());
        ps.setDouble(2, r.getMontant());
        ps.setDate(3, Date.valueOf(r.getDate()));
        ps.executeUpdate();
    }
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM remboursement WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<remboursement> readAll() throws SQLException {
        List<remboursement> list = new ArrayList<>();
        String sql = "SELECT * FROM remboursement";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                remboursement r = new remboursement(
                        rs.getInt("id"),
                        rs.getInt("reclamation_id"),
                        rs.getDouble("montant"),
                        rs.getDate("date").toLocalDate()
                );
                list.add(r);
            }
        }
        return list;
    }


    public List<remboursement> readByReclamationId(int reclamationId) throws SQLException {
        List<remboursement> list = new ArrayList<>();
        String sql = "SELECT * FROM remboursement WHERE reclamation_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, reclamationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    remboursement r = new remboursement(
                            rs.getInt("id"),
                            rs.getInt("reclamation_id"),
                            rs.getDouble("montant"),
                            rs.getDate("date").toLocalDate()
                    );
                    list.add(r);
                }
            }
        }
        return list;
    }

    public boolean remboursementExistePour(int idReclamation) throws SQLException {
        String query = "SELECT COUNT(*) FROM remboursement WHERE reclamation_id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, idReclamation);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

}
