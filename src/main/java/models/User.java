package models;

public class User {
    private int id;
    private String email;

    // Constructeur
    public User(int id, String email) {
        this.id = id;
        this.email = email;
    }

    // Getters
    public int getId() {
        return this.id; // ✅ Ajout du return manquant
    }

    public String getEmail() {
        return email;
    }
}