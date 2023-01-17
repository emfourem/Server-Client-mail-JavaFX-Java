package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.ClientApplication;
import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.externmodel.Constants;
import com.unito.prog3.progetto.externmodel.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents GUI client controller
 */

public class ClientGuiController {
  /**
   * Received emails ListView
   */
  @FXML
  private ListView<Email> receivedEmailsListView;

  /**
   * Labels
   */
  @FXML
  private Label accountLabel;
  @FXML
  private Label emailObjectDataLabel;
  @FXML
  private Label receiverDataLabel;
  @FXML
  private Label dateDataLabel;
  @FXML
  private Label senderDataLabel;

  /**
   * Text area
   */
  @FXML
  private TextArea emailTextArea;

  /**
   * Mailbox management buttons group
   */
  @FXML
  private Button writeEmailBtn;
  @FXML
  private Button deleteAllBtn;

  /**
   * Emails management buttons group
   */
  @FXML
  private Button replyBtn;
  @FXML
  private Button forwardBtn;
  @FXML
  private Button deleteBtn;
  @FXML
  private Button replyAllBtn;

  /**
   * Model and controller
   */
  private ClientController clientController;
  private ClientMailbox mailbox;
  private Email selectedEmail;
  private Email emptyEmail;

  /**
   * New email GUI stage and controller
   */
  Stage stage1 = null;
  NewEmailGuiController childController;

  /**
   * Alert message
   */
  Alert alert = null;
  Alert alertNewMessage = null;
  boolean alertOnes = true;

  protected boolean newGuiIsShowing = false;

  /**
   * @param controller: controller of project model
   */
  @FXML
  public void initialize(ClientController controller) {
    this.clientController = controller;
    this.mailbox = controller.getMailbox();
    selectedEmail = null;
    accountLabel.setText(this.mailbox.getEmailAddress());
    emailTextArea.setEditable(false);
    // this method is used to modify automatically some listView graphic properties, and it is used at every change
    receivedEmailsListView.setCellFactory(cell -> new ListCell<>(){
      @Override
      protected void updateItem(Email email, boolean empty) {
        super.updateItem(email, empty);
        if (!empty && email != null) {
          setText(email.toString());
          if (Constants.NEW_EMAIL.equalsIgnoreCase(email.getState()) || Constants.EMAIL_RECEIVED_NOT_SEEN.equalsIgnoreCase(email.getState())) {
            setStyle("-fx-font-weight: bold");
          } else {
            setStyle(null);
          }
        } else {
          setText(null);
        }
      }
    });
    receivedEmailsListView.setOnMouseClicked(this::showSelectedEmail);
    // binding between the listView and the observableList through the SimpleListProperty
    receivedEmailsListView.itemsProperty().bind(mailbox.getInboxProperty());
    emptyEmail = new Email(-1, "", List.of(""), "", "", new Date());
    updateDetailView(emptyEmail);
    clientController.startService();
  }
  /**
   * @param mouseEvent: an event that invokes the method
   * Notify controller to send request to server to mark email as seen, then updates the view
   */
  protected void showSelectedEmail(MouseEvent mouseEvent) {
    Email email = receivedEmailsListView.getSelectionModel().getSelectedItem();
    //if the selected email is new or not seen...
    if (email != null && (Constants.NEW_EMAIL.equalsIgnoreCase(email.getState()) ||
            Constants.EMAIL_RECEIVED_NOT_SEEN.equalsIgnoreCase(email.getState()))) {
      email.setState(Constants.EMAIL_SEEN);
      Email seenMail = new Email(this.mailbox.getEmailAddress());
      seenMail.setId(email.getId());
      //...send request to server to mark email as seen...
      this.clientController.seenMail(seenMail, Constants.REQUEST_MARK_EMAIL_AS_SEEN);
    }
    //...then update the GUI...
    selectedEmail = email;
    updateDetailView(email);
    if(clientController.checkServerStatus()) {
      if (email != null) {
        //...and enable buttons
        disableControls(newGuiIsShowing);
        if(email.getSender().equalsIgnoreCase("no_reply.progetto.prog3@server.it")) {
          disableAllEmailButtons(true);
          this.deleteBtn.setDisable(newGuiIsShowing);
        }
      }
    }else{
        disableControls(true);
    }
  }

  /**
   * @param email: the selected email
   * Updates the view with the details of selected email
   */
  protected void updateDetailView(Email email) {
    if (email != null) {
      senderDataLabel.setText(email.getSender());
      receiverDataLabel.setText(String.join(",", email.getReceivers()));
      emailObjectDataLabel.setText(email.getObject());
      if (email.getId() == -1) {
        this.selectedEmail = null;
        dateDataLabel.setText("");
        disableAllEmailButtons(true);
      } else {
        dateDataLabel.setText(email.getDate());
      }
      emailTextArea.setText(email.getText());
    }
  }

  /**
   * Launches new GUI to write a new email
   */
  @FXML
  public void onWriteEmail() throws IOException {
    launchNewEmailGui(Constants.NEW_EMAIL, selectedEmail, this);
    disableControls(true);
  }

  /**
   * Notify controller to send request to server to delete all emails, then empty the inbox and updates the view
   */
  @FXML
  public void onDeleteAllEmails() {
    disableAllEmailButtons(true);
    for (Email email : this.mailbox.getInboxProperty().get()) {
      clientController.deleteEmail(email);
    }
    mailbox.emptyInbox();
    updateDetailView(emptyEmail);
  }

