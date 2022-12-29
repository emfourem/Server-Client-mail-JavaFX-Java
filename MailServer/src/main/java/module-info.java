module com.unito.prog3.progetto.mailserver.mailserver {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.unito.prog3.progetto.mailserver to javafx.fxml;
  exports com.unito.prog3.progetto.mailserver;

  opens com.unito.prog3.progetto.mailserver.controller to javafx.fxml;
  exports com.unito.prog3.progetto.mailserver.controller;
}