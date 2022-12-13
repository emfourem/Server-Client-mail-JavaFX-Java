package com.project.view;

import com.project.model.Client;
import com.project.model.MailService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HelloApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ServerMinimalGui.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 700, 500);
    ServerController controller = fxmlLoader.getController();
    Client c = new Client("MARIO");
    controller.initialize(c);
    MailService service = new MailService(6789, controller);
    service.start();
    stage.setTitle("Server Logger!");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    // setup del server
    // launch the gui
    launch();
  }
}