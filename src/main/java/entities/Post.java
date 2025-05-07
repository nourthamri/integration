package entities;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.BadWordsFilter;


import java.time.LocalDateTime;

public class Post {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObservableList<Commentaire> commentaires = FXCollections.observableArrayList();

    public Post() {
        this.createdAt.set(LocalDateTime.now());
    }

    public Post(String title, String content) {
        this();
        this.setTitle(title); // Utilisation du setter pour la validation
        this.setContent(content);
    }

    public Post(int id, String title, String content, LocalDateTime createdAt) {
        this(title, content);
        this.id.set(id);
        this.createdAt.set(createdAt);
    }

    // Property Accessors
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty contentProperty() { return content; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public ObservableList<Commentaire> getCommentaires() { return commentaires; }

    // Getters & Setters avec validation
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide");
        }
        if (BadWordsFilter.containsBadWords(title)) {
            throw new IllegalArgumentException("Titre inapproprié détecté");
        }
        this.title.set(title);
    }

    public String getContent() { return content.get(); }
    public void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu ne peut pas être vide");
        }
        if (BadWordsFilter.containsBadWords(content)) {
            throw new IllegalArgumentException("Contenu inapproprié détecté");
        }
        this.content.set(content);
    }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }

    // Gestion des commentaires
    public void addCommentaire(Commentaire commentaire) {
        commentaires.add(commentaire);
        commentaire.setPostId(this.getId());
    }

    public void removeCommentaire(Commentaire commentaire) {
        commentaires.remove(commentaire);
        commentaire.setPostId(0);
    }

    public int getCommentCount() {
        return commentaires.size();
    }
}