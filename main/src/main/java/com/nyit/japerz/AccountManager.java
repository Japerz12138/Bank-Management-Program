package com.nyit.japerz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountManager extends JFrame{
    private JPanel panel1;
    private JButton createAnAccountButton;
    private JList accountList;
    private JButton refreshButton;

    public AccountManager() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Account Manager");
        setSize(650,400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        performAccountSearch();

        createAnAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenAccount oa = new OpenAccount();
            }
        });
        refreshButton.addComponentListener(new ComponentAdapter() {
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAccountSearch();
            }
        });
    }

    private void performAccountSearch() {
        try {
            // Get the current customer ID
            String username = Login.getUsernamePT();
            int customerID = -1;
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT customers_id FROM customers WHERE customers_username = '" + username + "'");
            if (resultSet.next()) {
                customerID = resultSet.getInt(1);
            }

            // Search for accounts related to the customer ID
            DefaultListModel<String> model = new DefaultListModel<>();
            ResultSet accountResultSet = statement.executeQuery(
                    "SELECT account_number, employees_id, balance, is_active FROM accounts WHERE customers_id = " + customerID);
            while (accountResultSet.next()) {
                long accountNumber = accountResultSet.getLong("account_number");
                Integer employeeID = accountResultSet.getInt("employees_id"); // Use Integer instead of int to allow null values
                double balance = accountResultSet.getDouble("balance");
                boolean isActive = accountResultSet.getBoolean("is_active");
                String status = isActive ? "Active" : "Inactive";

                String employeeName = null;
                if (employeeID != null) {
                    Statement employeeStatement = connection.createStatement();
                    ResultSet employeeResultSet = employeeStatement.executeQuery(
                            "SELECT employees_name FROM employees WHERE employees_id = " + employeeID);
                    if (employeeResultSet.next()) {
                        employeeName = employeeResultSet.getString("employees_name");
                    }
                    employeeStatement.close();
                }
                model.addElement("Account#: " + accountNumber + "  Manager: " + employeeName + "  Balance: $" + balance + "  Status: " + status);
            }
            accountList.setModel(model);

        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
