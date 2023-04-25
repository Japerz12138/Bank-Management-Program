package com.nyit.japerz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.nyit.japerz.utils.HashingUtils;


public class Login extends JFrame{
    private JPanel panel1;
    private JTextField usernameTF;
    private JComboBox loginTypeCB;
    private JButton loginButton;
    private JPasswordField passwordPTF;
    private JButton newCustomerRegisterButton;

    //Set pass through to other classes.
    private static String usernamePT;
    private static String passwordPT;

    static DefaultComboBoxModel<String> loginTypeCBModel = new DefaultComboBoxModel<String>();

    public Login() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Login");
        setSize(650,400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        setLookAndFeel();

        loginTypeCBModel.removeAllElements();
        loginTypeCBModel.addElement("Customers");
        loginTypeCBModel.addElement("Employee");
        loginTypeCBModel.addElement("Admin");

        // Set the loginTypeCBModel as the model for the loginTypeCB ComboBox
        loginTypeCB.setModel(loginTypeCBModel);

        //Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        //Register Button
        newCustomerRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register register = new Register();
                dispose();
            }
        });

        //Enter key detection
        passwordPTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    //Connects to database and start initialize the window
    public static void main(String[] args) {
        Database.connect();
        setupClosingDBConnection();
        Login login = new Login();
    }

    //Set the window looks modern on Windows
    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Main Login Function
    private void performLogin() {

        //get texts from text field
        String username = usernameTF.getText();
        usernamePT = username;
        String password = new String(passwordPTF.getPassword());
        String selectedLoginType = loginTypeCB.getSelectedItem().toString();
        String tableName = "";
        String columnName = "";

        //Check the selection in combo box
        if (selectedLoginType.equals("Customers")){
            tableName = "customers";
            columnName = tableName;
        } else if (selectedLoginType.equals("Employee")){
            tableName = "employees";
            columnName = tableName;
        } else if (selectedLoginType.equals("Admin")){
            tableName = "admins";
            columnName = tableName;
        } else {
            System.out.println("Eh...Nothing is selected???? HOW?");
        }

        //Check the database username for login
        String sql = "SELECT * FROM bank_db." + tableName + " WHERE " + columnName + "_username = ?";

        try (PreparedStatement statement = Database.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String hashedPasswordFromDatabase = rs.getString(columnName + "_password");
                //Encrypt the password to SHA256 foe later searching in database
                String hashedPasswordEntered = HashingUtils.sha256(password);
                passwordPT = hashedPasswordEntered;

                if (hashedPasswordFromDatabase.equals(hashedPasswordEntered)) {
                    // Login successful
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    System.out.println("[INFO] User " + username + " logged in! Password correct and encrypted.");
                    if (selectedLoginType.equals("Customers")){
                        CustomerPortal cp = new CustomerPortal();
                        dispose();
                    } else if (selectedLoginType.equals("Employee")) {
                        //TODO: Finish the EmployeePortal and switch window here!
                    } else if (selectedLoginType.equals("Admin")) {
                        //TODO: Finish the AdminPortal and switch window here!
                    } else {
                        JOptionPane.showMessageDialog(null, "Something is wrong! Please contact a bank employee! Error code: L-001");
                    }
                } else {
                    // Incorrect password
                    JOptionPane.showMessageDialog(null, "Incorrect password!");
                }
            } else {
                // User does not exist
                JOptionPane.showMessageDialog(null, "User does not exist!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    //Setup Closing Database Connection Function
    public static void setupClosingDBConnection() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try { Database.connection.close(); System.out.println("[INFO] Application Closed - DB Connection Closed");
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }, "Shutdown-thread"));
    }

    //Get the username pass through
    public static String getUsernamePT() {
        return usernamePT;
    }

    //Get the password after hashing operation pass through
    public static String getPasswordPT() {
        return passwordPT;
    }
}
