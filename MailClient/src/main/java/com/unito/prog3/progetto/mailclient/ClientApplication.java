package com.unito.prog3.progetto.mailclient;

import com.unito.prog3.progetto.mailclient.controller.ClientGuiController;
import com.unito.prog3.progetto.mailclient.controller.ClientController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ClientApplication extends Application {
  private ClientController clientController;

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main_gui_mockup.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    ClientGuiController controller = fxmlLoader.getController();
    clientController = new ClientController();
    clientController.setGuiController(controller);
    controller.initialize(clientController);
    //<WindowEvent can be replaced with <> or lambda expression
    /*
    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        controller.closeAllChildren();
        clientController.guiIsClosing();
      }
    });
     */
    stage.setOnCloseRequest(windowEvent -> {
      controller.closeAllChildren();
      clientController.guiIsClosing();
    });
    stage.setTitle("Mail Client");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
