package AppClient;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import appServer.RequestType;
import appServer.appMain;

import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class RegisterUser extends JFrame {

  private JPanel contentPane;

  private JPanel panel;
  
  private JTextField username;
  
  private JTextField emailText;
  
  private JPasswordField password;

  public static void main(String[] args) {
  }

  public RegisterUser() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setBackground(new Color(44, 120, 101));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    panel = new JPanel();
    panel.setBounds(100,15,250,175);
    panel.setBackground(new Color(44, 120, 101));
    panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
    JLabel lblNewLabel = new JLabel("Choose your username:");
    lblNewLabel.setForeground(new Color(255,255,255));
    lblNewLabel.setFont(new Font("Tahoma",Font.PLAIN,18));
    lblNewLabel.setBounds(46, 62, 138, 16);
    panel.add(lblNewLabel);

    panel.add(Box.createRigidArea(new Dimension(0,7)));

    username = new JTextField();
    username.setBounds(245, 59, 116, 22);
    username.setBorder(new EmptyBorder(0,0,0,0));
    panel.add(username);
    username.setColumns(10);

    panel.add(Box.createRigidArea(new Dimension(0,7)));

    JLabel lblEnterYourEmail = new JLabel("Enter your email:");
    lblEnterYourEmail.setForeground(new Color(255,255,255));
    lblEnterYourEmail.setFont(new Font("Tahoma",Font.PLAIN,18));
    lblEnterYourEmail.setBounds(85, 111, 99, 16);
    panel.add(lblEnterYourEmail);

    panel.add(Box.createRigidArea(new Dimension(0,7)));

    emailText = new JTextField();
    emailText.setBounds(245, 108, 116, 22);
    emailText.setBorder(new EmptyBorder(0,0,0,0));
    panel.add(emailText);
    emailText.setColumns(10);

    panel.add(Box.createRigidArea(new Dimension(0,7)));

    JLabel lblPickAPassword = new JLabel("Pick a password:");
    lblPickAPassword.setForeground(new Color(255,255,255));
    lblPickAPassword.setFont(new Font("Tahoma",Font.PLAIN,18));
    lblPickAPassword.setBounds(86, 158, 98, 16);
    panel.add(lblPickAPassword);

    panel.add(Box.createRigidArea(new Dimension(0,7)));

    password = new JPasswordField();
    password.setBounds(245, 155, 116, 22);
    password.setBorder(new EmptyBorder(0,0,0,0));
    panel.add(password);

    panel.add(Box.createRigidArea(new Dimension(0,7)));

    contentPane.add(panel);
    
    JButton registerBtn = new JButton("Register");
    registerBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String name = username.getText();
        if(!appMain.names.contains(name)) {
          try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
            Statement stmt = con.createStatement();

            String pass = new String (password.getPassword());
            String email = emailText.getText();
            String qry = "insert into login values ('"+name+"', '"+pass+"', '"+email+"');";
            stmt.executeUpdate(qry);
            String sql = "create table " + name + "(FriendName varchar(20) primary key, Socket varchar(255));";
            stmt.executeUpdate(sql);
            qry = "insert into " + name + "(FriendName) values ('"+name+"');";
            stmt.executeUpdate(qry);
            JOptionPane.showMessageDialog(null, "New User Registered!");
            appMain.names.add(name);
            LoginApp login = new LoginApp();
            login.setVisible(true);
            dispose();
          }
          catch(Exception e) {
            JOptionPane.showMessageDialog(null, "No Connection");
          }
        }else {
          JOptionPane.showMessageDialog(null, "User already exists!");
        }
      }
    });
    registerBtn.setBounds(151, 204, 97, 25);
    registerBtn.setBackground(new Color(255, 152, 0));
    registerBtn.setFont(new Font("Tahoma",Font.PLAIN,18));
    registerBtn.setBorder(new EmptyBorder(7,7,7,7));
    contentPane.add(registerBtn);
  }
}
