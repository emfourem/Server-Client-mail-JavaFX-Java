package com.project.emailclient.model;

import com.project.emailclient.controller.HelloController;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class MailClient {
  private final int MAIL_SERVER_PORT = 6789;
  // JAVAFX UI COMPONENTS
  // CONTROLLER
  private final HelloController controller;
  private final ObservableList<String> inboxContent;
  private final SimpleListProperty<String> inbox;
  // MY VARIABLES
  private final SimpleStringProperty email;
  // NETWORK VARIABLES
  private Socket socket;
  private InputStream in;
  private OutputStream out;

  public MailClient(String email, HelloController controller) {
    this.inbox = new SimpleListProperty<>();
    this.inboxContent = FXCollections.observableArrayList(new LinkedList<>());
    this.inbox.set(inboxContent);
    this.email = new SimpleStringProperty(email);
    this.controller = controller;
  }

  public void connectToServer() {
     // TODO: gestire N tentativi, dopo seganal l'errore
    // tentativo di collegarsi al mail server
    try {
      socket = new Socket(InetAddress.getByName(null), MAIL_SERVER_PORT);
      System.out.println("connessione riuscita");
      // comunico il mio nickname
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      objectOutputStream.writeObject(this.getEmail());
      objectOutputStream.flush();
      // objectOutputStream.close();
      //
      // ricevo la lista delle mail sotto forma di ArrayList
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      ArrayList<String> inboxArrayList = (ArrayList<String>) objectInputStream.readObject();
      inboxArrayList.forEach(System.out::println);
      // avviso la GUI

    } catch (IOException e) {
      // qui se non riesco a collegarmi al server
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public String getEmail() {
    return email.get();
  }

  public SimpleStringProperty emailProperty() {
    return email;
  }

  public SimpleListProperty<String> inboxProperty() {
    return inbox;
  }

  public void addEmail(String email) {
    this.inboxContent.add(email);
  }
  public void sendEmailToServer() {
    System.out.println("INVIO...");
  }
}
