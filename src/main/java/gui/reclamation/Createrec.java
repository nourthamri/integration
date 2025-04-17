package gui.reclamation;

import entities.CategorieReclamation;  // Assurez-vous d'importer la classe CategorieReclamation
import entities.reclamation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.reclamationC;
import services.CategorieReclamationC;  // Import du service pour récupérer les catégories

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Createrec implements Initializable {

    @FXML
    private TextField titre;

    @FXML
    private TextField desc;

    @FXML
    private DatePicker date;

    @FXML
    private Button add;

    @FXML
    private ComboBox<CategorieReclamation> categorieCombo;  // Déclaration du ComboBox pour les catégories

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation du ComboBox avec les catégories
        CategorieReclamationC service = new CategorieReclamationC();
        try {
            categorieCombo.getItems().addAll(service.readAll());  // Remplir le ComboBox avec les catégories récupérées
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Action du bouton "Ajouter"
        add.setOnAction(event -> {
            try {
                ajouterReclamation();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void ajouterReclamation() throws SQLException {
        String t = titre.getText();
        String d = desc.getText();
        LocalDate ld = date.getValue();

        // Récupérer la catégorie sélectionnée dans le ComboBox
        CategorieReclamation categorie = categorieCombo.getValue();

        // Ex : statut par défaut et user_id fictif (à adapter selon ton app)
        String statut = "en attente";
        int userId = 1;

        // Récupérer l'ID de la catégorie (probablement le champ ID dans CategorieReclamation)
        int categorieId = categorie != null ? categorie.getId() : 0; // Utiliser un ID par défaut si aucune catégorie n'est sélectionnée

        // Création de la réclamation avec la catégorie sélectionnée (en passant l'ID de la catégorie)
        reclamation r = new reclamation(userId, t, d, statut, ld);
        r.setCategorieId(categorieId);  // Assurez-vous d'ajouter l'ID de la catégorie à la réclamation

        reclamationC rc = new reclamationC();
        rc.create(r);

        // Nettoyage du formulaire après insertion
        titre.clear();
        desc.clear();
        date.setValue(null);
        categorieCombo.setValue(null);  // Réinitialiser le ComboBox

        System.out.println("Réclamation ajoutée !");
    }
}
