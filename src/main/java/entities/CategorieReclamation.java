package entities;

public class CategorieReclamation {

    private int id;
    private String nom;

    // Constructeurs
    public CategorieReclamation() {
        // Constructeur vide (utile pour l'injection ou certaines bibliothèques)
    }

    public CategorieReclamation(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public CategorieReclamation(String nom) {
        this.nom = nom;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Pour l'affichage dans une ComboBox
    @Override
    public String toString() {
        return nom;
    }
}
