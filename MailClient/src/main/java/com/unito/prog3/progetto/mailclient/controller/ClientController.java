package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.mailclient.service.MailClientService;
import com.unito.prog3.progetto.externmodel.*;
import javafx.application.Platform;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the intermediary between GUI controller, model and service
 * It receives input from GUI controller and notifies the model to update or
 * instructs the MailClientService to send requests to server
 */

public class ClientController {

  /**
   * Model, controller and service
   */
  private ClientMailbox mailbox;
  private ClientGuiController guiController;
  private MailClientService service;


  public ClientController() {
    this.mailbox = new ClientMailbox(Constants.MY_EMAIL_ADDRESS);
    this.service = new MailClientService(this);
  }

  public void setGuiController(ClientGuiController guiController) {
    this.guiController = guiController;
  }

  public ClientMailbox getMailbox() {
    return mailbox;
  }

  /**
   * Opens connection with server and send periodical email retrieve requests
   */
  public void startService() {
    this.openConnection(); //instructs service to open connection with server
    this.periodicalEmailRequest(); //instructs service to request periodically emails
  }

  /**
   * Notify service to send open connection request to server
   */
  public void openConnection() {
    Message message = new Message();
    message.setHeader(Constants.CONNECTION_REQUEST);
    message.setEmail(new Email(mailbox.getEmailAddress()));
    service.openConnection(message);
  }

  /**
   * Notify service to send periodical request to server
   */
  public void periodicalEmailRequest() {
    Message message = new Message();
    message.setHeader(Constants.REQUEST_NEW_EMAILS);
    message.setEmail(new Email(mailbox.getEmailAddress()));
    service.periodicalEmailsRetrieve(message);
  }
  /**
   * @param email: the email to be checked
   * Platform.runLater is used in non-GUI thread to update a GUI component:
   * the event is inserted in a queue of GUI thread and will be handled by it as soon as possible
   */
  private void checkContains(Email email) {
    // if email is present...
    if (mailbox.contains(email)) {
      // ...and its state is 'new'...
      if (Constants.NEW_EMAIL.equalsIgnoreCase(email.getState())) {
        // ...notify GUI controller to show alert message...
        Platform.runLater(() -> this.guiController.alertNewMessage(email.getSender()));
        // ...then change state to 'received not seen'...
        Email seen = new Email(this.mailbox.getEmailAddress());
        seen.setId(email.getId());
        // ...and notify server
        seenMail(seen, Constants.EMAIL_RECEIVED_NOT_SEEN);
        email.setState(Constants.EMAIL_RECEIVED_NOT_SEEN);
      }
    }
  }

  /**
   * @param email: the message email
   * @param header: the message header
   * Notify service to send request to server
   */
  public void seenMail(Email email, String header) {
    Message message = new Message();
    message.setHeader(header);
    message.setEmail(email);
    service.sendMessageService(message);
  }

  /**
   * @param email: the email to check
   * Adds the email to mailbox if not present
   */
  public void pushIfNotPresent(Email email) {
    this.mailbox.addEmailIfNotPresent(email);
    checkContains(email);
  }

  /**
   * Notify client service to shut down and send notification to server
   */
  public void guiIsClosing() {
    System.out.println("GUI is closing...");
    Message message = new Message();
    message.setEmail(new Email(mailbox.getEmailAddress()));
    message.setHeader(Constants.CONNECTION_CLOSED);
    this.service.notifyClientDisconnect(message);
  }


  /**
   * @param email: the email to send
   * Notify service to send request to server
   */
  public void sendEmail(Email email) {
    System.out.println("I want to send: " + email);
    Message message = new Message();
    message.setHeader(Constants.NEW_EMAIL);
    message.setEmail(email);
    service.sendMessageService(message);
  }

  /**
   * @param email: the email to delete
   * Notify service to send request to server
   */
  public void deleteEmail(Email email) {
    System.out.println("I want to delete: " + email);
    Email e = new Email(mailbox.getEmailAddress());
    e.setId(email.getId());
    Message message = new Message();
    message.setHeader(Constants.DELETE_EMAIL_BY_ID);
    message.setEmail(e);
    service.sendMessageService(message);
  }

  /**
   * @param flag: boolean value used in method to disable buttons
   * If server is down disables buttons and shows alert message
   */
  public void notifyServerDown(boolean flag) {
    if (flag) {
      // if server is down disable controls and show alert message
      this.guiController.disableControls(true);
      this.guiController.disableControlsNewGui(true);
      this.guiController.alertInformation();
    } else {
      // else enable mailbox management buttons
      this.guiController.disableDashboardCta(false);
      this.guiController.disableControlsNewGui(false);
      if(!this.guiController.newGuiIsShowing){
        this.guiController.restartCommands();
      }
    }
  }

  /**
   * Resets alert message
   */
  public void resetAlert() {
    this.guiController.resetAlert();
  }

  /**
   * Checks if server is up
   * @return true if server is up, false otherwise
   */
  public boolean checkServerStatus() {
    return service.getServerStatus();
  }
}
