package com.project.model;

import com.project.view.ServerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailService extends Thread{
  private int port;
  private Client c;
  private ServerController guiController;

  private final int CORE_MACHINES=16; //number of cores

  public MailService(int port, ServerController guiController) {
    this.port = port;
    this.c = c;
    this.guiController = guiController;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public Client getC() {
    return c;
  }

  public void setC(Client c) {
    this.c = c;
  }

  @Override
  public void run() {
    System.out.println("Server locahols in port " + this.port + "... \n");
    this.service();
  }

  public void service() {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      ExecutorService executorService= Executors.newFixedThreadPool(CORE_MACHINES);
      while (true) {
        Socket client = serverSocket.accept();
        Runnable task=new ClientWorker(client, guiController);
        executorService.execute(task);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
