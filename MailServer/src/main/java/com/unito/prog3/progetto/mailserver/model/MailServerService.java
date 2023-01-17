package com.unito.prog3.progetto.mailserver.model;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;
import com.unito.prog3.progetto.mailserver.service.MyFileWriterService;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Merico Michele, Montesi Dennis, Turcan Boris
 * Represents the Server service
 */
public class MailServerService extends Thread{
  private final int port;
  private final ServerGuiController guiController;
  ExecutorService executorService;
  ServerSocket serverSocket;
  MyFileWriterService writerService;

  /*A boolean variable that will be true when the server is up, false otherwise*/
  private boolean isServiceOn = true;

  private final int CORE_MACHINES = 16; //number of cores

  private ObservableList<String> userList; // observable list of active users

  private SimpleListProperty<String> userListProperty; // property of userList

  /**
   * This method is the constructor of MailServerService.
   * @param port: required to listen to the server on the specific port.
   * @param guiController: used to pass it to MailClientWorker that otherwise could not use it.
   */
  public MailServerService(int port, ServerGuiController guiController) {
    this.port = port;
    this.guiController = guiController;
    this.serverSocket = null;
    this.userListProperty = new SimpleListProperty<>();
    this.userList = FXCollections.observableArrayList(new LinkedList<>());
    this.userListProperty.set(userList);
    this.writerService = new MyFileWriterService();
  }

  /**
   * @return the user list property
   */
  public SimpleListProperty<String> getUserList() {
    return this.userListProperty;
  }

  /**
   * This method overrides a method declaration in a supertype and invokes service method.
   */
  @Override
  public void run() {
    System.out.println("Server ready. Port: " + this.port );
    try {
      this.service();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  /**
   * This method is the heart of the server as it creates a thread pool
   * and puts the server listening on the port to accept new connections.
   */
  public void service() throws IOException {
    try {
      // creates new server socket...
      serverSocket = new ServerSocket(port);
      // creates a new executor of a fixed thread pool...
      executorService = Executors.newFixedThreadPool(CORE_MACHINES);
      // if service is on...
      while (isServiceOn) {
        // ...accept new client...
        Socket client = serverSocket.accept();
        System.out.println("Accept: " + client);
        // ...then creates new MailWorker that will execute the request
        Runnable task = new MailServerClientWorker(client, guiController, writerService);
        // next line will call start() method on task that will call run()
        executorService.execute(task);
      }
    } catch (SocketException ignored) {
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is invoked to shut down the executor only after the still active tasks are terminated.
   */
  public void shutdownMailServerService() {
    if (executorService != null) {
      // executor will not accept other tasks, it will terminate just the still active ones
      executorService.shutdown();
      try {
        // waits 500 ms that active tasks terminate, if some task not terminate executes shutdown
        if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }

  /**
   * This method is invoked when the GUI is closing and logs out the Server correctly.
   */
  public void guiIsClosing() {
    this.isServiceOn = false;
    try {
      this.serverSocket.close();
      System.out.println("ServerSocket close.");
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.shutdownMailServerService();
    if (!this.isInterrupted())  {
      this.interrupt();
    }
    System.out.println("Shutdown server done. Bye!");
  }

}
