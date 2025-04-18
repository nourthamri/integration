package piproject.models;

import java.util.Objects;

public class Product {
    private int id;
    private String nom;
    private String description;
    private int coupon;
    private float valeur;
    private String etat;
    private String dispo;
    private Category categorie; // Replaced String with Category
    private String image;

    // Constructors
    public Product() {}

    public Product(int id, String nom, String description, int coupon, float valeur, String etat, String dispo, Category categorie, String image) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.coupon = coupon;
        this.valeur = valeur;
        this.etat = etat;
        this.dispo = dispo;
        this.categorie = categorie; // Accepting Category object
        this.image = image;
    }

    public Product(String nom, String description, int coupon, float valeur, String etat, String dispo, Category categorie, String image) {
        this.nom = nom;
        this.description = description;
        this.coupon = coupon;
        this.valeur = valeur;
        this.etat = etat;
        this.dispo = dispo;
        this.categorie = categorie; // Accepting Category object
        this.image = image;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    public float getValeur() {
        return valeur;
    }

    public void setValeur(float valeur) {
        this.valeur = valeur;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDispo() {
        return dispo;
    }

    public void setDispo(String dispo) {
        this.dispo = dispo;
    }

    public Category getCategorie() {
        return categorie;
    }

    public void setCategorie(Category categorie) {
        this.categorie = categorie; // Setting Category object
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id == product.id && coupon == product.coupon && Float.compare(product.valeur, valeur) == 0 &&
                Objects.equals(nom, product.nom) &&
                Objects.equals(description, product.description) &&
                Objects.equals(etat, product.etat) &&
                Objects.equals(dispo, product.dispo) &&
                Objects.equals(categorie, product.categorie) && // Comparing Category objects
                Objects.equals(image, product.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, description, coupon, valeur, etat, dispo, categorie, image);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", coupon=" + coupon +
                ", valeur=" + valeur +
                ", etat='" + etat + '\'' +
                ", dispo='" + dispo + '\'' +
                ", categorie=" + categorie + // To string representation of Category object
                ", image='" + image + '\'' +
                '}';
    }
}
