package AppClient;

import java.awt.*;

import data.Data;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import appServer.RequestType;
import appServer.appMain;

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

  JPanel panel_4;
  JPanel panel_5;
  JPanel panel_6;
  
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
          this.setTitle("WeChat: " + name);
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
            JPanel jp=new JPanel();
            JLabel jl=new JLabel(sender + ": " + message + "\n");
            //jp.setBounds(205,5,350,40);
            jp.setMaximumSize(new Dimension(350, 40));
            jl.setVisible(true);
            jp.setOpaque(true);
            if(sender.equals(user)) {
              jp.setBackground(Color.gray);
              jp.add(jl);
              panel_6.add(jp);
              panel_6.add(Box.createRigidArea(new Dimension(0, 10)));
              panel_5.add(Box.createRigidArea(new Dimension(0,40)));
            }else {
              jp.setBackground(Color.GREEN);
              jp.add(jl);
              panel_5.add(jp);
              panel_5.add(Box.createRigidArea(new Dimension(0, 10)));
              panel_6.add(Box.createRigidArea(new Dimension(0,40)));
            }
            panel_6.revalidate();
            panel_6.repaint();
            panel_5.revalidate();
            panel_5.repaint();
            panel_4.revalidate();
            panel_4.repaint();
            System.out.println("added");
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
              JPanel jp=new JPanel();
              JLabel jl=new JLabel(sender + ": " + f.getAbsolutePath() + " File Saved.\n");
              //jp.setBounds(205,5,350,40);
              jp.setMaximumSize(new Dimension(350, 40));
              jl.setVisible(true);
              jp.setOpaque(true);
              if(sender.equals(user)) {
                jp.setBackground(Color.gray);
                jp.add(jl);
                panel_6.add(jp);
                panel_6.add(Box.createRigidArea(new Dimension(0, 10)));
                panel_5.add(Box.createRigidArea(new Dimension(0,40)));
              }else {
                jp.setBackground(Color.GREEN);
                jp.add(jl);
                panel_5.add(jp);
                panel_5.add(Box.createRigidArea(new Dimension(0, 10)));
                panel_6.add(Box.createRigidArea(new Dimension(0,40)));
              }
              panel_6.revalidate();
              panel_6.repaint();
              panel_5.revalidate();
              panel_5.repaint();
              panel_4.revalidate();
              panel_4.repaint();
              System.out.println("added");
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


  public static void main(String[] args) throws Exception{
      LoginApp login = new LoginApp();
      login.setVisible(true);
  }

  /**
   * Create the frame.
   * @param username 
   */
  public Main(String username) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 726, 524);
    contentPane = new JPanel();
    contentPane.setBackground(new Color(144, 210, 109));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    
    JPanel panel = new JPanel();
    panel.setBounds(0, 0, 203, 520);
    panel.setBackground(new Color(255, 120, 101));
    contentPane.add(panel);
    panel.setLayout(null);
    
    list = refreshList(username);
    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        input.setEditable(true);
      }
    });
    list.setBounds(12, 50, 179, 350);
    list.setFont(new Font("Tahoma",0,17));
    panel.add(list);
    
    add = new JTextField();
    add.setBounds(12, 420, 125, 22);
    add.setBorder(new EmptyBorder(0,0,0,0));
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
    addfriend.setBounds(137, 420, 41, 22);
    addfriend.setBackground(new Color(255, 152, 0));
    addfriend.setBorder(new EmptyBorder(0,0,0,0));
    addfriend.setFont(new Font("Tahoma",0,15));
    panel.setBackground(new Color(	44, 120, 101));
    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
    panel.add(addfriend);
    
    JPanel panel_3 = new JPanel();
    panel_3.setBounds(215, 404, 481, 60);
    panel_3.setBackground(new Color(144, 210, 109));
    contentPane.add(panel_3);
    panel_3.setLayout(null);
    
    input = new JTextField();
    input.setBounds(12, 13, 300, 34);
    input.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55),2));
    panel_3.add(input);
    input.setColumns(10);
    input.setEditable(false);
    
    sendBtn = new JButton("Send");
    sendBtn.setBackground(new Color(251, 109, 72));
    sendBtn.setBorder(new EmptyBorder(0,0,0,0));
    sendBtn.setFont(new Font("Tahoma",0,15));
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
    filebtn.setBackground(new Color(255, 152, 0));
    filebtn.setBorder(new EmptyBorder(0,0,0,0));
    filebtn.setFont(new Font("Tahoma",0,15));
    filebtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        openFile(arg0);
      }
    });
    filebtn.setBounds(318, 13, 67, 34);
    panel_3.add(filebtn);

    panel_4=new JPanel();
    panel_5=new JPanel();
    panel_6=new JPanel();
    panel_4.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55),4));
    panel_4.setBounds(215,50,481,350);
    panel_4.setLayout(new BorderLayout());
    panel_5.setLayout(new BoxLayout(panel_5,BoxLayout.Y_AXIS));
    panel_6.setLayout(new BoxLayout(panel_6,BoxLayout.Y_AXIS));
    panel_4.add(panel_5,BorderLayout.WEST);
    panel_4.add(panel_6,BorderLayout.EAST);
    contentPane.add(panel_4);
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
