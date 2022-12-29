package com.unito.prog3.progetto.mailclient.service;

import com.unito.prog3.progetto.mailclient.controller.ClientController;
import com.unito.prog3.progetto.model.Constants;
import com.unito.prog3.progetto.model.Email;
import com.unito.prog3.progetto.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class MailClientService {
  private ClientController mailClientController;
  private Socket socket;

  public MailClientService(ClientController mailClientController) {
    this.mailClientController = mailClientController;
    this.socket = null;
    // hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shutdown hook");
    }));
  }

  public void sendMessage(Message message) {
    // avvio un thread
    new Thread(() -> {
      try {
        socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
        // qua la socket e' aperta
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void openConnection(Message message) {
    // thread che avvisa il server e recupera i dati
    // apro la socket
    // recupero i messaggi
    new Thread(() -> {
      try {
        socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
        // qua la socket c'e'
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
        // dopo aver notificato...
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ArrayList<Email> emails = (ArrayList<Email>) objectInputStream.readObject();
        for (Email e: emails) {
          System.out.println(e);
          mailClientController.pushMail(e);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void notifyClientDisconnect(Message message) {
    new Thread(() -> {
      try {
        socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  /*
  protected class MailInWorker extends Thread {
    @Override
    public synchronized void run() {
      System.out.println("Sono il listener della posta in entrata..." + currentThread().getName());
      try {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ArrayList<Email> emailArrayList = (ArrayList<Email>) objectInputStream.readObject();
        for (Email email : emailArrayList) {
          mailClientController.pushMail(email);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  protected class MailOutWorker extends Thread {

    @Override
    public synchronized void run() {
      System.out.println("Sono il listener della posta in uscita..." + currentThread().getName());
      try {
        ObjectOutputStream objectInputStream = new ObjectOutputStream(socket.getOutputStream());
        Email noEmail = new Email(-1, "boris.turcan@gmail.com", null, "", "", new Date());
        objectInputStream.writeObject(noEmail);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  */
}
