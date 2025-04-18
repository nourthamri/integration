package tn.esprit.entities;


import java.time.LocalDateTime;

public class Event {
    String nom;
    String image;
    String desc;
    String lieu;
    int id_event;
    LocalDateTime date;

    public Event(String nom, String image, String desc, String lieu, LocalDateTime date) {
        this.nom = nom;
        this.image = image;
        this.desc = desc;
        this.lieu = lieu;
        this.date = date;
    }

    public Event(int id, String nom, String image, String desc, String lieu, LocalDateTime date) {
        this.nom = nom;
        this.image = image;
        this.desc = desc;
        this.id_event = id;
        this.lieu = lieu;
        this.date = date;
    }

    public Event() {

    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public int getId_event() {
        return this.id_event;
    }

    public String getLieu() {
        return this.lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String toString() {
        return "event{id_event=" + this.id_event + ",nom='" + this.nom + "', image='" + this.image + "', desc='" + this.desc + "', lieu='" + this.lieu + "', date=" + this.date + "}";
    }
}