package com.unito.prog3.progetto.mailserver.model;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;
import com.unito.prog3.progetto.mailserver.service.MyFileWriterService;
import com.unito.prog3.progetto.model.Constants;
import com.unito.prog3.progetto.model.Email;
import com.unito.prog3.progetto.model.Message;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the server worker that manages a client reqeust
 */
public class MailServerClientWorker implements Runnable {
  private final Socket socket;
  private final ServerGuiController guiController;
  private String emailSender;
  private ArrayList<Email> inbox;

  /**
   * The constructor of the worker
   * @param socket: socket used to communicate with client
   * @param guiController: the controller of server GUI
   */
  public MailServerClientWorker(Socket socket, ServerGuiController guiController) {
    this.socket = socket;
    this.guiController = guiController;
    this.inbox = new ArrayList<>();
  }

  @Override
  public void run() {
    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      // reads request on inputStream and behaves accordingly
      Message message = (Message) objectInputStream.readObject();
      String header = message.getHeader();
      this.emailSender = message.getEmail().getSender();
      if (Constants.CONNECTION_REQUEST.equalsIgnoreCase(header)) {
        Platform.runLater(() -> {
          this.guiController.logNewConnection(socket.toString());
          this.guiController.logNewClient(this.emailSender);
        });
        // mailbox will be created only at first client connection request
        MyFileWriterService.createMailbox(emailSender);
        // makes available all messages received on client GUI send them to him
        // needFilter will be true only when request is not connection_request,
        // because in this case all emails must be sent, otherwise only the new ones.
        retrieveThenSendEmails(emailSender, false);
      } else if (Constants.CONNECTION_CLOSED.equalsIgnoreCase(header)) {
        Platform.runLater(() -> {
          this.guiController.logLostConnection(socket.toString());
          this.guiController.logLostClient(emailSender);
        });
      } else if (Constants.NEW_EMAIL.equalsIgnoreCase(header)) {
        receiveMailThenStore(message.getEmail());
      } else if (Constants.DELETE_EMAIL_BY_ID.equalsIgnoreCase(header)) {
        MyFileWriterService.deleteEmail(message.getEmail());
      } else if (Constants.REQUEST_NEW_EMAILS.equalsIgnoreCase(header)) {
        retrieveThenSendEmails(emailSender, true);
      } else if (Constants.REQUEST_MARK_EMAIL_AS_SEEN.equalsIgnoreCase(header)) {
        MyFileWriterService.markAs(message.getEmail(), Constants.REQUEST_MARK_EMAIL_AS_SEEN);
      } else if (Constants.EMAIL_RECEIVED_NOT_SEEN.equalsIgnoreCase(header)) {
        MyFileWriterService.markAs(message.getEmail(), Constants.EMAIL_RECEIVED_NOT_SEEN);
      }
      System.out.println("Close: " + socket);
      objectInputStream.close();
      socket.close();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Stores received email on file
   * @param email: the email to store
   */
  private void receiveMailThenStore(Email email) throws IOException, ClassNotFoundException {
    StringBuilder dest = new StringBuilder();
    for (String s : email.getReceivers()) {
      dest.append(" [").append(s).append("] ");
    }
    String finalDest = dest.toString();
    Platform.runLater(() -> this.guiController.logMessageSend("New message from [" + email.getSender() + "] to " + finalDest + "\n"));
    // notFound will contain not found receivers
    ArrayList<String> notFound = MyFileWriterService.writeEmail(email);
    if (notFound.size() > 0) {
      writeNoReply(email.getSender(), notFound);
    }
    for (String s : email.getReceivers()) {
      if(!notFound.contains(s)) {
        Platform.runLater(() -> this.guiController.logMessageSend("New message to [" + s + "] from [" + email.getSender() + "]\n"));
      }
    }
  }

  /**
   * Sends a no-reply answer to client for each not found receiver
   * @param dest: the client email that made a request
   * @param notFound: the email addresses not found in the server directory
   */
  private void writeNoReply(String dest, ArrayList<String> notFound) throws IOException, ClassNotFoundException {
    Email noReply = new Email("no_reply.progetto.prog3@server.it");
    noReply.setReceivers(List.of(dest));
    noReply.setObject("noReply: Receivers not found");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Dear Sender,\n\tThe following list contains not found receivers of your message:\n");
    for (String s : notFound) {
      stringBuilder.append("\t").append(s).append("\n");
      Platform.runLater(() -> this.guiController.logMessageSend("Message from [" + dest + "] to [" + s + "] FAILED [ user not found ]\n"));
    }
    stringBuilder.append("Cordially, \nTeam MMT\n");
    noReply.setText(stringBuilder.toString());
    noReply.setId(System.currentTimeMillis());
    noReply.setState(Constants.NEW_EMAIL);
    noReply.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date()));
    MyFileWriterService.writeEmail(noReply);
  }

  /**
   * Reads received emails from file and send them as an ArrayList of Emails
   * @param emailSender: email of client that requests new emails
   * @param needFilter: boolean value used to check when filter messages
   */
  private void retrieveThenSendEmails(String emailSender, boolean needFilter) throws IOException, ClassNotFoundException {
    ArrayList<Email> allEmails = MyFileWriterService.retrieveMails(emailSender);
    // filters messages only when connection is already established and client requests only new messages
    if (needFilter) {
      for (Email e : allEmails) {
        if (Constants.NEW_EMAIL.equalsIgnoreCase(e.getState())) {
          inbox.add(e);
        }
      }
    } else {
      inbox = allEmails;
    }
    sendEmails();
  }

  /**
   * Write inbox on socket output stream
   */
  private void sendEmails() throws IOException {
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    objectOutputStream.writeObject(inbox);
    objectOutputStream.flush();
    // flush guarantees that data will be available to client, so then the socket is closed
  }
}
