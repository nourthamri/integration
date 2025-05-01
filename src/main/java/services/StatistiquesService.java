package services;

import models.reclamation;
import utils.MyConnection;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;



public class StatistiquesService {
    private Connection cnx = MyConnection.getInstance().getConnection();

    // Nombre de réclamations par statut
    public Map<String, Integer> getStatsParStatut() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT status, COUNT(*) AS total FROM reclamation GROUP BY status";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("total"));
            }
        }
        return stats;
    }
    // Nombre de Réclamations par Catégorie
    public Map<String, Integer> getStatsParCategorie() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT cr.nom AS categorie, COUNT(*) AS total " +
                "FROM reclamation r " +
                "JOIN categorie_reclamation cr ON r.categorie_id = cr.id " + // 👈 Table corrigée
                "GROUP BY cr.nom";

        System.out.println("Requête exécutée : " + query);

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String categorie = rs.getString("categorie");
                int total = rs.getInt("total");
                System.out.println("Catégorie: " + categorie + " | Total: " + total);
                stats.put(categorie, total);
            }
        }
        return stats;
    }

    public Map<String, Integer> getReclamationsParMois() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT DATE_FORMAT(date, '%Y-%m') AS mois, COUNT(*) AS total " +
                "FROM reclamation " +
                "GROUP BY DATE_FORMAT(date, '%Y-%m')";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("mois"), rs.getInt("total"));
            }
        }
        return stats;
    }
}