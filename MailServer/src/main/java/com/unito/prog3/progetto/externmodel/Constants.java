package com.unito.prog3.progetto.externmodel;

public class Constants {
  /**
   * Connection and application constants
   */
  public static int MAIL_SERVER_PORT = 6789;
  public static String MY_EMAIL_ADDRESS = "michele.merico@gmail.com";

  /**
   * Possible email operations that can be done by user
   */
  public static String NEW_EMAIL = "New Email";
  public static String EMAIL_SEEN = "Seen";
  public static String EMAIL_RECEIVED_NOT_SEEN = "Received Not Seen";
  public static String FORWARD_EMAIL = "Forward Email";
  public static String REPLY_EMAIL = "Reply Email";
  public static String REPLY_ALL_EMAIL = "Reply Email To All";

  /**
   * Possible different headers of a message
   */
  public static String CONNECTION_REQUEST = "Connection Request";
  public static String REQUEST_NEW_EMAILS = "New Emails Request";
  public static String REQUEST_MARK_EMAIL_AS_SEEN = "Mark Email As Seen Request";
  public static String CONNECTION_CLOSED = "Disconnection Request";
  public static String DELETE_EMAIL_BY_ID = "Delete Request";
}
