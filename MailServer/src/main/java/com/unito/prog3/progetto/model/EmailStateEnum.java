package com.unito.prog3.progetto.model;

public enum EmailStateEnum {
  NEW_EMAIL("New Email"),
  MAIL_SEEN("Seen"),
  MAIL_RECEIVED_NOT_SEEN("Received Not Seen"),
  FORWARD_EMAIL("Forward Email"),
  REPLY_EMAIL("Reply Email"),
  REPLY_ALL_EMAIL("Reply Email To All");

  private final String name;

  EmailStateEnum(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
