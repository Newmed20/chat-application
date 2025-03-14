package AppClient;

import java.awt.BorderLayout;
import data.Data;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import appServer.RequestType;
import appServer.appMain;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class Main extends JFrame {
  
  private static final String serverAddress = "127.0.0.1";
  
  public static final int PORT = 59003;
  
  public ArrayList<String> names = new ArrayList<>();
  
  public String user;
  
  private Socket socket;
  
  private ObjectInputStream in;
  
  private ObjectOutputStream out;
  
  private JPanel contentPane;
  
  private JTextField input;
  
  private JTextField add;
  
  JButton sendBtn;
  
  JButton filebtn;  
  
  JList list;
  
  JTextArea textArea;
  
  public String getName() {
    return user;
}
  
  public String getSocket(String username) {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
      Statement stmt = con.createStatement();
      String qry = "select Socket from " + username + " where FriendName="+" '"+username+"';";
      ResultSet rs = stmt.executeQuery(qry);
      if(rs.next()) {
        return rs.getString("Socket");
      }
    } catch(Exception ex) {
      //TODO
    }
    return null;
  }
  
  public void running() throws IOException{
    this.user = this.names.get(0);
    try {
      socket = new Socket(serverAddress, PORT);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
      
      while(true) {
        String line = (String)in.readObject();
        if(line.startsWith("SUBMIT")) {
          String name = getName();
          out.writeObject(name);
          this.setTitle("Multi-purpose chat: " + name);
        }
        else if(line.startsWith("CONNECTED")) {
          try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
            Statement stmt = con.createStatement();
            String qry = "update " + user + " set Socket = '"+socket+"' where FriendName = '"+user+"';";
            stmt.executeUpdate(qry);
            System.out.println("This is the socket: " + socket);
            out.writeObject("SUCCESS");
            break;
          }catch(Exception e) {
            //TODO
          }
        }
      }
      while(true) {
        try {
          in = new ObjectInputStream(socket.getInputStream());
          System.out.println("Second loop");
          DefaultListModel input = (DefaultListModel)in.readObject();
          if(input == null) {
            continue;
          }
          System.out.println("ACCEPTED");
          int type = (int) input.elementAt(0);
          if(type == RequestType.SEND_MSG) {
            String message = (String) input.elementAt(1);
            String sender = (String) input.elementAt(2);
            textArea.append(sender + ": " + message + "\n");
          }
          else if(type == RequestType.SEND_FILE) {
            Data data = (Data) input.elementAt(1);
            String sender = (String) input.elementAt(2);
            JFileChooser choose = new JFileChooser();
            int c = choose.showSaveDialog(null);
            if(c == JFileChooser.APPROVE_OPTION) {
              byte[] b = data.getFile();
              File f = new File(choose.getSelectedFile().getPath() +  data.getName().substring(data.getName().indexOf(".")));
              System.out.println(f.getAbsolutePath());
              FileOutputStream outFile = new FileOutputStream(f);
              outFile.write(b);
              outFile.close();
              textArea.append(sender + ": " + f.getAbsolutePath() + " File Saved.\n");
            }
          }
        }
        catch(Exception ex) {
          //TODO
        }
      }
    } catch (ClassNotFoundException e) {
    } finally {
    }
  }

  /**
   * Launch the application.
   */
  public static void main(String[] args) throws Exception{
    /*
    if(args.length == 0) {
      System.out.print("Pass the run keyword as the sole command line arguement.");
      return;
    }
    if(args.length ==1 && args[0].equalsIgnoreCase("run")) {

     */
      LoginApp login = new LoginApp();
      login.setVisible(true);
      /*
    }
    */

  }

  /**
   * Create the frame.
   * @param username 
   */
  public Main(String username) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 726, 524);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    
    JPanel panel = new JPanel();
    panel.setBounds(0, 0, 203, 477);
    contentPane.add(panel);
    panel.setLayout(null);
    
    JPanel panel_2 = new JPanel();
    panel_2.setBounds(0, 0, 203, 139);
    panel.add(panel_2);
    panel_2.setLayout(null);
    
    list = refreshList(username);
    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        input.setEditable(true);
      }
    });
    list.setBounds(12, 177, 179, 240);
    panel.add(list);
    
    add = new JTextField();
    add.setBounds(12, 442, 125, 22);
    panel.add(add);
    add.setColumns(10);
    
    JButton addfriend = new JButton("+");
    addfriend.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String friendToAdd = add.getText();
        try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
          Statement stmt = con.createStatement();
          String qry = "select * from login where Username="+" '"+friendToAdd+"';";
          ResultSet rs = stmt.executeQuery(qry);
          if(rs.next()) {
            qry = "insert into " + user + " values('"+friendToAdd+"', '"+getSocket(friendToAdd)+"');";
            stmt.executeUpdate(qry);
            JOptionPane.showMessageDialog(null, "Friend added!");
            add.setText("");
          }
          else {
            JOptionPane.showMessageDialog(null,"User does not exist.");
          }
        }
        catch(Exception e) {
          //TODO
        }
        list = refreshList(user);
      }
    });
    addfriend.setBounds(150, 441, 41, 25);
    panel.add(addfriend);
    
    JPanel panel_1 = new JPanel();
    panel_1.setBounds(203, 0, 505, 60);
    contentPane.add(panel_1);
    panel_1.setLayout(null);
    
    JButton callBtn = new JButton("Call");
    callBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        
      }
    });
    callBtn.setBounds(427, 13, 66, 34);
    panel_1.add(callBtn);
    
    JPanel panel_3 = new JPanel();
    panel_3.setBounds(215, 404, 481, 60);
    contentPane.add(panel_3);
    panel_3.setLayout(null);
    
    input = new JTextField();
    input.setBounds(12, 13, 284, 34);
    panel_3.add(input);
    input.setColumns(10);
    input.setEditable(false);
    
    sendBtn = new JButton("Send");
    sendBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
          try {
            out = new ObjectOutputStream(socket.getOutputStream());
            DefaultListModel sendModel = new DefaultListModel();
            sendModel.addElement(RequestType.SEND_MSG);
            sendModel.addElement(input.getText());
            sendModel.addElement(user);
            DefaultListModel<String> friends = new DefaultListModel<String>();
            friends.addElement((String)list.getSelectedValue());
            friends.addElement(user);
            sendModel.addElement(friends);
            out.writeObject(sendModel);
            input.setText("");
          } catch (IOException e1) {
            // TODO 
            System.out.println("FAILED");
          }
        }
    });
    sendBtn.setBounds(397, 13, 72, 34);
    panel_3.add(sendBtn);
    
    filebtn = new JButton("File");
    filebtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        openFile(arg0);
      }
    });
    filebtn.setBounds(318, 13, 67, 34);
    panel_3.add(filebtn);
    
    textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setBounds(215, 73, 481, 318);
    contentPane.add(textArea);
    
  }
  
  private void openFile(ActionEvent evt) {
    try {
      out = new ObjectOutputStream(socket.getOutputStream());
      JFileChooser choose = new JFileChooser();
      int c = choose.showOpenDialog(this);
      if(c == JFileChooser.APPROVE_OPTION) {
        File f = choose.getSelectedFile();
        FileInputStream inFile = new FileInputStream(f);
        byte b[] = new byte[inFile.available()];
        inFile.read(b);
        Data data = new Data();
        data.setStatus("File");//TODO
        data.setName(f.getName());
        data.setFile(b);
        DefaultListModel fileModel = new DefaultListModel();
        fileModel.addElement(RequestType.SEND_FILE);
        fileModel.addElement(data);
        fileModel.addElement(user);
        DefaultListModel<String> friends = new DefaultListModel<String>();
        friends.addElement((String)list.getSelectedValue());
        fileModel.addElement(friends);
        out.writeObject(fileModel);
      }
    }
    catch(Exception e) {
      //TODO
    }
  }
  
  public JList refreshList(String username) {
    DefaultListModel listModel = new DefaultListModel();
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
      Statement stmt = con.createStatement();
      String qry = "select * from " + username;
      ResultSet rs = stmt.executeQuery(qry);
      while(rs.next()) {
        String friend = rs.getString("FriendName");
        if(!friend.equals(username)) {
          listModel.addElement(friend);
        }
      }
      list.setModel(listModel);
    }catch(Exception e) {
      //TODO
    }
    return new JList(listModel);
  }
}
