package com.unito.prog3.progetto.mailserver.service;

import com.unito.prog3.progetto.model.Email;
import java.io.*;
import java.util.ArrayList;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the service that manages the server database
 */
public class MyFileWriterService {

  /**
   * Retrieves client emails
   * @param email: the email of client that made the request
   * @return a list of received client emails
   */
  public static synchronized ArrayList<Email> retrieveMails(String email) throws IOException, ClassNotFoundException {
    File mailbox = new File("database/".concat(email.replace("@gmail.com","").concat(".dat")));
    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(mailbox));
    Object o = objectInputStream.readObject();
    objectInputStream.close();
    ArrayList<Email> emails = new ArrayList<>();
    if(o != null && o.getClass() == emails.getClass()){
      emails = (ArrayList<Email>) o;
    }
    return emails;
  }

  /**
   * Creates a new mailbox for the client
   * @param emailSender: the new client whose mailbox must be created
   */
  public static synchronized void createMailbox(String emailSender) throws IOException {
    // mailbox is the file to be opened for writing
    File mailbox = new File("database/".concat(emailSender.replace("@gmail.com","").concat(".dat")));
    if (mailbox.createNewFile()) {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
      // at the beginning there are no received emails
      // writes buffer content and then cleans it
      objectOutputStream.writeObject(new ArrayList<Email>());
      objectOutputStream.flush();
      objectOutputStream.close();
    }
  }

  /**
   * Writes email in the client mailbox
   * @param email: the email to be written
   * @return a list of not found receivers
   */
  public static synchronized ArrayList<String> writeEmail(Email email) throws IOException, ClassNotFoundException {
    ArrayList<String> destNotFound = new ArrayList<>();
    for (String s : email.getReceivers()) {
      File mailbox = new File("database/".concat(s.replace("@gmail.com","").concat(".dat")));
      if (!mailbox.exists()) {
        destNotFound.add(s);
      } else {
        ArrayList<Email> emailArrayList = retrieveMails(s);
        emailArrayList.add(email);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
        objectOutputStream.writeObject(emailArrayList);
        objectOutputStream.flush();
        objectOutputStream.close();
      }
    }
    return destNotFound;
  }

  /**
   * Updates the mailbox
   * @param email: the client owner of the mailbox
   * @param emails: the emails updated of client
   */
  public static synchronized void updateMailboxOf(String email, ArrayList<Email> emails) throws IOException{
    File mailbox = new File("database/".concat(email.replace("@gmail.com","").concat(".dat")));
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
    objectOutputStream.writeObject(emails);
    objectOutputStream.flush();
    objectOutputStream.close();
  }

  /**
   * Deletes the email from client mailbox
   * @param email: the email to be deleted
   */
  public static synchronized void deleteEmail(Email email) throws IOException, ClassNotFoundException {
    ArrayList<Email> emailsOf = retrieveMails(email.getSender());
    emailsOf.remove(email);
    updateMailboxOf(email.getSender(), emailsOf);
  }

  /**
   * Marks the email state
   * @param email: the email to be marked as the head value
   * @param head: the value of the email state
   */
  public static synchronized void markAs(Email email, String head) throws IOException, ClassNotFoundException {
    ArrayList<Email> emails = retrieveMails(email.getSender());
    for (Email e : emails) {
      if (e.getId() == email.getId()) {
        e.setState(head);
      }
    }
    // writes on file
    updateMailboxOf(email.getSender(), emails);
  }
}
