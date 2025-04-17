package gui.categorie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class GestionCategorieController {

    @FXML
    private ListView<String> listCategorie;  // Liste des catégories
    @FXML
    private TextField nomField;  // Champ de texte pour le nom de la catégorie
    @FXML
    private Button btnAjouter;  // Bouton Ajouter
    @FXML
    private Button btnModifier;  // Bouton Modifier
    @FXML
    private Button btnSupprimer;  // Bouton Supprimer

    private ObservableList<String> categories;  // Liste observable pour contenir les catégories

    public GestionCategorieController() {
        // Initialisation de la liste des catégories
        categories = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        // Initialiser le ListView avec la liste de catégories
        listCategorie.setItems(categories);

        // Ajout d'un événement pour le bouton Ajouter
        btnAjouter.setOnAction(event -> ajouterCategorie());

        // Ajout d'un événement pour le bouton Modifier
        btnModifier.setOnAction(event -> modifierCategorie());

        // Ajout d'un événement pour le bouton Supprimer
        btnSupprimer.setOnAction(event -> supprimerCategorie());
    }

    // Méthode pour ajouter une catégorie
    private void ajouterCategorie() {
        String nomCategorie = nomField.getText().trim();
        if (!nomCategorie.isEmpty()) {
            categories.add(nomCategorie);  // Ajouter la catégorie à la liste
            nomField.clear();  // Effacer le champ de texte
        }
    }

    // Méthode pour modifier une catégorie
    private void modifierCategorie() {
        String selectedCategorie = listCategorie.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            String nomCategorie = nomField.getText().trim();
            if (!nomCategorie.isEmpty()) {
                categories.set(categories.indexOf(selectedCategorie), nomCategorie);  // Modifier la catégorie
                nomField.clear();  // Effacer le champ de texte
            }
        }
    }

    // Méthode pour supprimer une catégorie
    private void supprimerCategorie() {
        String selectedCategorie = listCategorie.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            categories.remove(selectedCategorie);  // Supprimer la catégorie sélectionnée
        }
    }
}
