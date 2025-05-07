package controllers.forum;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import services.PostService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatsViewController {
    @FXML private Label topDateLabel;
    @FXML private Label postCountLabel;
    @FXML private BarChart<String, Number> postsChart;

    private final PostService postService = new PostService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        loadTopDateStats();
        loadChart();
    }

    private void loadTopDateStats() {
        Map.Entry<LocalDate, Long> topDate = postService.findDateWithMostPosts();
        topDateLabel.setText(DATE_FORMATTER.format(topDate.getKey()));
        postCountLabel.setText(topDate.getValue() + " posts");
    }

    private void loadChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Posts par date");

        postService.getPostsCountByDate().forEach((date, count) ->
                series.getData().add(new XYChart.Data<>(DATE_FORMATTER.format(date), count))
        );

        postsChart.getData().add(series);
        postsChart.setLegendVisible(false);
    }
}