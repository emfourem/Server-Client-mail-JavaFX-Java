module com.unito.prog3.progetto.client.mailclient {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.unito.prog3.progetto.mailclient to javafx.fxml;
  exports com.unito.prog3.progetto.mailclient;

  opens com.unito.prog3.progetto.mailclient.controller to javafx.fxml;
  exports com.unito.prog3.progetto.mailclient.controller;
}