package com.nyit.japerz;

import com.nyit.japerz.utils.HashingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateEmployee extends JFrame{
    private JTextField nameTF;
    private JTextField usernameTF;
    private JButton createButton;
    private JButton cancelButton;
    private JPasswordField passwordField;
    private JPanel panel1;

    public CreateEmployee() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Create Employee");
        setSize(400, 500);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCreate();
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void performCreate() {
        try {
            Connection connection = Database.connection; // Connect to database
            Statement stm = connection.createStatement();

            String name = nameTF.getText();
            String username = usernameTF.getText();
            String password = new String(passwordField.getPassword());

            String hashedPasswordEntered = HashingUtils.sha256(password);

            // Construct the SQL query
            String query = "INSERT INTO employees (employees_name, employees_dob, employees_username, employees_email, employees_password) " +
                    "VALUES ('" + name + "', '" + "1999-01-01" + "', '" + username + "', '" + "default@email.com" + "', '" + hashedPasswordEntered + "')";

            // Execute the query
            stm.executeUpdate(query);

            // Close the statement and connection
            stm.close();
            connection.close();

            JOptionPane.showMessageDialog(this, "Employee created successfully!");

            // Clear the fields after successful insertion
            nameTF.setText("");
            usernameTF.setText("");
            passwordField.setText("");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating employee: " + ex.getMessage());
        }
    }


}
