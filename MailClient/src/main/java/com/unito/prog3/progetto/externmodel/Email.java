package com.unito.prog3.progetto.externmodel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents an email
 */
public class Email implements Serializable {
    private static final long serialVersionUID=1L;
    private long id;
    private String sender;
    private List<String> receivers;
    private String object;
    private String text;
    private String date;
    private String state;

    /**
     * @param id: the email id
     * @param sender: the email sender
     * @param receivers: the email receivers list
     * @param object: the email object
     * @param text: the email text
     * @param date: the email sending date
     */
    public Email(long id, String sender, List<String> receivers, String object, String text, Date date) {
        this.id = id;
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(date);
        this.state = "not_seen";
    }

    public Email(String sender) {
        this.id = -1;
        this.sender = sender;
        this.receivers = null;
        this.object = "";
        this.text = "";
        this.date = null;
        this.state = "no_email";
    }

    public String getState() {
        return state;
    }

    public long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getObject() {
        return object;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * @param o: the email to compare
     * @return true if the two emails have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email nE = (Email) o;
        return this.getId() == nE.getId();
    }

    /**
     * @return a string of the sender email and the list of receivers email
     */
    @Override
    public String toString() {
        return String.join(" - ", List.of(this.sender,this.object));
    }
}