  /**
   * Launches new GUI to forward the email
   */
  @FXML
  public void onForwardEmail() throws IOException {
    launchNewEmailGui(Constants.FORWARD_EMAIL, selectedEmail,this);
    disableControls(true);
  }

  /**
   * Notify controller to send request to server to delete the email, then updates the view
   */
  @FXML
  public void onDeleteEmail() {
    mailbox.deleteEmail(selectedEmail);
    clientController.deleteEmail(selectedEmail);
    updateDetailView(emptyEmail);
    disableAllEmailButtons(true);
  }

  /**
   * Launches new GUI to reply email to all receivers
   */
  @FXML
  public void onReplyAll() throws IOException {
    launchNewEmailGui(Constants.REPLY_ALL_EMAIL, selectedEmail,this);
    disableControls(true);
  }

  /**
   * Launches new GUI to reply email
   */
  @FXML
  public void onReply() throws IOException {
    launchNewEmailGui(Constants.REPLY_EMAIL, selectedEmail,this);
    disableControls(true);
  }

  /**
   * Resets alert message to null
   */
  public void resetAlert() {
    this.alert = null;
  }

  /**
   * Shows alert message if server is down
   */
  public void alertInformation() {
    if (alert == null) {
      alertOnes = false;
      alert = new Alert(Alert.AlertType.WARNING);
      alert.setHeaderText("Connection to server failed!");
      alert.setContentText("Buttons will be enabled, once server is UP... be patient");
      alert.setTitle("Mail Service Warning");
      alert.showAndWait();
    }
  }

  /**
   * Shows alert message if new email is received
   */
  public void alertNewMessage(String s) {
    alertNewMessage = new Alert(Alert.AlertType.INFORMATION);
    alertNewMessage.setHeaderText("New Message Received!");
    alertNewMessage.setContentText("New message received from " + s);
    alertNewMessage.setTitle("New Message Notification");
    alertNewMessage.show();
  }

  /**
   * @param c: operation chosen by user (new, forward, reply, reply all)
   * @param e: selected email
   * Launches new email GUI when user chooses operation c applied to selected email e
   */
  protected void launchNewEmailGui(String c, Email e, ClientGuiController mainGuiController) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("new_email_gui_mockup.fxml"));
    newGuiIsShowing = true;
    stage1 = new Stage();
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage1.setTitle(c);
    stage1.setScene(scene);
    childController = fxmlLoader.getController();
    childController.initialize(mailbox, c, e, clientController, mainGuiController);
    disableControls(true);
    stage1.setOnCloseRequest(event -> {
      newGuiIsShowing = false;
      disableAllEmailButtons(selectedEmail == null || this.selectedEmail.getSender().equalsIgnoreCase("no_reply.progetto.prog3@server.it"));
      if(this.selectedEmail != null && this.selectedEmail.getSender().equalsIgnoreCase("no_reply.progetto.prog3@server.it")){
        this.deleteBtn.setDisable(!clientController.checkServerStatus());
      }
      writeEmailBtn.setDisable(!clientController.checkServerStatus());
      deleteAllBtn.setDisable(!clientController.checkServerStatus());
    });
    stage1.setResizable(false);
    stage1.show();
  }

  /**
   * @param flag: boolean value used by method to set disable values
   * Sets disable value of email management buttons
   */
  protected void disableAllEmailButtons(boolean flag) {
    replyBtn.setDisable(flag);
    forwardBtn.setDisable(flag);
    deleteBtn.setDisable(flag);
    replyAllBtn.setDisable(flag);
  }

  /**
   * @param flag: boolean value used by method to set disable values
   * Sets disable value of all buttons
   */
  public void disableControls(boolean flag) {
    disableAllEmailButtons(flag);
    disableDashboardCta(flag);
  }

  /**
   * @param flag: boolean value used by method to set disable values
   * Sets disable value of mailbox management buttons
   */
  public void disableDashboardCta(boolean flag) {
    if(stage1 != null && stage1.isShowing()) {
      writeEmailBtn.setDisable(true);
      deleteAllBtn.setDisable(true);
    }else{
      writeEmailBtn.setDisable(flag);
      deleteAllBtn.setDisable(flag);
    }
  }

  /**
   * @param flag: boolean value used by method to set disable values
   * Sets disable value of new GUI buttons
   */
  public void disableControlsNewGui(boolean flag) {
    if (this.stage1 != null ){
      childController.disableButtons(flag);
    }
  }

  /**
   * Closes stage child
   */
  public void closeAllChildren() {
    if (this.stage1 != null && stage1.isShowing()) {
      stage1.close();
    }
  }

  /**
   * Disables or enables main GUI email management buttons
   */
  public void restartCommands() {
    if(this.selectedEmail == null) {
      this.disableAllEmailButtons(true);
    } else {
      this.disableAllEmailButtons(this.selectedEmail.getSender().equalsIgnoreCase("no_reply.progetto.prog3@server.it"));
      if (this.selectedEmail.getSender().equalsIgnoreCase("no_reply.progetto.prog3@server.it")){
        this.deleteBtn.setDisable(false);
      }
    }
  }
}
