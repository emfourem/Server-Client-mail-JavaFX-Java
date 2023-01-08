package com.unito.prog3.progetto.mailserver.service;

import com.unito.prog3.progetto.model.Email;

import java.io.*;
import java.util.ArrayList;

public class MyFileWriterService {

  public static synchronized ArrayList<Email> retrieveMails(Email email) throws IOException, ClassNotFoundException {
    File mailbox = new File("database/".concat(email.getSender().concat(".dat")));
    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(mailbox));
    return (ArrayList<Email>) objectInputStream.readObject();
  }

  public static synchronized void createMailbox(Email email) throws IOException {
    File mailbox = new File("database/".concat(email.getSender().concat(".dat")));
    if (!mailbox.exists()) {
      // creo la mailbox
      if (mailbox.createNewFile()) {
        System.out.println("Ho creato la mailbox di " + mailbox.getName());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
        objectOutputStream.writeObject(new ArrayList<Email>());
        objectOutputStream.flush();
        objectOutputStream.close();
      }
    } else {
      System.out.println("Mailbox of " + email.getSender() + " already exists!");
    }
  }

  public static synchronized ArrayList<String> writeEmail(Email email) throws IOException, ClassNotFoundException {
    ArrayList<String> destNotFound = new ArrayList<>();
    for (String s : email.getReceivers()) {
      File mailbox = new File("database/".concat(s.concat(".dat")));
      if (!mailbox.exists()) {
        destNotFound.add(s);
      } else {
        Email sEmail = new Email(s);
        ArrayList<Email> emailArrayList = retrieveMails(sEmail);
        emailArrayList.add(email);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
        objectOutputStream.writeObject(emailArrayList);
        objectOutputStream.flush();
        objectOutputStream.close();
      }
    }
    return destNotFound;
  }

  public static synchronized void updateMailboxOf(Email email, ArrayList<Email> emailsFromDisconnectedClient) throws IOException, ClassNotFoundException {
    // aggiorna lo storage
    File mailbox = new File("database/".concat(email.getSender().concat(".dat")));
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
    objectOutputStream.writeObject(emailsFromDisconnectedClient);
    objectOutputStream.flush();
    objectOutputStream.close();
  }

  public static synchronized void deleteEmail(Email email) throws IOException, ClassNotFoundException {
    ArrayList<Email> emailsOf = retrieveMails(email);
    emailsOf.remove(email);
    updateMailboxOf(email, emailsOf);
  }

  public static synchronized void markAs(Email email, String head) throws IOException, ClassNotFoundException {
    ArrayList<Email> emails = retrieveMails(email);
    for (Email e : emails) {
      if (e.getId() == email.getId()) {
        e.setStato(head);
      }
    }
    // scrivere su file
    updateMailboxOf(email, emails);
  }
}
