package com.unito.prog3.progetto.model;

import java.io.Serializable;

public class Message implements Serializable {
  private String header;
  private Email email;

  public Message() {

  }

  public Message(String header, Email email) {
    this.header = header;
    this.email = email;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public Email getEmail() {
    return email;
  }

  public void setEmail(Email email) {
    this.email = email;
  }
}
