package controllers.reclamation;

// Imports
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.stage.FileChooser;
import services.StatistiquesService;
import utils.ExcelExporter;
import utils.PdfExporter;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javafx.scene.control.Alert;

public class DashboardController {
    @FXML private PieChart pieChartStatut;
    @FXML private BarChart<String, Number> barChartCategories;
    @FXML private LineChart<String, Number> lineChartMois;

    private final StatistiquesService statsService = new StatistiquesService();

    @FXML
    public void initialize() {
        try {
            // Camembert des statuts
            Map<String, Integer> statsStatut = statsService.getStatsParStatut();
            statsStatut.forEach((statut, total) ->
                    pieChartStatut.getData().add(new PieChart.Data(statut, total))
            );

            // Graphique des catégories
            barChartCategories.getData().add(createCategorySeries());

            // Graphique des mois
            Map<String, Integer> statsMois = new TreeMap<>(statsService.getReclamationsParMois());
            XYChart.Series<String, Number> seriesMois = new XYChart.Series<>();
            statsMois.forEach((mois, total) ->
                    seriesMois.getData().add(new XYChart.Data<>(mois, total))
            );
            lineChartMois.getData().add(seriesMois);

        } catch (SQLException e) {
            showAlert("Erreur de chargement : " + e.getMessage());
        }
    }

    private XYChart.Series<String, Number> createCategorySeries() throws SQLException {
        XYChart.Series<String, Number> seriesCategorie = new XYChart.Series<>();
        Map<String, Integer> statsCategorie = statsService.getStatsParCategorie();
        statsCategorie.forEach((categorie, total) ->
                seriesCategorie.getData().add(new XYChart.Data<>(categorie, total))
        );
        return seriesCategorie;
    }

    @FXML
    private void handleExportStats() {
        try {
            // Récupérer les données statistiques
            Map<String, Integer> statsStatut = statsService.getStatsParStatut();
            Map<String, Integer> statsCategorie = statsService.getStatsParCategorie();
            Map<String, Integer> statsMois = new TreeMap<>(statsService.getReclamationsParMois());

            // Vérifier si les données sont vides
            if (statsStatut.isEmpty() && statsCategorie.isEmpty() && statsMois.isEmpty()) {
                showAlert("Aucune donnée statistique à exporter !");
                return;
            }

            // Ouvrir le dialogue de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les statistiques");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(pieChartStatut.getScene().getWindow());

            if (file != null) {
                String path = file.getAbsolutePath();

                if (path.endsWith(".xlsx")) {
                    ExcelExporter.exportStatsToExcel(
                            statsStatut,
                            statsCategorie,
                            statsMois,
                            path
                    );
                }
                else if (path.endsWith(".pdf")) {
                    PdfExporter.exportStatsToPDF(
                            statsStatut,
                            statsCategorie,
                            statsMois,
                            path
                    );
                }

                showAlert("Export réussi vers :\n" + path);
            }
        }
        catch (SQLException e) {
            showAlert("Erreur de base de données : " + e.getMessage());
        }
        catch (Exception e) {
            showAlert("Erreur lors de l'export : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}