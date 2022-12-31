package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.model.EmailStateEnum;
import com.unito.prog3.progetto.model.Email;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.*;

// controller della gui CLIENT
public class NewEmailGuiController {
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

  private ClientMailbox mailClient;
  private ClientController controller;

  @FXML
  public void initialize(ClientMailbox c, EmailStateEnum flag, Email currentEmail, ClientController controller) {
    this.mailClient = c;
    this.controller = controller;
    fromEmailTextField.setText(mailClient.getEmailAddress());
    switch (flag) {
      case NEW_EMAIL -> {
      }
      case REPLY_EMAIL -> {
        sendToTextField.setText(currentEmail.getSender());
        currentEmail.setStato(EmailStateEnum.REPLY_EMAIL.toString());
        objectEmailTextField.setText("RE: " + currentEmail.getObject());
        objectEmailTextField.setEditable(false);
      }
      case FORWARD_EMAIL -> {
        objectEmailTextField.setText(currentEmail.getObject());
        currentEmail.setStato(EmailStateEnum.FORWARD_EMAIL.toString());
        emailBodyTextArea.setText(currentEmail.getText());
      }
      case REPLY_ALL_EMAIL -> {
        currentEmail.setStato(EmailStateEnum.REPLY_ALL_EMAIL.toString());
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
    String regex="^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
    Pattern pattern=Pattern.compile(regex);
    //gestire caso dei molteplici receivers
    Matcher matcher=pattern.matcher(sendToTextField.getText());
    if(matcher.matches()) {
      Email email = new Email();
      email.setId(System.currentTimeMillis());
      email.setSender(controller.getMailbox().getEmailAddress());
      email.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date()));
      email.setStato(EmailStateEnum.NEW_EMAIL.toString());
      email.setReceivers(Arrays.asList(sendToTextField.getText().split(",")));
      email.setText(emailBodyTextArea.getText());
      email.setObject(objectEmailTextField.getText());
      controller.sendEmail(email);
      this.triggerClose(sendEmailBtn);
    }else{
      Alert alert=new Alert(Alert.AlertType.WARNING);
      alert.setHeaderText("Wrong email");
      alert.setTitle("Check Email");
      alert.setContentText("Email entered is not a valid email address");
      alert.show();
    }
  }

  private void triggerClose(Button source) {
    Stage stage = (Stage) source.getScene().getWindow();
    stage.hide();
  }

  public void disableBts(boolean flag) {
    cancelEmailBtn.setDisable(flag);
    sendEmailBtn.setDisable(flag);
  }
}
