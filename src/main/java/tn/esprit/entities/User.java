package tn.esprit.entities;

public class User {
    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String mdp;


    public User() {}

    public User(String nom, String prenom, String email, String role, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.mdp = mdp;
    }




    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}
