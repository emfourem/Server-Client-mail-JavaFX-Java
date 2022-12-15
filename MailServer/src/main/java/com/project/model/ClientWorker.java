package com.project.model;

import com.project.view.ServerController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

//socket associata al singolo client
public class ClientWorker implements Runnable{
    private Socket socket;
    private ServerController guiController;

    private ArrayList<Email> inbox;

    public ClientWorker(Socket socket, ServerController guiController) {
        this.socket = socket;
        this.guiController=guiController;
        this.inbox=new ArrayList<>();
    }




    @Override
    public void run() {
        //mandare messaggi al client
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Email email = (Email) objectInputStream.readObject();
            Platform.runLater(() -> {
                this.guiController.logNewConnection(socket.toString());
                this.guiController.logNewClient("NUOVO CLIENT: " + email.getSender());
            });
            dummyEmails();
            objectOutputStream.writeObject(inbox);
            objectOutputStream.flush();
            while(objectInputStream.read()!=-1){

            }
            this.guiController.logLostConnection("Disconnected: "+ socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void dummyEmails() {
        Email e1 = new Email(1, "mario.rossi@gmail.com", Arrays.asList("luca.verdi@uni.it", "marco.marroni@unito.it", "marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
        Email e2 = new Email(2, "alberto.marino@gmail.com", Arrays.asList("luca.verdi@uni.it", "marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
        Email e3 = new Email(3, "antonio.pesce@gmail.com", Arrays.asList("marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
        inbox.add(e1);
        inbox.add(e2);
        inbox.add(e3);
    }
}
