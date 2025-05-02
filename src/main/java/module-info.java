module piproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires itextpdf;

    requires proto.google.cloud.vertexai.v1;
    requires java.net.http;
    requires com.google.api.client;
    requires com.google.zxing.javase;
    requires com.google.zxing;

    exports piproject;
    exports piproject.models;


    exports piproject.utils;
    exports piproject.interfaces;
    exports piproject.services;

    opens piproject.controllers to javafx.fxml; // Permet à JavaFX d'accéder aux contrôleurs
    opens piproject.models to javafx.fxml; // Ajoute l'accès aux modèles si nécessaire pour JavaFX
}


