package com.nyit.japerz;

import com.nyit.japerz.utils.HashingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChangeInformation extends JFrame {
    private JTabbedPane tabbedPane1;
    private JTextField cSearchTF;
    private JButton cSearchButton;
    private JComboBox<String> cCustomerCB;
    private JTextField cNameTF;
    private JTextArea addressTA;
    private JTextField cUsernameTF;
    private JPasswordField cPasswordField;
    private JButton cUpdateButton;
    private JTextField eSearchTF;
    private JButton eSearchButton;
    private JComboBox<String> employeesCB;
    private JTextField eNameTF;
    private JTextField eUsernameTF;
    private JPasswordField ePasswordField;
    private JButton eUpdateButton;
    private JPanel panel1;

    public ChangeInformation() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Accounts List");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        eSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performEmployeeSearch();
            }
        });
        cSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCustomerSearch();
            }
        });

        cCustomerCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomerInfo();
            }
        });

        eUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeInfo();
            }
        });

        cUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomerInfo();
            }
        });
    }

    private void performCustomerSearch() {
        String name = cSearchTF.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.");
            return;
        }

        try {
            Connection connection = Database.connection; // Connect to database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers WHERE customers_name LIKE '%" + name + "%'");

            cCustomerCB.removeAllItems();
            while (resultSet.next()) {
                int id = resultSet.getInt("customers_id");
                String customerName = resultSet.getString("customers_name");
                cCustomerCB.addItem(customerName + " (" + id + ")");
            }

            if (cCustomerCB.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No customer found with the given name.");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void performEmployeeSearch() {
        String name = eSearchTF.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.");
            return;
        }

        try {
            Connection connection = Database.connection; // Connect to database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM employees WHERE employees_name LIKE '%" + name + "%'");

            employeesCB.removeAllItems();
            while (resultSet.next()) {
                int id = resultSet.getInt("employees_id");
                String employeeName = resultSet.getString("employees_name");
                employeesCB.addItem(employeeName + " (" + id + ")");
            }

            if (employeesCB.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No employee found with the given name.");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadCustomerInfo() {
        String selectedItem = (String) cCustomerCB.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        int id = Integer.parseInt(selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")")).trim());

        try {
            Connection connection = Database.connection; // Connect to database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers WHERE customers_id = " + id);

            if (resultSet.next()) {
                String customerName = resultSet.getString("customers_name");
                String address = resultSet.getString("customers_address");
                String username = resultSet.getString("customers_username");

                cNameTF.setText(customerName);
                addressTA.setText(address);
                cUsernameTF.setText(username);
                cPasswordField.setText("");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateEmployeeInfo() {
        String selectedItem = (String) employeesCB.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        int id = Integer.parseInt(selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")")).trim());
        String employeeName = eNameTF.getText().trim();
        String username = eUsernameTF.getText().trim();
        String password = new String(ePasswordField.getPassword()).trim();
        String hashedPassword = HashingUtils.sha256(password);

        try {
            Connection connection = Database.connection; // Connect to database
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE employees SET employees_name = '" + employeeName + "', employees_username = '" + username + "', employees_password = '" + hashedPassword + "' WHERE employees_id = " + id);
            JOptionPane.showMessageDialog(this, "Employee information updated successfully.");
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCustomerInfo() {
        String selectedItem = (String) cCustomerCB.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        int id = Integer.parseInt(selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")")).trim());

        String name = cNameTF.getText().trim();
        String address = addressTA.getText().trim();
        String username = cUsernameTF.getText().trim();
        String password = new String(cPasswordField.getPassword()).trim();

        if (name.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Connection connection = Database.connection; // Connect to database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers WHERE customers_id = " + id);

            if (resultSet.next()) {
                String hashedPassword = HashingUtils.sha256(password);
                String sql = String.format("UPDATE customers SET customers_name = '%s', customers_address = '%s', customers_username = '%s', customers_password = '%s' WHERE customers_id = %d",
                        name, address, username, hashedPassword, id);

                statement.executeUpdate(sql);

                JOptionPane.showMessageDialog(this, "Customer information updated successfully.");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
