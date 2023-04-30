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

public class OpenAccount extends JFrame{
    private JButton acceptButton;
    private JButton canelButton;
    private JTextArea byClickingTheAgreeTextArea;
    private JPanel panel1;

    public OpenAccount() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Open Account Agreement");
        setSize(500,600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        byClickingTheAgreeTextArea.setEditable(false);
        byClickingTheAgreeTextArea.setCaretPosition(0);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAccountReg();
            }
        });

        canelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    private void performAccountReg() {
        String username = Login.getUsernamePT();
        try {
            Connection connection = Database.connection; // Connect to database
            Statement stm = connection.createStatement();

            long newAccountNumber = generateAccountNumber();

            // Insert a new record into the accounts table with auto-incremented account_id
            String sql = "INSERT INTO accounts (account_number, customers_id, balance, is_active) VALUES " + "("+newAccountNumber+", (SELECT customers_id FROM customers WHERE customers_username = '"+username+"'), 0, false)";
            stm.executeUpdate(sql);

            JOptionPane.showMessageDialog(null, "Account created successfully and waiting for approval. \n Your account number is: " + newAccountNumber);
            dispose();

        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static long generateAccountNumber() {
        long accountNumber = 0;
        do {
            accountNumber = (long) (Math.random() * 900000000000L) + 100000000000L;
        } while (isAccountNumberExists(accountNumber));
        return accountNumber;
    }

    public static boolean isAccountNumberExists(long accountNumber) {
        try {
            Connection connection = Database.connection;
            Statement stm = connection.createStatement();
            String sql = "SELECT account_number FROM accounts WHERE account_number = " + accountNumber;
            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
