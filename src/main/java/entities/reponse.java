
package entities;

import java.time.LocalDate;

public class reponse {
    private int id;
    private int reclamation_id;
    private String contenu;
    private LocalDate date;

    public reponse(int id, int reclamation_id, String contenu, LocalDate date) {
        this.id = id;
        this.reclamation_id = reclamation_id;
        this.contenu = contenu;
        this.date = date;
    }

    public reponse(int reclamation_id, String contenu, LocalDate date) {
        this.reclamation_id = reclamation_id;
        this.contenu = contenu;
        this.date = date;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReclamation_id() { return reclamation_id; }
    public void setReclamation_id(int reclamation_id) { this.reclamation_id = reclamation_id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
