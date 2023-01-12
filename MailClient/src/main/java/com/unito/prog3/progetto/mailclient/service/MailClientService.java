package com.unito.prog3.progetto.mailclient.service;

import com.unito.prog3.progetto.mailclient.controller.ClientController;
import com.unito.prog3.progetto.model.Constants;
import com.unito.prog3.progetto.model.Email;
import com.unito.prog3.progetto.model.Message;
import javafx.application.Platform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the client service that communicate with server
 */
public class MailClientService {
  private final ClientController mailClientController;  //controller
  private boolean isServiceOn = true;
  private boolean retrieveInboxFirsTry = false;

  /**
   * @param mailClientController: the controller of the project model
   */
  public MailClientService(ClientController mailClientController) {
    this.mailClientController = mailClientController;
  }

  /**
   * @param message: the message to send to server
   * Opens a connection with the server
   */
  public void openConnection(Message message) {
    newService(message, true);
    retrieveInboxFirsTry = true;
  }

  /**
   * Retrieves all emails and notifies controller to push them if not present
   */
  private void readAndWriteAllEmails(Socket socket) throws IOException, ClassNotFoundException {
    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
    // read all emails...
    ArrayList<Email> emails = (ArrayList<Email>) objectInputStream.readObject();
    for (Email e : emails) {
      // ...and write if not present
      Platform.runLater(()->{
        mailClientController.pushIfNotPresent(e);
      });
    }
  }

  /**
   * @param message: the message to send to server
   * Sends periodical requests to server to retrieve new emails
   */
  public void periodicalEmailsRetrieve(Message message) {
    new Thread(() -> {
      while (this.isServiceOn) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if(!retrieveInboxFirsTry) {
          Message messageAssist = new Message();
          messageAssist.setHeader(Constants.CONNECTION_REQUEST);
          messageAssist.setEmail(new Email(message.getEmail().getSender()));
          openConnection(messageAssist);
        }
        else {
          newService(message, true);
        }
      }
    }).start();
  }

  /**
   * @param flag: the condition to verify in the method
   * Calls controller method 'notifyServerDown' with parameter flag and if flag is false calls method 'resetAlert'
   */
  public void disableGuiCta(boolean flag) {
    Platform.runLater(() -> {
      this.mailClientController.notifyServerDown(flag);
      if (!flag) {
        this.mailClientController.resetAlert();
      }
    });
  }

  /**
   * @param message: the message to send to server
   * Turns client service down and launches new thread
   */
  public void notifyClientDisconnect(Message message) {
    this.isServiceOn = false;
    newService(message, false);
  }

  /**
   * @param message: the message to send to server
   * Launches new thread
   */
  public void sendMessage(Message message) {
    newService(message, false);
  }
  /**
   * @param message: the message to send to server
   * Launches new thread
   */
  public void deleteMessage(Message message) {
    newService(message, false);
  }
  /**
   * @param message: the message to send to server
   * Launches new thread
   */
  public void seenMail(Message message) {
    newService(message, false);
  }

  public void newService(Message message, boolean readAndWrite) {
    Socket socket = null;
    try {
      // open connection with server...
      socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
      disableGuiCta(false);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      // ...then send message
      objectOutputStream.writeObject(message);
      objectOutputStream.flush();
      // if requested retrieve all emails
      if(readAndWrite) {
        readAndWriteAllEmails(socket);
      }
    } catch (ConnectException connectException) {
      disableGuiCta(true);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try{
        if(socket != null) {
          socket.close();
        }
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
  }

}
