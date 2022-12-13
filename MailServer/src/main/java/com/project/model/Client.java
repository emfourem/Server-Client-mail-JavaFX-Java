package com.project.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

public class Client {
  private final ObservableList<String> inboxContent;
  private final SimpleListProperty<String> inbox;
  private final SimpleStringProperty email;

  public Client(String email) {
    this.inbox = new SimpleListProperty<>();
    this.inboxContent = FXCollections.observableArrayList(new LinkedList<>());
    this.inbox.set(inboxContent);
    this.email = new SimpleStringProperty(email);
  }

  public SimpleStringProperty emailProperty() {
    return email;
  }

  public SimpleListProperty<String> inboxProperty() {
    return inbox;
  }

  public void addEmail(String email) {
    this.inboxContent.add(email);
  }
}
