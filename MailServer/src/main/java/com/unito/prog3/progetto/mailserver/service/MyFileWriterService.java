package com.unito.prog3.progetto.mailserver.service;

import com.unito.prog3.progetto.model.Email;

import java.io.*;
import java.util.ArrayList;

public class MyFileWriterService {

  public static ArrayList<Email> retreiveMails(Email email) throws IOException, ClassNotFoundException {
    File mailbox = new File("database/".concat(email.getSender().concat(".dat")));
    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(mailbox));
    return (ArrayList<Email>) objectInputStream.readObject();
  }

  public static void createMailbox(Email email) throws IOException {
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
      System.out.println("La mailbox di " + email.getSender() + " esiste!");
    }
  }

  public static synchronized ArrayList<String> writeEmail(Email email) throws IOException, ClassNotFoundException {
    ArrayList<String> destinatorsNotFound = new ArrayList<>();
    for (String s: email.getReceivers()) {
      // recupero la inbox del receicer
      File mailbox = new File("database/".concat(s.concat(".dat")));
      //
      if (!mailbox.exists()) {
        destinatorsNotFound.add(s);
      } else {
        //
        ArrayList<Email> emailArrayList = retreiveMails(email);
        emailArrayList.add(email);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(mailbox));
        objectOutputStream.writeObject(emailArrayList);
        objectOutputStream.flush();
        objectOutputStream.close();
      }
    }
    return destinatorsNotFound;
  }
}
