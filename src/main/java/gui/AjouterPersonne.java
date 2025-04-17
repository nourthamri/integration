package gui;

import entities.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.PersonService;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterPersonne {

    private final PersonService personService = new PersonService();
    @FXML
    private TextField TFAge;

    @FXML
    private TextField TFNom;

    @FXML
    private TextField TFPrenom;

    @FXML
    void ajouter(ActionEvent event) {
        int age = Integer.parseInt(TFAge.getText());
        String Nom = TFNom.getText();
        String Prenom = TFPrenom.getText();

        Person p = new Person(age,Prenom,Nom);

        try {
            personService.create(p);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }


    }
    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPersonne.fxml"));
            TFAge.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }






}
