package com.nyit.japerz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminManageAccounts extends JFrame{
    private JButton refreshButton;
    private JTabbedPane tabbedPane1;
    private JTable customerTable;
    private JTable EmployeeTable;
    private JPanel panel1;
    private JButton changeInformationButton;

    public AdminManageAccounts() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Accounts Management");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        // Create columns in the customerTable
        DefaultTableModel customerTableModel = new DefaultTableModel();
        customerTableModel.addColumn("ID");
        customerTableModel.addColumn("Username");
        customerTableModel.addColumn("Email");
        customerTable.setModel(customerTableModel);

        // Create columns in the EmployeeTable
        DefaultTableModel employeeTableModel = new DefaultTableModel();
        employeeTableModel.addColumn("ID");
        employeeTableModel.addColumn("Username");
        employeeTableModel.addColumn("Email");
        EmployeeTable.setModel(employeeTableModel);

        customerTable.setDefaultEditor(Object.class, null);
        EmployeeTable.setDefaultEditor(Object.class, null);

        performSearch();

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // Add a popup menu to customerTable
        JPopupMenu customerPopup = new JPopupMenu();
        JMenuItem customerDelete = new JMenuItem("Delete");
        customerPopup.add(customerDelete);
        customerTable.setComponentPopupMenu(customerPopup);

        // Add a popup menu to EmployeeTable
        JPopupMenu employeePopup = new JPopupMenu();
        JMenuItem employeeDelete = new JMenuItem("Delete");
        employeePopup.add(employeeDelete);
        EmployeeTable.setComponentPopupMenu(employeePopup);

        // Add action listeners to the delete options
        customerDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
                        int customerId = (int) model.getValueAt(selectedRow, 0);
                        try {
                            Connection connection = Database.connection;
                            Statement statement = connection.createStatement();
                            String query = "DELETE FROM customers WHERE customers_id=" + customerId;
                            statement.executeUpdate(query);
                            statement.close();
                            //connection.close();
                            performSearch();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        employeeDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = EmployeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        DefaultTableModel model = (DefaultTableModel) EmployeeTable.getModel();
                        int employeeId = (int) model.getValueAt(selectedRow, 0);
                        try {
                            Connection connection = Database.connection;
                            Statement statement = connection.createStatement();
                            String query = "DELETE FROM employees WHERE employees_id=" + employeeId;
                            statement.executeUpdate(query);
                            statement.close();
                            //connection.close();
                            performSearch();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        changeInformationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeInformation ci = new ChangeInformation();
            }
        });
    }

    private void performSearch() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();

            // Execute the query to fetch all customers
            String customerQuery = "SELECT * FROM customers";
            ResultSet customerRs = statement.executeQuery(customerQuery);

            // Populate the customerTable with the results
            DefaultTableModel customerTableModel = (DefaultTableModel) customerTable.getModel();
            customerTableModel.setRowCount(0); // Clear existing rows

            while (customerRs.next()) {
                int customerId = customerRs.getInt("customers_id");
                String customerUsername = customerRs.getString("customers_username");
                String customerEmail = customerRs.getString("customers_email");

                Object[] customerRow = {customerId, customerUsername, customerEmail};
                customerTableModel.addRow(customerRow);
            }

            // Execute the query to fetch all employees
            String employeeQuery = "SELECT * FROM employees";
            ResultSet employeeRs = statement.executeQuery(employeeQuery);

            // Populate the employeeTable with the results
            DefaultTableModel employeeTableModel = (DefaultTableModel) EmployeeTable.getModel();
            employeeTableModel.setRowCount(0); // Clear existing rows

            while (employeeRs.next()) {
                int employeeId = employeeRs.getInt("employees_id");
                String employeeUsername = employeeRs.getString("employees_username");
                String employeeEmail = employeeRs.getString("employees_email");

                Object[] employeeRow = {employeeId, employeeUsername, employeeEmail};
                employeeTableModel.addRow(employeeRow);
            }

            // Close the statement, resultset and connection
            employeeRs.close();
            customerRs.close();
            statement.close();
            //connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
