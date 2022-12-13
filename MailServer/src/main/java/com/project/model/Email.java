package com.project.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents a email
 */

public class Email implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String sender;
    private List<String> receivers;
    private String object;
    private String text;
    private String date;

    /**
     *
     * @param id the id of the email
     * @param sender the sender email
     * @param receivers the list of receivers email
     * @param object the object of the email
     * @param text the text of the email
     * @param date the forwarding date of the email
     */

    public Email(long id, String sender, List<String> receivers, String object, String text, Date date) {
        this.id = id;
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.date = new SimpleDateFormat("dd/MM/yyyy").format(date);
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

    /**
     *
     * @return a string of the sender email and the list of receivers email
     */
    @Override
    public String toString() {
        return String.join(" - ", List.of(this.sender,this.object));
    }
}
