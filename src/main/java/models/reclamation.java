package models;

import java.time.LocalDate;

public class reclamation {

    private int id, user_id;
    private String titre, description, status;
    private LocalDate date;
    private int categorieId;
    private String emailUtilisateur;

    private boolean recCompleted;
    private boolean reponseCompleted;
    private boolean remboursementCompleted;
    private boolean categorieCompleted;

    // Getters/Setters pour les nouveaux attributs
    public boolean isRecCompleted() { return recCompleted; }
    public void setRecCompleted(boolean b) { this.recCompleted = b; }

    public boolean isReponseCompleted() { return reponseCompleted; }
    public void setReponseCompleted(boolean b) { this.reponseCompleted = b; }

    public boolean isRemboursementCompleted() { return remboursementCompleted; }
    public void setRemboursementCompleted(boolean b) { this.remboursementCompleted = b; }

    public boolean isCategorieCompleted() { return categorieCompleted; }
    public void setCategorieCompleted(boolean b) { this.categorieCompleted = b; }

    // Reste du code inchangé
    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }

    public void setEmailUtilisateur(String emailUtilisateur) {
        this.emailUtilisateur = emailUtilisateur;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return user_id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public reclamation() {}

    // Constructeurs existants (inchangés)
    public reclamation(int id, int id_user, String titre, String description, String status, LocalDate date, int categorieId, String emailUtilisateur) {
        this.id = id;
        this.user_id = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
        this.categorieId = categorieId;
        this.emailUtilisateur = emailUtilisateur;
    }

    public reclamation(int id_user, String titre, String description, String status, LocalDate date, int categorieId) {
        this.user_id = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
        this.categorieId = categorieId;
    }

    public reclamation(int id, int id_user, String titre, String description, String status, LocalDate date) {
        this.id = id;
        this.user_id = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public reclamation(int userId) {
        this.user_id = userId;
        this.status = "en attente";
        this.date = LocalDate.now();
    }

    public reclamation(int id_user, String titre, String description, String status, LocalDate date) {
        this.user_id = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", categorieId=" + categorieId +
                '}';
    }

    public void setUser_id(int id_user) {
        this.user_id = id_user;
    }
}