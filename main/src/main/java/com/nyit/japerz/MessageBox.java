package com.nyit.japerz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageBox extends JFrame{
    private JButton refreshButton;
    private JTable table1;
    private JButton closeButton;
    private JPanel panel1;

    public MessageBox() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Message Box");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        performMessageSearch();

        refreshButton.addActionListener(e -> performMessageSearch());
        closeButton.addActionListener(e -> dispose());
    }

    private void performMessageSearch() {
        try{
            String username = Login.getUsernamePT();

            Connection connection = Database.connection;

            // Retrieve the customer ID from the database
            String customerQuery = "SELECT customers_id FROM customers WHERE customers_username = ?";
            PreparedStatement customerStatement = connection.prepareStatement(customerQuery);
            customerStatement.setString(1, username);
            ResultSet customerResult = customerStatement.executeQuery();

            if (customerResult.next()) {
                int customerID = customerResult.getInt("customers_id");

                // Retrieve the messages for the customer from the database
                String messageQuery = "SELECT messageContent, employees_name FROM messages " +
                        "INNER JOIN employees ON messages.employees_id = employees.employees_id " +
                        "WHERE customers_id = ?";
                PreparedStatement messageStatement = connection.prepareStatement(messageQuery);
                messageStatement.setInt(1, customerID);
                ResultSet messageResult = messageStatement.executeQuery();

                // Create the table model and add the retrieved data to it
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Sender");
                tableModel.addColumn("Message");

                table1.setModel(tableModel);
                table1.getColumnModel().getColumn(0).setPreferredWidth(150);
                table1.getColumnModel().getColumn(1).setPreferredWidth(500);

                while (messageResult.next()) {
                    String messageContent = messageResult.getString("messageContent");
                    String senderName = messageResult.getString("employees_name");

                    Object[] rowData = new Object[2];
                    rowData[0] = senderName;
                    rowData[1] = messageContent;

                    tableModel.addRow(rowData);
                }
            } else {
                // No customer found with the given username
                JOptionPane.showMessageDialog(this, "No customer found with the given username.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}