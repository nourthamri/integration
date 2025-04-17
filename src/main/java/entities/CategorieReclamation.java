package entities;

public class CategorieReclamation {
    private int id;
    private String nom;

    public CategorieReclamation(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public CategorieReclamation(String nom) {
        this.nom = nom;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() {
        return nom; // utile pour l’affichage dans la ComboBox
    }
}
