package services;

import models.reclamation;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;

public class reclamationC implements IService<reclamation> {

    Connection cnx = MyConnection.getInstance().getConnection();
    private EmailC emailService = new EmailC();

    public void marquerCommeResolue(int idReclamation) {
        reclamation rec = getById(idReclamation);
        rec.setStatus("resolved");

        try {
            System.out.println("Tentative d'envoi d'email à : " + rec.getEmailUtilisateur());
            String message = "Votre réclamation #" + idReclamation + " a été résolue.";
            emailService.envoyerEmail(rec.getEmailUtilisateur(), "PROBLÈME RÉSOLU", message);
            System.out.println("Email envoyé avec succès !");

            NotificationService.getInstance().showNotification(
                    "Réclamation résolue",
                    "Réclamation #" + idReclamation + " traitée !",
                    idReclamation
            );
        } catch (MessagingException e) {
            System.err.println("Échec d'envoi d'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public reclamation getById(int id) {
        reclamation reclamation = null;
        String query = "SELECT * FROM reclamation WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                reclamation = new reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setUser_id(rs.getInt("user_id"));
                reclamation.setTitre(rs.getString("titre"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setStatus(rs.getString("status"));
                reclamation.setDate(rs.getDate("date").toLocalDate());
                reclamation.setCategorieId(rs.getInt("categorie_id"));
                reclamation.setEmailUtilisateur(rs.getString("emailUtilisateur"));

                // Nouveaux champs de progression
                reclamation.setRecCompleted(rs.getBoolean("rec_completed"));
                reclamation.setReponseCompleted(rs.getBoolean("reponse_completed"));
                reclamation.setRemboursementCompleted(rs.getBoolean("remboursement_completed"));
                reclamation.setCategorieCompleted(rs.getBoolean("categorie_completed"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération : " + e.getMessage());
        }
        return reclamation;
    }

    @Override
    public void create(reclamation r) throws SQLException {
        String query = "INSERT INTO reclamation (user_id, titre, description, status, date, "
                + "categorie_id, emailUtilisateur, rec_completed, reponse_completed, "
                + "remboursement_completed, categorie_completed) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, r.getId_user());
        ps.setString(2, r.getTitre());
        ps.setString(3, r.getDescription());
        ps.setString(4, r.getStatus());
        ps.setDate(5, Date.valueOf(r.getDate()));
        ps.setInt(6, r.getCategorieId());
        ps.setString(7, r.getEmailUtilisateur());

        // Valeurs par défaut pour la progression
        ps.setBoolean(8, true);  // rec_completed = true à la création
        ps.setBoolean(9, false); // reponse_completed
        ps.setBoolean(10, false); // remboursement_completed
        ps.setBoolean(11, false); // categorie_completed

        ps.executeUpdate();
    }

    @Override
    public void update(reclamation r) throws SQLException {
        String sql = "UPDATE reclamation SET titre=?, description=?, status=?, date=?, "
                + "categorie_id=?, rec_completed=?, reponse_completed=?, "
                + "remboursement_completed=?, categorie_completed=? WHERE id=?";

        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, r.getTitre());
        ps.setString(2, r.getDescription());
        ps.setString(3, r.getStatus());
        ps.setDate(4, Date.valueOf(r.getDate()));
        ps.setInt(5, r.getCategorieId());

        // Nouveaux champs de progression
        ps.setBoolean(6, r.isRecCompleted());
        ps.setBoolean(7, r.isReponseCompleted());
        ps.setBoolean(8, r.isRemboursementCompleted());
        ps.setBoolean(9, r.isCategorieCompleted());

        ps.setInt(10, r.getId());
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
                    rs.getInt("categorie_id"),
                    rs.getString("emailUtilisateur")
            );

            // Ajout des nouveaux champs
            r.setRecCompleted(rs.getBoolean("rec_completed"));
            r.setReponseCompleted(rs.getBoolean("reponse_completed"));
            r.setRemboursementCompleted(rs.getBoolean("remboursement_completed"));
            r.setCategorieCompleted(rs.getBoolean("categorie_completed"));

            list.add(r);
        }
        return list;
    }

    public List<reclamation> readByUserId(int userId) throws SQLException {
        List<reclamation> list = new ArrayList<>();
        String query = "SELECT * FROM reclamation WHERE user_id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            reclamation r = new reclamation(
                    rs.getInt("user_id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getDate("date").toLocalDate()
            );
            r.setId(rs.getInt("id"));
            r.setCategorieId(rs.getInt("categorie_id"));
            r.setEmailUtilisateur(rs.getString("emailUtilisateur"));

            // Ajout des nouveaux champs
            r.setRecCompleted(rs.getBoolean("rec_completed"));
            r.setReponseCompleted(rs.getBoolean("reponse_completed"));
            r.setRemboursementCompleted(rs.getBoolean("remboursement_completed"));
            r.setCategorieCompleted(rs.getBoolean("categorie_completed"));

            list.add(r);
        }
        return list;
    }
    public boolean hasReponse(int idReclamation) throws SQLException {
        String query = "SELECT COUNT(*) FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, idReclamation);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean hasRemboursement(int idReclamation) throws SQLException {
        String query = "SELECT COUNT(*) FROM remboursement WHERE reclamation_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, idReclamation);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}