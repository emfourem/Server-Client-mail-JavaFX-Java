package com.unito.prog3.progetto.mailclient.model;

import com.unito.prog3.progetto.model.Constants;
import com.unito.prog3.progetto.model.Email;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class ClientMailbox {
  private final ObservableList<Email> inboxContent;
  private final SimpleListProperty<Email> inbox;
  private final SimpleStringProperty emailAddress;

  public ClientMailbox(String email) {
    this.inbox = new SimpleListProperty<>();
    this.inboxContent = FXCollections.observableArrayList(new LinkedList<>());
    this.inbox.set(inboxContent);
    this.emailAddress = new SimpleStringProperty(email);
  }

  public String getEmailAddress() {
    return emailAddress.get();
  }

  public SimpleStringProperty emailAddressProperty() {
    return emailAddress;
  }

  public SimpleListProperty<Email> inboxProperty() {
    return inbox;
  }

  public void addEmail(Email email) {
    this.inboxContent.add(email);
  }

  public void emptyInbox() {
    this.inboxContent.clear();
  }

  public void deleteEmail(Email e) {
    e.setStato(Constants.MAIL_DELETED);
  }
}
