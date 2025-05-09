package tn.esprit.entities;

import java.time.LocalDateTime;

public class Commentaire {
    private Integer id;
    private String content;
    private int postId;
    private Integer parentId; // Nouveau champ pour les réponses
    private LocalDateTime createdAt;

    public Commentaire() {
        this.createdAt = LocalDateTime.now();
    }

    public Commentaire(String content, int postId) {
        this();
        this.content = content;
        this.postId = postId;
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}