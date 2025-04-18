module piproject {
    requires java.sql;
    requires javafx.fxml;
    requires javafx.controls;

    exports piproject;
    exports piproject.models;
    exports piproject.utils;
    exports piproject.interfaces;
    exports piproject.services;

    opens piproject.controllers to javafx.fxml; // ✅ Allows JavaFX to access controllers
}
