package tn.esprit.entities;

import java.time.LocalDateTime;

public class Reaction {
    private Integer id;
    private int userId; // user_id statique comme vous l'avez mentionné
    private int postId;
    private String emoji; // Au lieu de ReactionType
    private LocalDateTime createdAt;

    public Reaction() {
        this.createdAt = LocalDateTime.now();
    }

    public Reaction(int userId, int postId, String emoji) {
        this();
        this.userId = userId;
        this.postId = postId;
        this.emoji = emoji;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}