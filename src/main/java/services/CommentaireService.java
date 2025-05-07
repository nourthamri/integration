package services;

import entities.Commentaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.MaConnexion;


import java.sql.*;
import java.time.LocalDateTime;

public class CommentaireService {
    private final Connection cnx = MaConnexion.getInstance().getCnx();

    public boolean add(Commentaire commentaire) {
        String query = "INSERT INTO commentaire (content, post_id, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, commentaire.getContent());
            pst.setInt(2, commentaire.getPostId());
            pst.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        commentaire.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Commentaire> getAll() {
        ObservableList<Commentaire> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM commentaire";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setContent(rs.getString("content"));
                c.setPostId(rs.getInt("post_id"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des commentaires", e);
        }
        return list;
    }

    public ObservableList<Commentaire> getCommentairesParPost(int postId) {
        ObservableList<Commentaire> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM commentaire WHERE post_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setId(rs.getInt("id"));
                c.setContent(rs.getString("content"));
                c.setPostId(rs.getInt("post_id"));
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Commentaire commentaire) {
        String query = "UPDATE commentaire SET content = ?, post_id = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, commentaire.getContent());
            pst.setInt(2, commentaire.getPostId());
            pst.setInt(3, commentaire.getId());
            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du commentaire", e);
        }
    }

    public boolean delete(int id) {
        String query = "DELETE FROM commentaire WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du commentaire", e);
        }
    }

    public Commentaire getById(int id) {
        String query = "SELECT * FROM commentaire WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Commentaire c = new Commentaire();
                    c.setId(rs.getInt("id"));
                    c.setContent(rs.getString("content"));
                    c.setPostId(rs.getInt("post_id"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return null;
    }
}