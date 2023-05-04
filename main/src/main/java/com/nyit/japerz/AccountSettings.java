package com.nyit.japerz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountSettings extends JFrame{
    private JTextField nameTF;
    private JTextArea addressTA;
    private JButton updateButton;
    private JPanel panel1;

    public AccountSettings() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Account Settings");
        setSize(350, 500);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        performInfoSearch();

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performUpdate();
            }
        });
    }

    private void performInfoSearch() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            String username = Login.getUsernamePT();
            String query = "SELECT customers_name, customers_address FROM customers WHERE customers_username = '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String name = resultSet.getString("customers_name");
                String address = resultSet.getString("customers_address");
                nameTF.setText(name);
                addressTA.setText(address);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void performUpdate() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            String username = Login.getUsernamePT();
            String name = nameTF.getText();
            String address = addressTA.getText();
            String query = "UPDATE customers SET customers_name = '" + name + "', customers_address = '" + address + "' WHERE customers_username = '" + username + "'";
            int rowsUpdated = statement.executeUpdate(query);
            if (rowsUpdated == 1) {
                JOptionPane.showMessageDialog(this, "Information updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update information.");
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
