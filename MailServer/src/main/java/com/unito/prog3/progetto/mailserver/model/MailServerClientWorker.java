package com.unito.prog3.progetto.mailserver.model;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;
import com.unito.prog3.progetto.mailserver.service.MyFileWriterService;
import com.unito.prog3.progetto.model.Email;
import com.unito.prog3.progetto.model.EmailStateEnum;
import com.unito.prog3.progetto.model.Message;
import com.unito.prog3.progetto.model.ServiceHeaders;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//socket associata al singolo client
public class MailServerClientWorker extends Thread {
  private Socket socket;
  private ServerGuiController guiController;
  private String emailSender;
  private ArrayList<Email> inbox;
  private long lastAccess = new Date().getTime();

  public MailServerClientWorker(Socket socket, ServerGuiController guiController) {
    this.socket = socket;
    this.guiController = guiController;
    this.inbox = new ArrayList<>();
  }


  @Override
  public void run() {
    //mandare messaggi al client
    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      Message message = (Message) objectInputStream.readObject();
      String header = message.getHeader();

      if (header.equalsIgnoreCase(ServiceHeaders.CONNECTION_REQUEST.toString())) {
        System.out.println("Richiesta di collegamento");
        this.emailSender = message.getEmail().getSender();
        MyFileWriterService.createMailbox(message.getEmail());
        retrieveThenSendEmails(message.getEmail());
        // aggiorno la gui
        Platform.runLater(() -> {
          this.guiController.logNewConnection(socket.toString());
          this.guiController.logNewClient(this.emailSender);
        });

      } else if (header.equalsIgnoreCase(ServiceHeaders.CONNECTION_CLOSED.toString())) {
        // Email e = message.getEmail();
        // ArrayList<Email> emails = (ArrayList<Email>) objectInputStream.readObject();
        // MyFileWriterService.updateMailboxOf(e, emails);
        // aggiorno la gui
        Platform.runLater(() -> {
          this.guiController.logLostConnection(socket.toString() + "\n");
          this.guiController.logLostClient(message.getEmail().getSender() + "\n");
        });
      } else if (header.equalsIgnoreCase(EmailStateEnum.NEW_EMAIL.toString())) {
        receiveMailThenStore(message.getEmail());
      } else if (header.equalsIgnoreCase(ServiceHeaders.DELETE_EMAIL_BY_ID.toString())) {
        MyFileWriterService.deleteEmail(message.getEmail());
        // retrieveThenSendEmails(message.getEmail());
      } else if (header.equalsIgnoreCase(ServiceHeaders.REQUEST_NEW_EMAILS.toString())) {
        this.emailSender = message.getEmail().getSender();
        retrieveThenSendEmails(message.getEmail());
      } else if (header.equalsIgnoreCase(ServiceHeaders.REQUEST_MARK_EMAIL_AS_SEEN.toString())) {
        this.emailSender = message.getEmail().getSender();
        MyFileWriterService.markAs(message.getEmail(), ServiceHeaders.REQUEST_MARK_EMAIL_AS_SEEN.toString());
      } else if (header.equalsIgnoreCase(EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString())) {
        MyFileWriterService.markAs(message.getEmail(), EmailStateEnum.MAIL_RECEIVED_NOT_SEEN.toString());
      }


    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Task completata, finisco di lavorare...");
  }

  private void receiveMailThenStore(Email email) throws IOException, ClassNotFoundException {
    System.out.println("Messaggio ricevuto: ");
    System.out.println(email.getSender());
    System.out.println(email.getObject());
    System.out.println(email.getText());
    String dests = "";
    for (String s : email.getReceivers()) {
      System.out.println("Dest: " + s);
      dests += " [ " + s + "] ";
    }
    String finalDests = dests;
    Platform.runLater(() -> {
      this.guiController.logMessageSend("New message from [ " + email.getSender() + " ] to " + finalDests + "\n");
      this.guiController.logMessageSend("New message to [ " + finalDests + " ] from " + emailSender + "\n");
    });
    ArrayList<String> notFound = MyFileWriterService.writeEmail(email);
    if (notFound.size() > 0) {
      writeNoReply(email.getSender(), notFound);
    }
  }

  private void writeNoReply(String dest, ArrayList<String> notFound) throws IOException, ClassNotFoundException {
    Email noReply = new Email("no_reply.progetto.prog3@server.it");
    noReply.setReceivers(List.of(dest));
    noReply.setObject("noReply: Receivers not found");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Dear Sender,\n\tThe following list contains not found receivers of your message:\n");
    for (String s : notFound) {
      stringBuilder.append("\t").append(s).append("\n");
      Platform.runLater(() -> {
        this.guiController.logMessageSend("Message from " + dest + " to " + s + " failed [ user not found ]\n");
      });
    }
    stringBuilder.append("Cordially, \nTeam MMT\n");
    noReply.setText(stringBuilder.toString());
    noReply.setId(System.currentTimeMillis());
    noReply.setStato(EmailStateEnum.NEW_EMAIL.toString());
    noReply.setDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date()));
    MyFileWriterService.writeEmail(noReply);
  }

  private void retrieveThenSendEmails(Email email) throws IOException, ClassNotFoundException {
    inbox = MyFileWriterService.retreiveMails(email);
    sendEmails();
  }

  private void sendEmails() throws IOException, ClassNotFoundException {
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    objectOutputStream.writeObject(inbox);
    objectOutputStream.flush();
    System.out.println("Inivato la inbox");
  }

  /*
  public void retreiveEmails() throws IOException, ClassNotFoundException {

    // System.out.println( databaseDirectory.getAbsolutePath() + " " + databaseDirectory.isDirectory());
    File[] files = databaseDirectory.listFiles();
    for (File f : files) {
      if (f.getName().equalsIgnoreCase(this.emailSender.concat(".dat"))) {
        System.out.println(new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss.S").format(new Date(f.lastModified())));
        System.out.println("mailbox trovata!");
        // recupero le mie email
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("database/".concat(this.emailSender.concat(".dat"))));
        inbox = (ArrayList<Email>) objectInputStream.readObject();
        return;
      }
    }
    // ciclo fallito, quindi creo la mia mailbox
    File mailbox = new File("database/".concat(this.emailSender.concat(".dat")));
    if (mailbox.createNewFile()) {
      System.out.println("Ho creato la mailbox di " + mailbox.getName());
      // dummyMails
      // popolo la inbox
    }
    dummyEmails();
    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
    outputStream.writeObject(inbox);
    outputStream.flush();
    outputStream.close();
  }
  */

  public void dummyEmails() {
    Email e1 = new Email(1, "mario.rossi@gmail.com", Arrays.asList("luca.verdi@uni.it", "marco.marroni@unito.it", "marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
    Email e2 = new Email(2, "alberto.marino@gmail.com", Arrays.asList("luca.verdi@uni.it", "marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
    Email e3 = new Email(3, "antonio.pesce@gmail.com", List.of("marco.pironti.botta@unito.it"), "Oggetto della mia mail", "Testo del messaggio", new Date());
    inbox.add(e1);
    inbox.add(e2);
    inbox.add(e3);
  }

  /*
  protected class SubworkerIn extends Thread {
    @Override
    public synchronized void run() {
      // QUESTA CLASSE fara da destinatario verso il client

    }
  }

  protected class SubworkerOut extends Thread {
    @Override
    public synchronized void run() {
      // QUESTA CLASSE fara da mittente verso il client
      try {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        retreiveEmails();
        objectOutputStream.writeObject(inbox);
        objectOutputStream.flush();
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
   */
}
