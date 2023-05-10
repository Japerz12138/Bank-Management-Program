package com.nyit.japerz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminPortal extends JFrame{
    private JButton logoutButton;
    private JButton createEmployeeButton;
    private JButton manageAccountsButton;
    private JButton manageBankAccountsButton;
    private JButton settingsButton;
    private JLabel header_Greating;
    private JLabel header_CustomerName;
    private JPanel panel1;

    public AdminPortal() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Admin Portal - " + Login.getUsernamePT());
        setSize(800, 550);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        setGreeting();

        String customerName = getCustomerName();
        if (customerName != null) {
            header_CustomerName.setText(customerName);
        } else {
            header_CustomerName.setText("ERROR");
        }

        //Logout function
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to log out?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "You have successfully signed out!");
                    Login login = new Login();
                    dispose();
                }
            }
        });
        createEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateEmployee ce = new CreateEmployee();
            }
        });
        manageAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminManageAccounts ama = new AdminManageAccounts();
            }
        });
        manageBankAccountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AccountList al = new AccountList();
            }
        });
    }

    //Function to use username and password to get the user's legal name.
    public String getCustomerName() {
        String username = Login.getUsernamePT();
        String password = Login.getPasswordPT();

        try {
            Connection connection = Database.connection; // Connect to database
            Statement stm = connection.createStatement();

            ResultSet rs = stm.executeQuery("SELECT admins_name FROM admins WHERE admins_name = '" + username + "' AND admins_password = '" + password + "'");

            if (rs.next()) {
                return rs.getString("admins_name");
            } else {
                JOptionPane.showMessageDialog(null, "Something went wrong! Check database! Error Code: CP-001");
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    //Set the greeting on the header
    private void setGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 0 && hour < 12) {
            header_Greating.setText("Good morning! ");
        } else {
            header_Greating.setText("Good afternoon! ");
        }
    }
}
