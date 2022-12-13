package com.project.emailclient;

import com.project.emailclient.controller.HelloController;
import com.project.emailclient.model.MailClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mockup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        HelloController controller = fxmlLoader.getController();
        controller.initialize(new MailClient("marco.pironti.botta@unito.it", controller));
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

