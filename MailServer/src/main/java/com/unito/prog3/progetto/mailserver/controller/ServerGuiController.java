package com.unito.prog3.progetto.mailserver.controller;

import com.unito.prog3.progetto.mailserver.model.Client;
import com.unito.prog3.progetto.mailserver.model.MailServerService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the controller of server GUI
 */
public class ServerGuiController {
    private Client clientModel;
    private MailServerService service;
    @FXML
    private ListView<String> idListView;
    @FXML
    private TextArea logTextArea;

    /**
     * This method initializes the Server Controller.
     * @param clientModel: required to bind the client’s inbox to the GUI view
     * @param service: the MailServerService is the Model that will execute the standard services of the Server itself
     */
    @FXML
    public void initialize(Client clientModel, MailServerService service) {
        if (this.clientModel != null) {
            throw new IllegalStateException("Client is already defined");
        }
        this.clientModel = clientModel;
        this.service = service;
        idListView.itemsProperty().bind(clientModel.inboxProperty());
        // next line will call run() and start Server service
        this.service.start();
    }

    /**
     * This method allows to insert a new client connection in the log when it is accepted by the server
     */
    @FXML
    public void logNewConnection(String newConnection) {
        this.logTextArea.appendText("Connected: " + newConnection + "\n");
    }

    /**
     * This method allows to insert a new disconnected client connection in the log when it occurs
     */
    @FXML
    public void logLostConnection(String s) {
        this.logTextArea.appendText("Disconnected: " + s);
    }

    /**
     * This method allows to remove the email of a client in the Users list of the GUI
     */
    @FXML
    public void logLostClient(String lostConnection) {
        this.idListView.getItems().remove(lostConnection);
    }

    /**
     * This method allows to insert the email of a new client in the Users list of the GUI if it's not already present
     */
    @FXML
    public void logNewClient(String clientEmail) {
        if (!idListView.getItems().contains(clientEmail)) {
            this.idListView.getItems().add(clientEmail);
        }
    }

    /**
     * This method allows to insert any type of message in the GUI Log.
     * Message Type can be:
     * 1)New message from .. to ..
     * 2)New message to .. from ..
     * 3)New message from .. to .. FAILED
     */
    @FXML
    public void logMessageSend(String message) {
        this.logTextArea.appendText(message);
    }
}