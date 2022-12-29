package com.unito.prog3.progetto.model;

public enum ServiceHeaders {
  CONNECTION_REQUEST("Richiesta di connessione"),
  REQUEST_NEW_EMAILS("Richiesta nuovi messaggi"),
  CONNECTION_CLOSED("Richiesta di disconnect");

  private final String headerMessage;

  ServiceHeaders(String headerMessage) {
    this.headerMessage = headerMessage;
  }

  @Override
  public String toString() {
    return this.headerMessage;
  }
}
