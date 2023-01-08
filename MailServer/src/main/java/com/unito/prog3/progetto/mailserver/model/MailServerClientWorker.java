package com.unito.prog3.progetto.mailserver.model;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;
import com.unito.prog3.progetto.mailserver.service.MyFileWriterService;
import com.unito.prog3.progetto.model.Email;
import com.unito.prog3.progetto.model.EmailStateEnum;
import com.unito.prog3.progetto.model.Message;
import com.unito.prog3.progetto.model.ServiceHeaders;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailServerClientWorker extends Thread {
  private Socket socket;
  private ServerGuiController guiController;
  private String emailSender;
  private ArrayList<Email> inbox;
  public MailServerClientWorker(Socket socket, ServerGuiController guiController) {
    this.socket = socket;
    this.guiController = guiController;
    this.inbox = new ArrayList<>();
  }


  @Override
  public void run() {
    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      Message message = (Message) objectInputStream.readObject();
      String header = message.getHeader();
      if (header.equalsIgnoreCase(ServiceHeaders.CONNECTION_REQUEST.toString())) {
        this.emailSender = message.getEmail().getSender();
        MyFileWriterService.createMailbox(message.getEmail());
        retrieveThenSendEmails(message.getEmail(), false);
        Platform.runLater(() -> {
          this.guiController.logNewConnection(socket.toString());
          this.guiController.logNewClient(this.emailSender);
        });
      } else if (header.equalsIgnoreCase(ServiceHeaders.CONNECTION_CLOSED.toString())) {
        Platform.runLater(() -> {
          this.guiController.logLostConnection(socket.toString() + "\n");
          this.guiController.logLostClient(message.getEmail().getSender() + "\n");
        });
      } else if (header.equalsIgnoreCase(EmailStateEnum.NEW_EMAIL.toString())) {
        receiveMailThenStore(message.getEmail());
      } else if (header.equalsIgnoreCase(ServiceHeaders.DELETE_EMAIL_BY_ID.toString())) {
        MyFileWriterService.deleteEmail(message.getEmail());
      } else if (header.equalsIgnoreCase(ServiceHeaders.REQUEST_NEW_EMAILS.toString())) {
        this.emailSender = message.getEmail().getSender();
        retrieveThenSendEmails(message.getEmail(), true);
        Platform.runLater(() -> {
          this.guiController.logNewClient(this.emailSender);
        });
      } else if (header.equalsIgnoreCase(ServiceHeaders.REQUEST_MARK_EMAIL_AS_SEEN.toString())) {
        this.emailSender = message.getEmail().getSender();
        MyFileWriterService.markAs(message.getEmail(), ServiceHeaders.REQUEST_MARK_EMAIL_AS_SEEN.toString());
      } else if (header.equalsIgnoreCase(EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString())) {
        MyFileWriterService.markAs(message.getEmail(), EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString());
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void receiveMailThenStore(Email email) throws IOException, ClassNotFoundException {
    String dest = "";
    for (String s : email.getReceivers()) {
      dest += " [ " + s + "] ";
    }
    String finalDest = dest;
    Platform.runLater(() -> {
      this.guiController.logMessageSend("New message from [" + email.getSender() + "] to " + finalDest + "\n");
    });
    ArrayList<String> notFound = MyFileWriterService.writeEmail(email);
    if (notFound.size() > 0) {
      writeNoReply(email.getSender(), notFound);
    }
    for (String s : email.getReceivers()) {
      if(!notFound.contains(s)) {
        Platform.runLater(() -> {
          this.guiController.logMessageSend("New message to [" + s + "] from [" + email.getSender() + "]\n");
        });
      }
    }
  }

  private void writeNoReply(String dest, ArrayList<String> notFound) throws IOException, ClassNotFoundException {
    Email noReply = new Email("no_reply.progetto.prog3@server.it");
    noReply.setReceivers(List.of(dest));
    noReply.setObject("noReply: Receivers not found");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Dear Sender,\n\tThe following list contains not found receivers of your message:\n");
    for (String s : notFound) {
      stringBuilder.append("\t").append(s).append("\n");
      Platform.runLater(() -> {
        this.guiController.logMessageSend("Message from [" + dest + "] to [" + s + "] FAILED [ user not found ]\n");
      });
    }
    stringBuilder.append("Cordially, \nTeam MMT\n");
    noReply.setText(stringBuilder.toString());
    noReply.setId(System.currentTimeMillis());
    noReply.setStato(EmailStateEnum.NEW_EMAIL.toString());
    noReply.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date()));
    MyFileWriterService.writeEmail(noReply);
  }

  private void retrieveThenSendEmails(Email email, boolean needFilter) throws IOException, ClassNotFoundException {
    ArrayList<Email> allEmails = MyFileWriterService.retrieveMails(email);
    // filtro i messaggi
    if (needFilter) {
      for (Email e : allEmails) {
        if (e.getStato().equalsIgnoreCase(EmailStateEnum.NEW_EMAIL.toString())) {
          inbox.add(e);
        }
      }
    } else {
      inbox = allEmails;
    }
    sendEmails();
  }

  private void sendEmails() throws IOException {
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    objectOutputStream.writeObject(inbox);
    objectOutputStream.flush();
  }
}
