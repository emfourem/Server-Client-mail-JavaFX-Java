package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.mailclient.service.MailClientService;
import com.unito.prog3.progetto.model.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientController {
  private ClientMailbox mailbox;
  private MailClientService service;
  private ClientGuiController guiController;
  private ExecutorService executorService;

  public ClientController() {
    this.mailbox = new ClientMailbox(Constants.MY_EMAIL_ADDRESS);
    this.service = new MailClientService(this);
  }

  public void startService() {
    this.openConnection();
  }

  public void setGuiController(ClientGuiController guiController) {
    this.guiController = guiController;
  }

  public void shutdownMailClientService() {
    if (executorService != null) {
      try {
        // attendo 15 secondi, se il thread del client non termina, allora forzo la terminazione
        if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow(); // Cancel currently executing tasks
          if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS))
            System.err.println("Pool did not terminate");
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }

  public ClientMailbox getMailbox() {
    return mailbox;
  }

  public synchronized void pushMail(Email email) {
    this.mailbox.addEmail(email);
  }

  public synchronized void deleteMail(Email email) {
    this.mailbox.deleteEmail(email);
  }

  public void guiIsClosing() {
    System.out.println("GUI is closing...");
    this.shutdownMailClientService();
    Message message = new Message();
    message.setEmail(new Email(mailbox.getEmailAddress()));
    message.setHeader(ServiceHeaders.CONNECTION_CLOSED.toString());
    this.service.notifyClientDisconnect(message);
  }

  public void sendEmail(Email email) {
    System.out.println("Voglio inviare: " + email);
    Message message = new Message();
    message.setHeader(EmailStateEnum.NEW_EMAIL.toString());
    message.setEmail(email);
    service.sendMessage(message);
  }

  public void openConnection() {
    Message message = new Message();
    message.setHeader(ServiceHeaders.CONNECTION_REQUEST.toString());
    message.setEmail(new Email(mailbox.getEmailAddress()));
    service.openConnection(message);
  }
}
