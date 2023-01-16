package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.model.Constants;
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

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents new email GUI controller
 */
public class NewEmailGuiController {

  /**
   * Text fields
   */
  @FXML
  private TextField sendToTextField;
  @FXML
  private TextField fromEmailTextField;
  @FXML
  private TextField objectEmailTextField;

  /**
   * Text area
   */
  @FXML
  private TextArea emailBodyTextArea;

  /**
   * Buttons
   */
  @FXML
  private Button clearTextBtn;
  @FXML
  private Button sendEmailBtn;

  /**
   * Model and controller
   */
  private ClientMailbox mailClient;
  private ClientController controller;
  private String flag;
  private ClientGuiController mainGuiController;

  /**
   * @param c: model
   * @param flag: operation chosen by user
   * @param currentEmail: email selected by user
   * @param controller: controller of project model
   */
  @FXML
  public void initialize(ClientMailbox c, String flag, Email currentEmail, ClientController controller, ClientGuiController  mainGuiController) {
    //necessary to retrieve email address of client
    this.mailClient = c;
    this.controller = controller;
    this.mainGuiController = mainGuiController;
    this.flag = flag;
    fromEmailTextField.setText(mailClient.getEmailAddress());
    if(Constants.REPLY_EMAIL.equalsIgnoreCase(flag)){
      sendToTextField.setText(currentEmail.getSender());
      currentEmail.setState(Constants.REPLY_EMAIL);
      objectEmailTextField.setText("RE: " + currentEmail.getObject());
      objectEmailTextField.setEditable(false);
    } else if(Constants.FORWARD_EMAIL.equalsIgnoreCase(flag)) {
      objectEmailTextField.setText(currentEmail.getObject());
      objectEmailTextField.setEditable(false);
      currentEmail.setState(Constants.FORWARD_EMAIL);
      emailBodyTextArea.setText(currentEmail.getText());
    } else if(Constants.REPLY_ALL_EMAIL.equalsIgnoreCase(flag)) {
      currentEmail.setState(Constants.REPLY_ALL_EMAIL);
      String rs = String.join(",", currentEmail.getReceivers()).replace(c.getEmailAddress(), currentEmail.getSender());
      sendToTextField.setText(rs);
      objectEmailTextField.setText("RE: " + currentEmail.getObject());
      objectEmailTextField.setEditable(false);
    }
  }

  /**
   * Sends the email if the receiver address is formally correct, shows alert message otherwise
   */
  @FXML
  public void onSendEmail() {
    // regular expression to check email address
    String regex="^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
    Pattern pattern = Pattern.compile(regex);
    String[] matcherReceivers = sendToTextField.getText().split(",");
    String wrongEmailAddresses = "";
    boolean flag = true;
    for(String s : matcherReceivers) {
      Matcher matcher = pattern.matcher(s);
      if(!matcher.matches()) {
        wrongEmailAddresses = wrongEmailAddresses.concat(s+"\n");
        flag = false;
      }
    }
    // all emails match the pattern of the regular expression
    if(flag) {
      Email email = new Email(controller.getMailbox().getEmailAddress());
      email.setId(System.currentTimeMillis());
      email.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date()));
      email.setState(Constants.NEW_EMAIL);
      email.setReceivers(Arrays.asList(sendToTextField.getText().split(",")));
      email.setText(emailBodyTextArea.getText());
      email.setObject(objectEmailTextField.getText());
      // send email and close new GUI
      controller.sendEmail(email);
      this.triggerClose(sendEmailBtn);
      // enable main GUI buttons
      this.mainGuiController.disableDashboardCta(false);
      this.mainGuiController.restartCommands();
    }
    // some email is formally wrong
    else {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setHeaderText("Wrong email");
      alert.setTitle("Check Email");
      alert.setContentText("Email entered is not a valid email address:\n" + wrongEmailAddresses);
      alert.showAndWait();
      sendToTextField.requestFocus();
    }
  }

  /**
   * Delete the text in the body
   */
  @FXML
  public void onCancelEmail() {
   emailBodyTextArea.setText("");
  }

  /**
   * @param source: the cancel GUI button
   * Closes the new GUI
   */
  private void triggerClose(Button source) {
    Stage stage = (Stage) source.getScene().getWindow();
    this.mainGuiController.newGuiIsShowing = false;
    stage.hide();
  }

  /**
   * @param flag: boolean value used by method to set disable values
   * Sets disable value of new GUI buttons
   */
  public void disableButtons(boolean flag) {
    clearTextBtn.setDisable(flag);
    sendEmailBtn.setDisable(flag);
  }
}
