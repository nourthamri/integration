package tn.esprit.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.entities.Reaction;
import tn.esprit.util.MaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReactionService {
    private final Connection cnx = MaConnexion.getInstance().getCnx();

    public boolean addOrUpdateReaction(Reaction reaction) {
        Reaction existing = getReactionByUserAndPost(reaction.getUserId(), reaction.getPostId());

        if (existing != null) {
            if (existing.getEmoji().equals(reaction.getEmoji())) {
                // Même emoji => suppression
                return delete(existing.getId());
            } else {
                // Emoji différent => mise à jour
                existing.setEmoji(reaction.getEmoji());
                return update(existing);
            }
        } else {
            // Nouvelle réaction
            return add(reaction);
        }
    }

    public boolean add(Reaction reaction) {
        String query = "INSERT INTO reaction (user_id, post_id, emoji, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reaction.getUserId());
            pst.setInt(2, reaction.getPostId());
            pst.setString(3, reaction.getEmoji());
            pst.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        reaction.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Reaction reaction) {
        String query = "UPDATE reaction SET emoji = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, reaction.getEmoji());
            pst.setInt(2, reaction.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM reaction WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
        }
        return false;
    }

    public Reaction getReactionByUserAndPost(int userId, int postId) {
        String query = "SELECT * FROM reaction WHERE user_id = ? AND post_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setInt(2, postId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reaction r = new Reaction();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setPostId(rs.getInt("post_id"));
                    r.setEmoji(rs.getString("emoji"));
                    r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return null;
    }

    public Map<String, Long> getReactionCountsForPost(int postId) {
        Map<String, Long> counts = new HashMap<>();
        String query = "SELECT emoji, COUNT(*) as count FROM reaction WHERE post_id = ? GROUP BY emoji";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, postId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("emoji"), rs.getLong("count"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des réactions: " + e.getMessage());
        }

        return counts;
    }

    public ObservableList<Reaction> getReactionsForPost(int postId) {
        ObservableList<Reaction> reactions = FXCollections.observableArrayList();
        String query = "SELECT * FROM reaction WHERE post_id = ? ORDER BY created_at DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, postId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reaction r = new Reaction();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setPostId(rs.getInt("post_id"));
                    r.setEmoji(rs.getString("emoji"));
                    r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    reactions.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }

        return reactions;
    }

    public boolean toggleReaction(int currentUserId, int id, String emoji) {
        return false;
    }

    public String getUserReaction(int currentUserId, int id) {
        return null;
    }
}