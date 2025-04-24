package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.LoginHistory;
import org.example.services.LoginHistoryService;

public class LoginHistoryController {

    @FXML
    private TableView<LoginHistory> loginHistoryTable;

    @FXML
    private TableColumn<LoginHistory, String> ipColumn;

    @FXML
    private TableColumn<LoginHistory, String> deviceColumn;

    @FXML
    private TableColumn<LoginHistory, String> loginTimeColumn;

    private final LoginHistoryService loginHistoryService = new LoginHistoryService();

    @FXML
    public void initialize() {
        // Set up the TableView columns
        ipColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());
        deviceColumn.setCellValueFactory(cellData -> cellData.getValue().deviceInfoProperty());
        loginTimeColumn.setCellValueFactory(cellData -> cellData.getValue().loginTimeProperty());

        // Load login history data into the table
        loadLoginHistoryData();
    }

    private void loadLoginHistoryData() {
        ObservableList<LoginHistory> loginHistoryList = FXCollections.observableArrayList();
        loginHistoryList.addAll(loginHistoryService.getAll());
        loginHistoryTable.setItems(loginHistoryList);
    }
}
