package com.unito.prog3.progetto.model;

// RFC 9999
public enum ServiceHeaders {
  CONNECTION_REQUEST("Richiesta di connessione"),
  REQUEST_NEW_EMAILS("Richiesta nuovi messaggi"),
  REQUEST_MARK_EMAIL_AS_SEEN("Segna come letto"),
  CONNECTION_CLOSED("Richiesta di disconnect"),
  DELETE_EMAIL_BY_ID("Delete");

  private final String headerMessage;

  ServiceHeaders(String headerMessage) {
    this.headerMessage = headerMessage;
  }

  @Override
  public String toString() {
    return this.headerMessage;
  }
}
