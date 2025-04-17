package entities;

import java.time.LocalDate;

public class reclamation {

    private int id, id_user;
    private String titre, description, status;
    private LocalDate date;
    private int categorieId;

    // Getters et Setters
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
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
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

    // Constructeur complet avec categorieId
    public reclamation(int id, int id_user, String titre, String description, String status, LocalDate date, int categorieId) {
        this.id = id;
        this.id_user = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
        this.categorieId = categorieId;
    }

    // Constructeur sans ID (ex: pour insertion) avec categorieId
    public reclamation(int id_user, String titre, String description, String status, LocalDate date, int categorieId) {
        this.id_user = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
        this.categorieId = categorieId;
    }

    // Constructeurs existants (si tu veux les garder aussi)
    public reclamation(int id, int id_user, String titre, String description, String status, LocalDate date) {
        this.id = id;
        this.id_user = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public reclamation(int id_user, String titre, String description, String status, LocalDate date) {
        this.id_user = id_user;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", id_user=" + id_user +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", categorieId=" + categorieId +
                '}';
    }
}
