package com.unito.prog3.progetto.mailserver;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;
import com.unito.prog3.progetto.mailserver.model.Client;
import com.unito.prog3.progetto.mailserver.model.MailServerService;
import com.unito.prog3.progetto.model.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Project Server Application
 */
public class ServerApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server_gui.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 700, 500);
    ServerGuiController controller = fxmlLoader.getController();
    MailServerService service = new MailServerService(Constants.MAIL_SERVER_PORT, controller);
    Client c = new Client("MMT");
    controller.initialize(c, service);
    stage.setOnCloseRequest(windowEvent -> service.guiIsClosing());
    stage.setTitle("Email Server");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }
  public static void main(String[] args) {
    launch();
  }
}