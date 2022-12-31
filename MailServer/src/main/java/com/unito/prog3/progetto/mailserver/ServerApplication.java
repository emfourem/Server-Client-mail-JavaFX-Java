package com.unito.prog3.progetto.mailserver;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;
import com.unito.prog3.progetto.mailserver.model.Client;
import com.unito.prog3.progetto.mailserver.model.MailServerService;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ServerApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server_minimal_gui_mockup.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 700, 500);
    ServerGuiController controller = fxmlLoader.getController();
    MailServerService service = new MailServerService(6789, controller);
    Client c = new Client("MARIO");
    controller.initialize(c, service);
    //closeServer...
    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        service.guiIsClosing();
      }
    });
    //
    stage.setTitle("Server Logger!");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}