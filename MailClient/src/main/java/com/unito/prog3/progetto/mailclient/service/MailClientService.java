package com.unito.prog3.progetto.mailclient.service;

import com.unito.prog3.progetto.mailclient.controller.ClientController;
import com.unito.prog3.progetto.externmodel.Constants;
import com.unito.prog3.progetto.externmodel.Email;
import com.unito.prog3.progetto.externmodel.Message;
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
  private ClientController mailClientController;
  private Socket socket;
  //true only when client is active, false otherwise
  private boolean isServiceOn = true;
  private boolean retrieveInboxFirsTry = false;
  private boolean serverUp = false;

  /**
   * @param mailClientController: the controller of the project model
   */
  public MailClientService(ClientController mailClientController) {
    this.mailClientController = mailClientController;
    this.socket = null;
  }

  /**
   * Gets the server status
   * @return true if server is up, false otherwise
   */
  public boolean getServerStatus(){
    return this.serverUp;
  }

  /**
   * @param message: the message to send to server
   * Opens a connection with the server
   */
  public void openConnection(Message message) {
    try {
      // open connection with server...
      this.socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
      this.serverUp = true;
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      // ...then send message...
      objectOutputStream.writeObject(message);
      objectOutputStream.flush();
      // ...and retrieves all client emails
      readAndWriteEmails();
      objectOutputStream.close();
      this.retrieveInboxFirsTry = true;
    } catch (ConnectException connectException) { // if server is down
      disableGuiCta(true);
      this.serverUp = false;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        if(socket != null) {
          socket.close();
        }
      } catch (IOException ignored) {
      }
    }
  }

  /**
   * @param message: the message to send to server
   * Turns client service down and calls method to send the message
   */
  public void notifyClientDisconnect(Message message) {
    this.isServiceOn = false;
    sendMessageService(message);
  }

  /**
   * Retrieves all emails and notifies controller to push them if not present
   */
  private void readAndWriteEmails() throws IOException, ClassNotFoundException {
    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
    ArrayList<Email> emails = new ArrayList<>();
    // read all emails...
    Object o = objectInputStream.readObject();
    if(o != null && o.getClass() == emails.getClass()) emails = (ArrayList<Email>) o;
    for (Email e : emails) {
      // ...and write if not present
      Platform.runLater(() -> mailClientController.pushIfNotPresent(e));
    }
    objectInputStream.close();
  }

  /**
   * @param message: the message to send to server
   * Creates new thread that sends periodical requests to server to retrieve new emails
   */
  public void periodicalEmailsRetrieve(Message message) {
    receivingThread thread = new receivingThread(message);
    thread.start();
  }

  /**
   * Invoked when socket connection request failed
   * Notifies controller to disable GUI buttons
   * @param flag: boolean value used in method to set disable values
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
   * Creates new thread and opens connection with server to send it the message
   * @param message: the message to send to server
   */
  public void sendMessageService(Message message) {
    new Thread(() -> {
      Socket newSocket = null;
      try {
        newSocket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(newSocket.getOutputStream());
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
        objectOutputStream.close();
      } catch (ConnectException connectException) {
        disableGuiCta(true);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if(newSocket != null) {
            newSocket.close();
          }
        } catch (IOException ignored) {
        }
      }
    }).start();
  }

  /**
   * This inner class represents new thread that do periodical emails request to server
   */
  class receivingThread extends Thread {

    private Message message;

    /**
     * @param message: the message to send to server
     */
    public receivingThread(Message message) {
      this.message = message;
    }

    @Override
    public void run() {
      while (isServiceOn) {
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
          if(retrieveInboxFirsTry){
            disableGuiCta(false);
          }
        }
        else {
          try {
            // open connection with server...
            socket = new Socket(InetAddress.getByName(null), Constants.MAIL_SERVER_PORT);
            serverUp = true;
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // ...send request...
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            // ...retrieves all emails...
            readAndWriteEmails();
            objectOutputStream.close();
          } catch (ConnectException connectException) {
            disableGuiCta(true);
            // to update client and server log if server goes down again
            retrieveInboxFirsTry = false;
            serverUp = false;
          } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
          } finally {
            try {
              if(socket != null) {
                socket.close();
              }
            } catch (IOException ignored) {
            }
          }
        }
      }
    }
  }
}
