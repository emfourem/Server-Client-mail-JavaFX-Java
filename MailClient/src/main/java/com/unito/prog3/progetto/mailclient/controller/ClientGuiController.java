package com.unito.prog3.progetto.mailclient.controller;

import com.unito.prog3.progetto.mailclient.ClientApplication;
import com.unito.prog3.progetto.mailclient.model.ClientMailbox;
import com.unito.prog3.progetto.model.EmailStateEnum;
import com.unito.prog3.progetto.model.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

// controller della gui CLIENT
public class ClientGuiController {
  // lista email ricevute
  @FXML
  private ImageView accountDummyImg;
  @FXML
  private ListView<Email> receivedEmailsListView;
  // label dell'email del client
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
  private ClientController clientController;
  private ClientMailbox mailbox;
  private Email selectedEmail;
  private Email emptyEmail;

  // child
  Stage stage1 = null;
  Alert alert = null;
  Alert alertNewMessage = null;
  boolean alertOnes = true;
  NewEmailGuiController childController;

  @FXML
  public void initialize(ClientController controller){
    this.clientController = controller;
    this.mailbox = controller.getMailbox();
    selectedEmail = null;
    accountLabel.setText(this.mailbox.getEmailAddress());
    emailTextArea.setEditable(false);
    receivedEmailsListView.setOnMouseClicked(this::showSelectedEmail);
    receivedEmailsListView.itemsProperty().bind(mailbox.inboxProperty());
    emptyEmail = new Email(-1, "", Arrays.asList(""), "", "", new Date());
    // disabilito i bottoni
    disableAllEmailBtns(true);
    //
    updateDetailView(emptyEmail);
    //
    clientController.startService();
  }

  protected void showSelectedEmail(MouseEvent mouseEvent) {
    Email email = receivedEmailsListView.getSelectionModel().getSelectedItem();

    if (email.getStato().equalsIgnoreCase(EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString())) {
      // email.setStato(EmailStateEnum.MAIL_SEEN.toString());
      Email seenMail = new Email(this.mailbox.getEmailAddress());
      seenMail.setId(email.getId());
      this.clientController.seenMail(seenMail, EmailStateEnum.MAIL_SEEN.toString());
    }

    selectedEmail = email;
    updateDetailView(email);
    if (email != null) {
      disableAllEmailBtns(false);
    }
  }

  protected void updateDetailView(Email email) {
    if (email != null) {
      senderDataLabel.setText(email.getSender());
      receiverDataLabel.setText(String.join(", ", email.getReceivers()));
      emailObjectDataLabel.setText(email.getObject());
      if (email.getId() == -1) {
        dateDataLabel.setText("");
        disableAllEmailBtns(true);
      } else {
        dateDataLabel.setText(email.getDate());
      }
      emailTextArea.setText(email.getText());
    }
  }

  @FXML
  public void onWriteEmail() throws IOException {
    System.out.println("onWriteEmail");
    launchNewEmailGui(EmailStateEnum.NEW_EMAIL, selectedEmail);
    System.out.println("onWriteEmailDone");
  }

  @FXML
  public void onDeleteAllEmails() {
    System.out.println("Elimino la casella");
    for (Email email : this.mailbox.inboxProperty().get()) {
      System.out.println("Elimino: " + email);
      clientController.deleteEmail(email);
    }
    mailbox.emptyInbox();
    updateDetailView(emptyEmail);
  }

  @FXML
  public void onForwardEmail() throws IOException {
    disableControls(true);
    launchNewEmailGui(EmailStateEnum.FORWARD_EMAIL, selectedEmail);
  }

  @FXML
  public void onDeleteEmail() {
    mailbox.deleteEmail(selectedEmail);
    clientController.deleteEmail(selectedEmail);
    updateDetailView(emptyEmail);
    disableControls(false);
  }

  @FXML
  public void onReplyAll() throws IOException {
    disableControls(true);
    launchNewEmailGui(EmailStateEnum.REPLY_ALL_EMAIL, selectedEmail);
  }

  @FXML
  public void onReply() throws IOException {
    disableControls(true);
    launchNewEmailGui(EmailStateEnum.REPLY_EMAIL, selectedEmail);
  }

  protected void disableAllEmailBtns(boolean flag) {
    replyBtn.setDisable(flag);
    forwardBtn.setDisable(flag);
    deleteBtn.setDisable(flag);
    replyAllBtn.setDisable(flag);
  }

  public void resetAlert() {
    this.alert = null;
  }

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

  public void alertNewMessage(String s) {
    // if (alertNewMessage != null) {
      alertNewMessage = new Alert(Alert.AlertType.INFORMATION);
      alertNewMessage.setHeaderText("New Message Received!");
      alertNewMessage.setContentText("New message received from " + s);
      alertNewMessage.setTitle("New Message Notification");
      alertNewMessage.show();
    // }
  }

  protected void launchNewEmailGui(EmailStateEnum c, Email e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("new_email_gui_mockup.fxml"));
    stage1 = new Stage();
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage1.setTitle(c.name());
    stage1.setScene(scene);
    childController = fxmlLoader.getController();
    childController.initialize(mailbox, c, e, clientController);
    stage1.setOnCloseRequest(event -> {
      disableControls(false);
    });
    stage1.setOnHiding(event -> {
      disableControls(false);
    });
    writeEmailBtn.setDisable(true);
    stage1.show();
  }

  public void disableControls(boolean flag) {
    disableAllEmailBtns(flag);
    writeEmailBtn.setDisable(flag);
    deleteAllBtn.setDisable(flag);
    if (childController != null) {
      childController.disableBts(flag);
    }
  }

  public void closeAllChildren() {
    if (this.stage1 != null && stage1.isShowing()) {
      stage1.close();
    }
  }
}
