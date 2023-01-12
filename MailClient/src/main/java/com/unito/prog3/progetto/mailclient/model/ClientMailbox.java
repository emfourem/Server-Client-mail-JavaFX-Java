package com.unito.prog3.progetto.mailclient.model;

import com.unito.prog3.progetto.model.Constants;
import com.unito.prog3.progetto.model.Email;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the application model
 */

public class ClientMailbox {
  /**
   * ObservableList and properties
   */
  private final ObservableList<Email> inboxContent;
  private final SimpleListProperty<Email> inbox;
  private final SimpleStringProperty emailAddress;

  /**
   * @param email: the user account email
   */
  public ClientMailbox(String email) {
    this.inbox = new SimpleListProperty<>();
    this.inboxContent = FXCollections.observableArrayList(new LinkedList<>());
    this.inbox.set(inboxContent);
    this.emailAddress = new SimpleStringProperty(email);
  }

  public String getEmailAddress() {
    return emailAddress.get();
  }

  public SimpleListProperty<Email> inboxProperty() {
    return inbox;
  }

  /**
   * @param email: the email to add in the inbox
   * Adds the email at the beginning of the inbox
   */
  public void addEmail(Email email) {
    this.inboxContent.add(0, email);
  }

  /**
   * Empty the inbox content
   */
  public void emptyInbox() {
    this.inboxContent.clear();
  }

  /**
   * @param e: the email to delete from the inbox
   * Removes the email from the inbox
   */
  public void deleteEmail(Email e) {
    e.setState(Constants.MAIL_DELETED);
    this.inbox.remove(e);
  }

  /**
   * @param email: the email to add in the inbox
   * Adds email if not present in the inbox
   */
  public void addEmailIfNotPresent(Email email) {
    if (!this.inboxContent.contains(email)) {
      this.addEmail(email);
    }
  }

  /**
   * @param email: the email to be checked
   * @return true if the inbox contains email, false otherwise
   */
  public boolean contains(Email email) {
    return this.inboxContent.contains(email);
  }
  /*public int mailboxLength() {
    return this.inboxContent.size();
  }*/
}
