package com.unito.prog3.progetto.model;

public enum Constants {
  NEW_EMAIL("New Email"),
  FORWARD_EMAIL("Forward Email"),
  REPLY_EMAIL("Reply Email"),
  REPLY_ALL_EMAIL("Reply Email To All");

  private final String name;

  Constants(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
