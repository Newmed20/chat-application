package AppClient;

import java.awt.*;
import java.sql.*;
import appServer.RequestType;
import appServer.appMain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class LoginApp extends JFrame {

  private JPanel contentPane;

  private JTextField username;

  private JPasswordField password;

  public String user;

  public static void main(String[] args) {
  }

  public LoginApp() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setBackground(new Color(44, 120, 101));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    JPanel panel_1 = new JPanel();
    panel_1.setBounds(100, 40, 200, 120);
    panel_1.setBackground(new Color(44, 120, 101));
    contentPane.add(panel_1);
    panel_1.setLayout(new BoxLayout(panel_1,BoxLayout.Y_AXIS));

    JLabel lblNewLabel = new JLabel("Username:");
    lblNewLabel.setForeground(new Color(255,255,255));
    lblNewLabel.setFont(new Font("Tahoma",Font.PLAIN,18));
    lblNewLabel.setBounds(12, 16, 100, 16);
    panel_1.add(lblNewLabel);

    panel_1.add(Box.createRigidArea(new Dimension(0, 7)));

    username = new JTextField();
    username.setBorder(new EmptyBorder(0,0,0,0));
    username.setBounds(98, 13, 116, 25);
    panel_1.add(username);
    username.setColumns(10);

    panel_1.add(Box.createRigidArea(new Dimension(0, 7)));

    JLabel lblNewLabel_1 = new JLabel("Password:");
    lblNewLabel_1.setForeground(new Color(255,255,255));
    lblNewLabel_1.setFont(new Font("Tahoma",Font.PLAIN,18));
    lblNewLabel_1.setBounds(12, 62, 100, 16);
    panel_1.add(lblNewLabel_1);
    panel_1.add(Box.createRigidArea(new Dimension(0, 7)));
    password = new JPasswordField();
    password.setBorder(new EmptyBorder(0,0,0,0));
    password.setBounds(98, 59, 116, 22);
    panel_1.add(password);


    JButton loginBtn = new JButton("Login");
    loginBtn.setBackground(new Color(255, 152, 0));
    loginBtn.setFont(new Font("Tahoma",Font.PLAIN,18));
    loginBtn.setBorder(new EmptyBorder(7,7,7,7));
    loginBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          //Create a JDBC connection with the database.
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
          //Creates a statement using the connection
          System.out.println("here 1");
          Statement stmt = con.createStatement();
          String name = username.getText();
          String pass = new String(password.getPassword());
          //Create a String which is the query to be run in SQL
          String qry = "select * from login where Username="+" '"+name+"';";
          //Stores the returned value of the query in the ResultSet
          ResultSet rs = stmt.executeQuery(qry);
          System.out.println("here 2");
          //Checks if the ResultSet has values stored for the given query
          if(rs.next()) {
            //If the password and username match, it launches the main application.
            if(pass.equals(rs.getString("Password"))) {
              System.out.println(" here 3");
              Main main = new Main(name);
              main.setVisible(true);
              main.names.add(name);
              //Creates a new Thread to keep the running() method always running
              Thread newThread = new Thread(new Runnable() {
                @Override
                public void run() {
                  try {
                    main.running();
                  } catch (IOException e) {
                    // TODO
                  }
                }
              });
              newThread.start();
              setVisible(false);
            }
            else {
              JOptionPane.showMessageDialog(null, "Incorrect Password");
            }
          }
          else {
            JOptionPane.showMessageDialog(null, "User does not exist!");
          }
        }
        catch(Exception ew) {
          JOptionPane.showMessageDialog(null, "No connection!"+ew.getMessage());
        }
      }
    });
    loginBtn.setBounds(92, 182, 97, 25);
    contentPane.add(loginBtn);

    JButton registerBtn = new JButton("New User");
    registerBtn.setBackground(new Color(255, 152, 0));
    registerBtn.setFont(new Font("Tahoma",Font.PLAIN,18));
    registerBtn.setBorder(new EmptyBorder(7,7,7,7));
    registerBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setVisible(true);
        dispose();
      }
    });
    registerBtn.setBounds(225, 182, 97, 25);
    contentPane.add(registerBtn);
  }


}
