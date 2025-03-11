package AppClient;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;

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
    panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

    JLabel lblNewLabel = new JLabel("Username:");
    lblNewLabel.setForeground(new Color(255, 255, 255));
    lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
    lblNewLabel.setBounds(12, 16, 100, 16);
    panel_1.add(lblNewLabel);

    panel_1.add(Box.createRigidArea(new Dimension(0, 7)));

    username = new JTextField();
    username.setBorder(new EmptyBorder(0, 0, 0, 0));
    username.setBounds(98, 13, 116, 25);
    panel_1.add(username);
    username.setColumns(10);

    panel_1.add(Box.createRigidArea(new Dimension(0, 7)));

    JLabel lblNewLabel_1 = new JLabel("Password:");
    lblNewLabel_1.setForeground(new Color(255, 255, 255));
    lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
    lblNewLabel_1.setBounds(12, 62, 100, 16);
    panel_1.add(lblNewLabel_1);
    panel_1.add(Box.createRigidArea(new Dimension(0, 7)));
    password = new JPasswordField();
    password.setBorder(new EmptyBorder(0, 0, 0, 0));
    password.setBounds(98, 59, 116, 22);
    panel_1.add(password);

    JButton loginBtn = new JButton("Login");
    loginBtn.setBackground(new Color(255, 152, 0));
    loginBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));
    loginBtn.setBorder(new EmptyBorder(7, 7, 7, 7));
    loginBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String name = username.getText();
          String pass = new String(password.getPassword());

          // Attempt to establish a connection with the database
          try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "")) {
            // Create a statement using the connection
            Statement stmt = con.createStatement();

            // Create a query to check if the user exists
            String userQuery = "SELECT * FROM login WHERE Username = ?";
            try (PreparedStatement userStatement = con.prepareStatement(userQuery)) {
              userStatement.setString(1, name);
              ResultSet userResult = userStatement.executeQuery();

              if (userResult.next()) { // If user exists
                if (pass.equals(userResult.getString("Password"))) {
                  // If the password is correct, proceed with login
                  System.out.println("Login successful!");

                  // Generate AES key for the user and update in the login table
                  AESCipher aesCipher = new AESCipher(name);
                  aesCipher.generateSecretKey(name);

                  // Proceed with your login logic
                  // For example, launch the main application
                  Main main = new Main(name);
                  main.setVisible(true);
                  main.names.add(name);

                  // Start a new thread to run some background tasks if needed
                  Thread newThread = new Thread(() -> {
                    try {
                      main.running(name);
                    } catch (IOException ex) {
                      ex.printStackTrace();
                    }
                  });
                  newThread.start();

                  // Hide the login window
                  setVisible(false);
                } else {
                  JOptionPane.showMessageDialog(null, "Incorrect Password");
                }
              } else {
                JOptionPane.showMessageDialog(null, "User does not exist!");
              }
            }
          }
        } catch (SQLException ex) {
          JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
      }
    });
    loginBtn.setBounds(92, 182, 97, 25);
    contentPane.add(loginBtn);

    JButton registerBtn = new JButton("New User");
    registerBtn.setBackground(new Color(255, 152, 0));
    registerBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));
    registerBtn.setBorder(new EmptyBorder(7, 7, 7, 7));
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
