package tn.esprit.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.entities.Reaction;
import tn.esprit.util.MaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReactionService {
    private final Connection cnx = MaConnexion.getInstance().getCnx();

    public boolean toggleReaction(int userId, int postId, String emoji) {
        try {
            // Vérifie si l'utilisateur a déjà réagi à ce post
            Optional<Reaction> existing = getReactionByUserAndPost(userId, postId);

            if (existing.isPresent()) {
                Reaction reaction = existing.get();
                if (reaction.getEmoji().equals(emoji)) {
                    // Supprime la réaction si c'est le même emoji
                    return delete(reaction.getId());
                } else {
                    // Met à jour l'emoji si différent
                    reaction.setEmoji(emoji);
                    return update(reaction);
                }
            } else {
                // Crée une nouvelle réaction
                Reaction newReaction = new Reaction(userId, postId, emoji);
                return add(newReaction);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la gestion de la réaction: " + e.getMessage());
            return false;
        }
    }

    private boolean add(Reaction reaction) throws SQLException {
        String query = "INSERT INTO reaction (user_id, post_id, emoji) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reaction.getUserId());
            pst.setInt(2, reaction.getPostId());
            pst.setString(3, reaction.getEmoji());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        reaction.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean update(Reaction reaction) throws SQLException {
        String query = "UPDATE reaction SET emoji = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, reaction.getEmoji());
            pst.setInt(2, reaction.getId());
            return pst.executeUpdate() > 0;
        }
    }

    private boolean delete(int id) throws SQLException {
        String query = "DELETE FROM reaction WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }

    public Optional<Reaction> getReactionByUserAndPost(int userId, int postId) throws SQLException {
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

                    // Gestion optionnelle de created_at si la colonne existe
                    try {
                        Timestamp timestamp = rs.getTimestamp("created_at");
                        if (timestamp != null) {
                            r.setCreatedAt(timestamp.toLocalDateTime());
                        }
                    } catch (SQLException e) {
                        // La colonne created_at n'existe pas, on ignore
                    }

                    return Optional.of(r);
                }
            }
        }
        return Optional.empty();
    }

    public Map<String, Integer> getReactionCountsForPost(int postId) {
        Map<String, Integer> counts = new HashMap<>();
        String query = "SELECT emoji, COUNT(*) as count FROM reaction WHERE post_id = ? GROUP BY emoji";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, postId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("emoji"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des réactions: " + e.getMessage());
        }

        return counts;
    }

    public ObservableList<Reaction> getReactionsForPost(int postId) {
        ObservableList<Reaction> reactions = FXCollections.observableArrayList();
        String query = "SELECT * FROM reaction WHERE post_id = ? ORDER BY id DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, postId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reaction r = new Reaction();
                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setPostId(rs.getInt("post_id"));
                    r.setEmoji(rs.getString("emoji"));

                    // Gestion optionnelle de created_at
                    try {
                        Timestamp timestamp = rs.getTimestamp("created_at");
                        if (timestamp != null) {
                            r.setCreatedAt(timestamp.toLocalDateTime());
                        }
                    } catch (SQLException e) {
                        // Colonne non présente, on ignore
                    }

                    reactions.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réactions: " + e.getMessage());
        }

        return reactions;
    }

    public String getUserReaction(int userId, int postId) {
        try {
            Optional<Reaction> reaction = getReactionByUserAndPost(userId, postId);
            return reaction.map(Reaction::getEmoji).orElse(null);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la réaction utilisateur: " + e.getMessage());
            return null;
        }
    }
}