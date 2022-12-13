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
        
        ArrayList<String> l = new ArrayList<>();
        l.add("Email1");
        l.add("Email2");
        l.add("Email3");

        // invio la lista
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        // objectOutputStream.flush();
        objectOutputStream.writeObject(l);
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
}
