package com.project.model;

import com.project.view.ServerController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MailService extends Thread{
  private int port;
  private Client c;
  private ServerController guiController;

  public MailService(int port, ServerController guiController) {
    this.port = port;
    this.c = c;
    this.guiController = guiController;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public Client getC() {
    return c;
  }

  public void setC(Client c) {
    this.c = c;
  }

  @Override
  public void run() {
    System.out.println("Server locahols in port " + this.port + "... \n");
    this.service();
  }

  public void service() {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      while (true) {
        Socket client = serverSocket.accept();
        // recupero il nickname del client
        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
        String email = (String) objectInputStream.readObject();
        // objectInputStream.close();
        Platform.runLater(() -> {
          this.guiController.logNewConnection(client.toString());
          this.guiController.logNewClient("NUOVO CLIENT: " + email);
        });
        
        // mock: invio email di prova

        // invio la lista
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        // objectOutputStream.flush();
        ArrayList<Email> emails = dummyEmails();
        objectOutputStream.writeObject(emails);
        objectOutputStream.flush();
        // objectOutputStream.close();
        
        // client.close();
      }
      /*
      Platform.runLater(() -> {
        c.addEmail(email);
      });*/
      // aggiorno la lista
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public ArrayList<Email> dummyEmails() {
    ArrayList<Email> emails = new ArrayList<>();
    Email e1 = new Email(1, "mario.rossi@gmail.com", Arrays.asList("luca.verdi@uni.it", "marco.marroni@unito.it", "marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
    Email e2 = new Email(2, "alberto.marino@gmail.com", Arrays.asList("luca.verdi@uni.it", "marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
    Email e3 = new Email(3, "antonio.pesce@gmail.com", Arrays.asList("marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());

    emails.add(e1);
    emails.add(e2);
    emails.add(e3);

    return emails;
  }
}
