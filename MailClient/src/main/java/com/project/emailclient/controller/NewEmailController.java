package com.project.emailclient.controller;

import com.project.emailclient.model.MailClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

// controller della gui CLIENT
public class NewEmailController {
  @FXML
  private TextField sendToTextField;
  // buttons
  @FXML
  private Button cancelEmailBtn;
  @FXML
  private Button sendEmailBtn;
  private MailClient c;

  @FXML
  public void initialize(MailClient c) {
    this.c = c;
  }

  @FXML
  public void onCancelEmail() {
    System.out.println("cancel email");
    this.triggerClose(cancelEmailBtn);
  }

  @FXML
  public void onSendEmail() {
    System.out.println("send email");
    System.out.println("DICO AL CLIENT DI INVIARE EMAIL");
    c.sendEmailToServer();
    // networkClient.send(payload)
    this.triggerClose(sendEmailBtn);
  }

  private void triggerClose(Button source) {
    Stage stage = (Stage) source.getScene().getWindow();
    stage.hide();
  }

}
