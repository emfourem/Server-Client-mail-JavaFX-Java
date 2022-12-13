package com.project.emailclient.controller;

import com.project.emailclient.model.MailClient;
import com.project.model.Constants;
import com.project.model.Email;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

// controller della gui CLIENT
public class NewEmailController {
  @FXML
  private TextField sendToTextField;
  @FXML
  private TextField fromEmailTextField;
  @FXML
  private TextField objectEmailTextField;
  // text area
  @FXML
  private TextArea emailBodyTextArea;
  // buttons
  @FXML
  private Button cancelEmailBtn;
  @FXML
  private Button sendEmailBtn;
  private MailClient mailClient;

  @FXML
  public void initialize(MailClient c, Constants flag, Email currentEmail) {
    this.mailClient = c;
    fromEmailTextField.setText(mailClient.getEmailAddress());
    switch (flag) {
      case NEW_EMAIL -> {
      }
      case REPLY_EMAIL -> {
        sendToTextField.setText(currentEmail.getSender());
        objectEmailTextField.setText("RE: " + currentEmail.getObject());
        objectEmailTextField.setEditable(false);
      }
      case FORWARD_EMAIL -> {
        objectEmailTextField.setText(currentEmail.getObject());
        emailBodyTextArea.setText(currentEmail.getText());
      }
      case REPLY_ALL_EMAIL -> {
        // TODO (gestire caso con textarea modificato)
        //
        String rs = String.join(", ", currentEmail.getReceivers()).replace(c.getEmailAddress(), currentEmail.getSender());
        System.out.println(rs);
        sendToTextField.setText(rs);
        objectEmailTextField.setText("RE: " + currentEmail.getObject());
        objectEmailTextField.setEditable(false);
      }
      default -> {
        System.out.println("default catch");
      }
    }
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
    mailClient.sendEmailToServer();
    // networkClient.send(payload)
    this.triggerClose(sendEmailBtn);
  }

  private void triggerClose(Button source) {
    Stage stage = (Stage) source.getScene().getWindow();
    stage.hide();
  }

}
