package gui;

import entities.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.PersonService;

import java.sql.SQLException;
import java.util.List;


public class AfficherPersonne {

    private final PersonService ps = new PersonService();
    @FXML
    private TableColumn<Person, Integer> ageCol;
    @FXML
    private TableColumn<Person, String> nomCol;
    @FXML
    private TableColumn<Person, String> prenomCol;
    @FXML
    private TableView<Person> tableView;

    @FXML
    void initialize() {
        try {
            List<Person> personnes = ps.readAll();
            System.out.println(personnes);
            ObservableList<Person> observableList = FXCollections.observableList(personnes);
            tableView.setItems(observableList);

            nomCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            prenomCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}
