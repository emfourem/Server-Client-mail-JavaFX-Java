package com.unito.prog3.progetto.externmodel;

import java.io.Serializable;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents a message (composed by header + email) to send to server
 */

public class Message implements Serializable {
  private String header;
  private Email email;

  public Message() {}

  public String getHeader() {
    return this.header;
  }
  public void setHeader(String header) {
    this.header=header;
  }

  public Email getEmail() {
    return email;
  }

  public void setEmail(Email email) {
    this.email = email;
  }
}
