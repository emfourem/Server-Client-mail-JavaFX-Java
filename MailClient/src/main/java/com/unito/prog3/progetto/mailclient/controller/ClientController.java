package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.mailclient.service.MailClientService;
import com.unito.prog3.progetto.model.*;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
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
    this.periodicalEmailRequest();
  }

  public void setGuiController(ClientGuiController guiController) {
    this.guiController = guiController;
  }

  public void shutdownMailClientService() {
    if (executorService != null) {
      try {
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
  private void checkContains(Email email) {
    if (mailbox.contains(email)) {
      if (email.getStato().equalsIgnoreCase(EmailStateEnum.NEW_EMAIL.toString())) {
        Platform.runLater(() -> {
          this.guiController.alertNewMessage(email.getSender());
        });
        email.setStato(EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString());
        // notifico il server
        Email seen = new Email(this.mailbox.getEmailAddress());
        seen.setId(email.getId());
        seenMail(seen, EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString());
      }
    }
  }

  public synchronized void pushIfNotPresent(Email email) {
    checkContains(email);
    this.mailbox.addEmailIfNotPresent(email);
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

  public void deleteEmail(Email email) {
    System.out.println("Voglio eliminare: " + email);
    Message message = new Message();
    Email e = new Email();
    e.setId(email.getId());
    e.setSender(mailbox.getEmailAddress());
    message.setHeader(ServiceHeaders.DELETE_EMAIL_BY_ID.toString());
    message.setEmail(e);
    service.deleteMessage(message);
  }

  public void openConnection() {
    Message message = new Message();
    message.setHeader(ServiceHeaders.CONNECTION_REQUEST.toString());
    message.setEmail(new Email(mailbox.getEmailAddress()));
    service.openConnection(message);
  }

  public void periodicalEmailRequest() {
    Message message = new Message();
    message.setHeader(ServiceHeaders.REQUEST_NEW_EMAILS.toString());
    message.setEmail(new Email(mailbox.getEmailAddress()));
    service.periodicalEmailsRetrieve(message);
  }

  public void notifyServerDown(boolean flag) {
    if (flag) {
      this.guiController.disableControls(true);
      this.guiController.alertInformation();
    } else {
      this.guiController.disableDashboardCta(false);
    }
  }

  public void resetAlert() {
    this.guiController.resetAlert();
  }

  public void seenMail(Email email, String header) {
    Message message = new Message();
    message.setHeader(header);
    message.setEmail(email);
    service.seenMail(message);
  }
}
