package com.unito.prog3.progetto.mailclient.service;

import com.unito.prog3.progetto.mailclient.controller.ClientController;
import com.unito.prog3.progetto.model.Constants;
import com.unito.prog3.progetto.model.Email;
import com.unito.prog3.progetto.model.Message;
import com.unito.prog3.progetto.model.ServiceHeaders;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class MailClientService {
  private ClientController mailClientController;
  private Socket socket;
  private boolean isServiceOn = true;
  private boolean retrieveInboxFirsTry = false;

  public MailClientService(ClientController mailClientController) {
    this.mailClientController = mailClientController;
    this.socket = null;
    // hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shutdown hook");
    }));
  }

  public void toggleService() {
    this.isServiceOn = false;
  }

  public void sendMessage(Message message) {
    // avvio un thread
    launchNewThread(message);
  }

  public void deleteMessage(Message message) {
    launchNewThread(message);
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
        readAndWriteAllEmails();
        retrieveInboxFirsTry = true;
      } catch (ConnectException connectException) {
        disabilitaGuiCta(true);
        Platform.runLater(() -> {
          // dico alla gui di mostrare un messaggio di errore, ovvero server DOWN
        });
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private void readAndWriteAllEmails() throws IOException, ClassNotFoundException {
    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
    ArrayList<Email> emails = (ArrayList<Email>) objectInputStream.readObject();
    for (Email e : emails) {
      Platform.runLater(() -> {
        mailClientController.pushIfNotPresent(e);
      });
    }
  }

  public void notifyClientDisconnect(Message message) {
    this.toggleService();
    launchNewThread(message);
  }

  public void periodicalEmailsRetrieve(Message message) {
    new Thread(() -> {
      while (this.isServiceOn) {
        try {
          Thread.sleep(1500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        //
        if(!retrieveInboxFirsTry) {
          Message messageAssist = new Message();
          messageAssist.setHeader(ServiceHeaders.CONNECTION_REQUEST.toString());
          messageAssist.setEmail(new Email(message.getEmail().getSender()));
          openConnection(messageAssist);
        }
        else {
          try {
            socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
            disabilitaGuiCta(false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // invio la notifica
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            // dopo aver notificato il server...
            readAndWriteAllEmails();
          } catch (ConnectException connectException) {
            disabilitaGuiCta(true);
          } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  public void disabilitaGuiCta(boolean flag) {
    Platform.runLater(() -> {
      this.mailClientController.notifyServerDown(flag);
      if (!flag) {
        this.mailClientController.resetAlert();
      }
    });
  }

  public void seenMail(Message message) {
    launchNewThread(message);
  }

  private void launchNewThread(Message message) {
    new Thread(() -> {
      try {
        socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
        // qua la socket e' aperta
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
      } catch (ConnectException connectException) {
        disabilitaGuiCta(true);
        Platform.runLater(() -> {
          // dico alla gui di mostrare un messaggio di errore, ovvero server DOWN
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }
}
