package models;

import java.time.LocalDate;

public class remboursement {
    private int id;
    private int reclamationId;
    private double montant;
    private LocalDate date;

    public remboursement(int id, int reclamationId, double montant, LocalDate date) {
        this.id = id;
        this.reclamationId = reclamationId;
        this.montant = montant;
        this.date = date;
    }

    public remboursement(int reclamationId, double montant, LocalDate date) {
        this.reclamationId = reclamationId;
        this.montant = montant;
        this.date = date;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReclamationId() { return reclamationId; }
    public void setReclamationId(int reclamationId) { this.reclamationId = reclamationId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
