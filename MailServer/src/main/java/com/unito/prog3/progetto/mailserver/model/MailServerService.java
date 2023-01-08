package com.unito.prog3.progetto.mailserver.model;

import com.unito.prog3.progetto.mailserver.controller.ServerGuiController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MailServerService extends Thread{
  private int port;
  private ServerGuiController guiController;
  ExecutorService executorService;
  ServerSocket serverSocket;
  private boolean isServiceOn = true;

  private final int CORE_MACHINES = 16; //number of cores

  public MailServerService(int port, ServerGuiController guiController) {
    this.port = port;
    this.guiController = guiController;
    serverSocket = null;
    // hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shutdown server...");
      // eseguirà la close delle socket
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }));
  }

  @Override
  public void run() {
    System.out.println("Server ready. Port: " + this.port + "\n");
    this.service();
  }

  public void service() {
    try {
      serverSocket = new ServerSocket(port);
      executorService= Executors.newFixedThreadPool(CORE_MACHINES);
      while (isServiceOn) {
        Socket client = serverSocket.accept();
        System.out.println("Accepted: " + client);
        Runnable task = new MailServerClientWorker(client, guiController);
        executorService.execute(task);
      }
    } catch (SocketException socketException) {
      System.out.println("Server GUI close, now I close the server socket");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void shutdownMailServerService() {
    if (executorService != null) {
      try {
        if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow(); // Cancel currently executing tasks
          if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS))
            System.err.println("Pool did not terminate");
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
      }
    }
  }

  public void guiIsClosing() {
    this.isServiceOn = false;
    try {
      this.serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.shutdownMailServerService();
    if (!this.isInterrupted())  {
      this.interrupt();
    }
  }
}
