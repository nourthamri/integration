package tn.esprit.entities;

import java.time.LocalDateTime;

public class Event {
    private int id_event;
    private String nom;
    private String image;
    private String description;
    private LocalDateTime date;
    private double latitude;
    private double longitude;


    // Constructeur vide (requis pour certaines opérations de mapping)
    public Event() {}

    // Constructeur sans ID (utilisé pour les insertions)
    public Event(String nom, String image, String description, LocalDateTime date, double latitude, double longitude) {
        this.nom = nom;
        this.image = image;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructeur avec ID (utilisé pour les sélections depuis la DB)
    public Event(int id_event, String nom, String image, String description, LocalDateTime date, double latitude, double longitude) {
        this.id_event = id_event;
        this.nom = nom;
        this.image = image;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters
    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id_event=" + id_event +
                ", nom='" + nom + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }


}
