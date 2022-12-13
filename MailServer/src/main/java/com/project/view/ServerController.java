package com.project.view;

import com.project.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ServerController {
    private Client clientModel;
    private String clientEmail;

    @FXML
    private ListView<String> idListView;

    @FXML
    private TextArea logTextArea;


    @FXML
    public void initialize(Client clientModel) {
        System.out.println("MY INIT");
        if (this.clientModel != null) {
            throw new IllegalStateException("client model e' gia' inizializzato");
        }
        this.clientModel = clientModel;
        clientModel.addEmail("PIPPPPOOOOO");
        clientModel.addEmail("PIPPPPOOOOO");
        clientModel.addEmail("PIPPPPOOOOO");
        idListView.itemsProperty().bind(clientModel.inboxProperty());
    }

    @FXML
    public void logNewConnection(String newConnection) {
        this.logTextArea.appendText(newConnection + "\n");
    }

    @FXML
    public void logNewClient(String clientEmail) {
        this.clientModel.addEmail(clientEmail);
    }
}