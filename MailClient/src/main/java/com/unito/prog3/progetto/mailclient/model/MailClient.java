package com.unito.prog3.progetto.mailclient.model;

import com.unito.prog3.progetto.mailclient.controller.ClientGuiController;
import com.unito.prog3.progetto.model.Email;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class MailClient {
  private final int MAIL_SERVER_PORT = 6789;
  // JAVAFX UI COMPONENTS
  private final ClientGuiController controller;
  private final ObservableList<Email> inboxContent;
  private final SimpleListProperty<Email> inbox;
  // MY VARIABLES
  private final SimpleStringProperty emailAddress;
  // NETWORK VARIABLES
  private Socket socket;
  private InputStream in;
  private OutputStream out;

  public MailClient(String email, ClientGuiController controller) {
    this.inbox = new SimpleListProperty<>();
    this.inboxContent = FXCollections.observableArrayList(new LinkedList<>());
    this.inbox.set(inboxContent);
    this.emailAddress = new SimpleStringProperty(email);
    this.controller = controller;
  }

  public void connectToServer() {
     // TODO: gestire N tentativi, dopo segnala l'errore
    // tentativo di collegarsi al mail server
    try {
      socket = new Socket(InetAddress.getByName(null), MAIL_SERVER_PORT);
      System.out.println("connessione riuscita");
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      // comunico il mio nickname
      Email email=new Email(this.getEmailAddress());
      objectOutputStream.writeObject(email);
      objectOutputStream.flush(); //forza l'invio
      // objectOutputStream.close();
      //
      // ricevo la lista delle mail sotto forma di ArrayList
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      ArrayList<Email> inboxArrayList = (ArrayList<Email>) objectInputStream.readObject();
      inboxArrayList.forEach(System.out::println);
      inboxArrayList.forEach(this::addEmail);

      // avviso la GUI

    } catch (ConnectException ce) {
      System.out.println("Server offline");
    }
    catch (IOException e) {
      // qui se non riesco a collegarmi al server
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public String getEmailAddress() {
    return emailAddress.get();
  }

  public SimpleStringProperty emailAddressProperty() {
    return emailAddress;
  }

  public SimpleListProperty<Email> inboxProperty() {
    return inbox;
  }

  public void addEmail(Email email) {
    this.inboxContent.add(email);
  }
  public void sendEmailToServer() {
    System.out.println("INVIO...");
  }

  public void emptyInbox() {
    // usare con cautela
    this.inboxContent.clear();
  }

  public void deleteEmail(Email e) {
    this.inboxContent.remove(e);
  }
}
