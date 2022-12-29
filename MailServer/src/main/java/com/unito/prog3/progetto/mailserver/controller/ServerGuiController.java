package com.unito.prog3.progetto.mailserver.controller;

import com.unito.prog3.progetto.mailserver.model.Client;
import com.unito.prog3.progetto.mailserver.model.MailServerService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ServerGuiController {
    private Client clientModel;
    private String clientEmail;
    private MailServerService service;

    @FXML
    private ListView<String> idListView;

    @FXML
    private TextArea logTextArea;


    @FXML
    public void initialize(Client clientModel, MailServerService service) {
        System.out.println("MY INIT");
        if (this.clientModel != null) {
            throw new IllegalStateException("client model e' gia' inizializzato");
        }
        this.clientModel = clientModel;
        this.service = service;
        idListView.itemsProperty().bind(clientModel.inboxProperty());
        service.start();
    }

    @FXML
    public void logNewConnection(String newConnection) {
        this.logTextArea.appendText(newConnection + "\n");
    }

    @FXML
    public void logLostClient(String lostConnection) {
        this.idListView.getItems().remove(lostConnection);
    }

    @FXML
    public void logLostConnection(String s) {
        this.logTextArea.appendText("Disconnected: " + s);
    }

    @FXML
    public void logNewClient(String clientEmail) {
        if (!idListView.getItems().contains(clientEmail)) {
            this.idListView.getItems().add(clientEmail);
        }
    }

    @FXML
    public void logMessageSend(String message) {
        this.logTextArea.appendText(message);
    }
}