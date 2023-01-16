package com.unito.prog3.progetto.mailclient;

import com.unito.prog3.progetto.mailclient.controller.ClientGuiController;
import com.unito.prog3.progetto.mailclient.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Project Client Application
 */
public class ClientApplication extends Application {
  private ClientController clientController;

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main_gui_mockup.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    ClientGuiController guiController = fxmlLoader.getController();
    clientController = new ClientController();
    clientController.setGuiController(guiController);
    guiController.initialize(clientController);
    stage.setOnCloseRequest(windowEvent -> {
      guiController.closeAllChildren();
      clientController.guiIsClosing();
    });
    stage.setTitle("Email Client");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
