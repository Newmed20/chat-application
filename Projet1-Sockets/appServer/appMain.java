package appServer;

import java.io.*;
import data.Data;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultListModel;

public class appMain{

  public static final int PORT = 59003;

  public static HashSet<String> names = new HashSet<String>();

  public static HashMap<String, Socket> clients = new HashMap<String, Socket>();

  public static HashMap<String, ObjectOutputStream> map = new HashMap<>();

  private static class Handler extends Thread{
    
    private Socket socket;
    
    private ObjectInputStream in;
    
    private ObjectOutputStream out;
    
    private ObjectOutputStream friendOut;
    
    public Handler(Socket socket) {
      this.socket = socket;
    }
    
    public void run() {
      String name;
      while(true) {
        try {
          out = new ObjectOutputStream(socket.getOutputStream());
          in = new ObjectInputStream(socket.getInputStream());
          while(true) {
            out.writeObject("SUBMIT");
            name = (String) in.readObject();
            if (name == null){
              return;
            }
            clients.put(name, socket);
            map.put(name, out);
            while(true) {
              out.writeObject("CONNECTED");
              String read = (String) in.readObject();
              if(read.equals("SUCCESS")) {
                break;
              }
            }
            break;
          }
          
          while(true) {
            try {
              in = new ObjectInputStream(socket.getInputStream());
              Object inputMsg = in.readObject();
              if(inputMsg == null) {
                continue;
              }
              System.out.println("TYPE = " + ((DefaultListModel)inputMsg).elementAt(0));
              checkMsgType((DefaultListModel<Object>) inputMsg, name);
            }
            catch(Exception e) {
              //TODO
            }
          }
        }
        catch(Exception e) {
          //TODO
        }
      }
    }
    
    private void checkMsgType(DefaultListModel input, String sender) {
      switch((int) input.elementAt(0)) {
        case RequestType.SEND_MSG:
          System.out.println("Send DETECTED");
          sendMessage(input, sender);
          break;
        case RequestType.SEND_FILE:
          System.out.println("File detected");
          sendFile(input, sender);
          break;
        case RequestType.LOGOUT:
          Logout(input, sender);
          break;
        default:
          break;
      }
    }
    
    private void sendFile(DefaultListModel input, String sender) {
      String user = (String) input.elementAt(2);
      Data data = (Data) input.elementAt(1);
      DefaultListModel<String> friendList = (DefaultListModel<String>) input.elementAt(3);
      for(int i = 0; i < friendList.size(); i++) {
        String friendName = friendList.elementAt(i);
        friendOut = map.get(friendName);
        if(user.equals(sender) && friendOut != null) {
          try {
            DefaultListModel model = new DefaultListModel();
            model.addElement(RequestType.SEND_FILE);
            model.addElement(data);
            model.addElement(sender);
            friendOut = new ObjectOutputStream((clients.get(friendName)).getOutputStream());
            friendOut.writeObject(model);
            friendOut.flush();
            System.out.println("Send file successful!");
          }
          catch (IOException e){
            //TODO
            System.out.println("FAILED TO SEND");
          }
        }
      }
    }
    
    private void sendMessage(DefaultListModel input, String sender) {
      String user = (String) input.elementAt(2);
      String message = (String) input.elementAt(1); 
      DefaultListModel<String> friendList = (DefaultListModel<String>) input.elementAt(3);
      for(int i = 0; i < friendList.size(); i++) {
        String friendName = friendList.elementAt(i);
        friendOut = map.get(friendName);
        if(user.equals(sender) && friendOut != null) {
          try {
            DefaultListModel model = new DefaultListModel();
            model.addElement(RequestType.SEND_MSG);
            model.addElement(message);
            model.addElement(sender);
            friendOut = new ObjectOutputStream((clients.get(friendName)).getOutputStream());
            friendOut.writeObject(model);
            friendOut.flush();
            System.out.println("Send message successful!");
          }
          catch (IOException e){
            //TODO
            System.out.println("FAILED TO SEND");
          }
        }
      }
    }
    
    private void Logout(DefaultListModel input, String sender) {
      System.out.println("Client\"" + sender +"\" just logged out!\r");
      Thread.currentThread().stop();
    }
    
  }

  public static void main(String[] args) {
    System.out.println("The server is running.....");
    ExecutorService threadPool = Executors.newFixedThreadPool(500);
    try(ServerSocket listener = new ServerSocket(PORT);){
      while(true) {
        new Handler (listener.accept()).start();
      }
    }
    catch(Exception e) {
      //TODO
    }
  }
  
}
