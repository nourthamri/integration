package entities;

import java.time.LocalDateTime;

public class Commentaire {
    private Integer id;
    private String content;
    private int postId;
    private LocalDateTime createdAt;

    public Commentaire() {
        this.createdAt = LocalDateTime.now();
    }

    public Commentaire(String content, int postId) {
        this();
        this.content = content;
        this.postId = postId;
    }

    // Getters
    public Integer getId() { return id; }
    public String getContent() { return content; }
    public int getPostId() { return postId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setPostId(int postId) { this.postId = postId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


}