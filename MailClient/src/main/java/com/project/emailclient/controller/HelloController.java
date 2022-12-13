package com.project.emailclient.controller;

import com.project.emailclient.HelloApplication;
import com.project.emailclient.model.MailClient;
import com.project.emailclient.model.MailClient;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

// controller della gui CLIENT
public class HelloController {
  // lista email ricevute
  @FXML
  private ListView receivedEmailsListView;
  // label dell'email del client
  @FXML
  private Label accountLabel;
  @FXML
  private Label emailObjectDataLabel;
  @FXML
  private Label receiverDataLabel;
  @FXML
  private Label dateDataLabel;
  // text areas
  @FXML
  private TextArea emailTextArea;
  // gruppo di bottoni gestione casella
  @FXML
  private Button writeEmailBtn;
  @FXML
  private Button deleteAllBtn;
  // gruppo bottoni gestione email
  @FXML
  private Button replyBtn;
  @FXML
  private Button forwardBtn;
  @FXML
  private Button deleteBtn;
  @FXML
  private Button replyAllBtn;

  //
  private MailClient c;

  @FXML
  public void initialize(MailClient c) {
    this.c = c;
    new Thread(c::connectToServer).start();
    accountLabel.setText(this.c.getEmail());
  }

  @FXML
  public void onWriteEmail() throws IOException {
    System.out.println("onWriteEmail");
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("NewEmail.fxml"));
    Stage stage1 = new Stage();
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage1.setTitle("My New Stage Title");
    stage1.setScene(scene);
    NewEmailController controller = fxmlLoader.getController();
    controller.initialize(c);
    stage1.setOnCloseRequest(event -> writeEmailBtn.setDisable(false));
    stage1.setOnHiding(event -> writeEmailBtn.setDisable(false));
    writeEmailBtn.setDisable(true);
    stage1.show();
    System.out.println("onWriteEmailDone");
  }

  @FXML
  public void onDeleteAllEmails() {

  }

  @FXML
  public void onForwardEmail() {

  }

  @FXML
  public void onDeleteEmail() {

  }

  @FXML
  public void onReplyAll() {

  }

  @FXML
  public void onReply() {

  }

  public void updateInbox(ArrayList<String> l) {

  }
}
