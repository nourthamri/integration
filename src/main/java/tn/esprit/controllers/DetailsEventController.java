package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceParticipant;
import tn.esprit.services.ServiceEvent;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
// PDF generation with iText
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

// QR code with ZXing
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

// JavaFX & AWT image handling
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import tn.esprit.util.ICalendarExporter;

// Java IO and utility
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class DetailsEventController {
    @FXML
    private Label nomLabelDetail;
    @FXML
    private Label lieuLabelDetail;
    @FXML
    private Label desLabelDetail;
    @FXML
    private Label dateLabelDetail;
    @FXML
    private ImageView ImageViewDetail;
    @FXML
    private Button btnsupp;
    @FXML
    private Button btnpart;
    @FXML
    private Button btnmodifier;
    @FXML
    private Button btnparticiper;
    @FXML
    private Button btnreturn;
    @FXML
    private Button btnShareFacebook;
    private Event event;
    private final ServiceParticipant serviceParticipant = new ServiceParticipant();
    private final ServiceEvent serviceEvent = new ServiceEvent();
    private AfficherEventController afficherEventController;
    @FXML
    private Button btnExportCalendar;
    @FXML
    public void initialize() {
        btnmodifier.setOnAction(event -> modifierEvent());
        btnsupp.setOnAction(event -> supprimerEvent());
        btnExportCalendar.setOnAction(this::handleExportToCalendar);
        // Set the button styles and actions
        updateParticipationButton();

        // Social Media Buttons
        btnShareFacebook.setOnAction(event -> shareEventOnSocialMedia());

    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            nomLabelDetail.setText(event.getNom());
            lieuLabelDetail.setText(event.getLatitude() + " " + event.getLongitude());
            desLabelDetail.setText(event.getDescription());
            dateLabelDetail.setText(event.getDate().toString());
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                Image image = new Image("file:" + event.getImage());
                ImageViewDetail.setImage(image);
            }
        }
    }

    @FXML
    private void shareEventOnSocialMedia( ) {
        if (event == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucun événement sélectionné.");
            return;
        }

        String eventUrl = "https://example.com/event?id=" + event.getId_event();
        String eventTitle = event.getNom();
        String eventDate = event.getDate().toString();
        String eventImage = event.getImage();

        String shareUrl = "";
        shareUrl = "https://www.facebook.com/sharer/sharer.php?u=" + eventUrl + eventTitle + eventDate + eventImage;


        try {
            // Open share URL in the default browser
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(shareUrl));
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page de partage.");
        }
    }

    @FXML
    private void Participer() {
        if (event == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucun événement sélectionné.");
            return;
        }

        int idEvent = event.getId_event();
        int idUser = 1; // Remplacer par l'ID réel de l'utilisateur connecté

        try {
            if (serviceParticipant.aDejaParticipe(idUser)) {
                afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Vous avez déjà participé à cet événement !");
                return;
            }

            serviceEvent.participer(idUser, idEvent);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Vous avez participé à l'événement avec succès !");

            Image qrImage = saveQRCodeAsImage(event, idUser); // One line does both
            showQRCodePopup(qrImage);

            updateParticipationButton();

        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image generateQRCodeImage(Event event, int userId) throws Exception {
        String content = "🎉 Invitation à l'événement 🎉\n"
                + "Nom : " + event.getNom() + "\n"
                + "Date : " + event.getDate() + "\n"
                + "Lieu : " + event.getLatitude() + ", " + event.getLongitude() + "\n"
                + "Description : " + event.getDescription() + "\n"
                + "Participant ID : " + userId;

        int size = 300;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        return SwingFXUtils.toFXImage(qrImage, null);
    }

    private void showQRCodePopup(Image qrImage) {
        ImageView imageView = new ImageView(qrImage);
        imageView.setFitHeight(250);
        imageView.setFitWidth(250);

        VBox box = new VBox(new Label("Voici votre QR code d'invitation :"), imageView);
        box.setSpacing(10);
        box.setPadding(new Insets(15));

        Scene scene = new Scene(box);
        Stage stage = new Stage();
        stage.setTitle("QR Code Invitation");
        stage.setScene(scene);
        stage.show();
    }

    private Image saveQRCodeAsImage(Event event, int userId) throws Exception {
        String content = "🎉 Invitation à l'événement 🎉\n"
                + "Nom : " + event.getNom() + "\n"
                + "Date : " + event.getDate() + "\n"
                + "Lieu : " + event.getLatitude() + ", " + event.getLongitude() + "\n"
                + "Description : " + event.getDescription() + "\n"
                + "Participant ID : " + userId;

        int size = 300;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Save to disk
        String fileName = "qr_invitation_" + event.getId_event() + "_user_" + userId + ".png";
        File qrFile = new File("qr_codes/" + fileName);
        qrFile.getParentFile().mkdirs(); // Create folder if not exists
        ImageIO.write(qrImage, "png", qrFile);

        // Return as JavaFX Image
        return SwingFXUtils.toFXImage(qrImage, null);
    }

    @FXML
    private void supprimerEvent() {
        if (event == null) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Voulez-vous vraiment supprimer cet événement ?");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEvent.supprimer_t(event.getId_event());
                if (afficherEventController != null) {
                    afficherEventController.rafraichirAffichage();
                }
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Événement supprimé avec succès !");
                Stage stage = (Stage) btnsupp.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'événement : " + e.getMessage());
            }
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void modifierEvent() {
        // Logic to modify event
    }

    @FXML
    private void handleReturnButtonClick() {
        Stage stage = (Stage) btnreturn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void updateParticipationButton() {
        int idUser = 1; // Replace with actual user ID
        try {
            boolean participeDeja = serviceParticipant.aDejaParticipe(idUser);
            if (participeDeja) {
                btnparticiper.setText("Annuler Participation");
                btnparticiper.setOnAction(e -> annulerParticipation());
            } else {
                btnparticiper.setText("Participer");
                btnparticiper.setOnAction(e -> Participer());
            }
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de vérifier la participation : " + e.getMessage());
        }
    }




@FXML
    private void annulerParticipation() {
        int idUser = 1; // Replace with actual user ID
        try {
            serviceParticipant.annulerParticipation(idUser);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Participation annulée avec succès !");
            updateParticipationButton();
        } catch (SQLException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'annuler la participation : " + e.getMessage());
        }
    }
    @FXML
    private void afficherListeParticipants() {
        if (event == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucun événement sélectionné.");
            return;
        }

        System.out.println("🔹 ID de l'événement envoyé : " + event.getId_event());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/listeParticipants.fxml"));
            Parent root = loader.load();

            listeParticipantControlle controller = loader.getController();
            controller.setEvent(event);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Participants de " + event.getNom());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher la liste des participants.");
        }
    }
   @FXML
    private void handleExportToCalendar(ActionEvent eventAction) {
        if (event == null) {
            showAlert("Erreur", "Aucun événement à exporter.");
            return;
        }

       try {
           File icsFile = ICalendarExporter.exportToICalendar(event);

           // Optionally print path for debugging
           System.out.println("ICS file saved to: " + icsFile.getAbsolutePath());

           // Cross-platform opening
           if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
               Desktop.getDesktop().open(icsFile);
           } else {
               String os = System.getProperty("os.name").toLowerCase();
               if (os.contains("win")) {
                   Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", icsFile.getAbsolutePath()});
               } else if (os.contains("mac")) {
                   Runtime.getRuntime().exec(new String[]{"open", icsFile.getAbsolutePath()});
               } else if (os.contains("nux")) {
                   Runtime.getRuntime().exec(new String[]{"xdg-open", icsFile.getAbsolutePath()});
               }
           }

       } catch (IOException e) {
           e.printStackTrace();
           showAlert("Erreur", "Impossible d'ouvrir l'événement exporté.");
       }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setAfficherEventController(AfficherEventController controller) {
        this.afficherEventController = controller;
    }
}