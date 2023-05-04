package com.nyit.japerz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerList extends JFrame{
    private JButton closeButton;
    private JTable table1;
    private JTextField searchTF;
    private JButton searchButton;
    private JPanel panel1;

    public CustomerList() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Customer Search");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        table1.setDefaultEditor(Object.class, null);


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAccountSearch();
            }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        source.setRowSelectionInterval(row, row);
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem menuItem = new JMenuItem("Sent Message");
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                sendMessage();
                            }
                        });
                        popupMenu.add(menuItem);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }
    private void performAccountSearch() {
        try{
            String username = Login.getUsernamePT();
            int customerID = -1;
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();

            // Get the customer name from the search text field
            String customerName = searchTF.getText();

            // Retrieve the customer ID and account data from the database
            String customerQuery = "SELECT customers_id FROM customers WHERE customers_name = '" + customerName + "'";
            String accountQuery = "SELECT COUNT(*) AS num_accounts, SUM(balance) AS total_balance FROM accounts WHERE customers_id = ?";
            PreparedStatement customerStatement = connection.prepareStatement(customerQuery);
            PreparedStatement accountStatement = connection.prepareStatement(accountQuery);
            ResultSet customerResult;
            ResultSet accountResult;

            customerResult = customerStatement.executeQuery();
            if (customerResult.next()) {
                customerID = customerResult.getInt("customers_id");

                accountStatement.setInt(1, customerID);
                accountResult = accountStatement.executeQuery();

                // Create the table model and add the retrieved data to it
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Customer ID");
                tableModel.addColumn("Customer Name");
                tableModel.addColumn("Accounts");
                tableModel.addColumn("Total Balance");

                table1.setModel(tableModel);
                table1.getColumnModel().getColumn(0).setPreferredWidth(80);
                table1.getColumnModel().getColumn(1).setPreferredWidth(150);
                table1.getColumnModel().getColumn(2).setPreferredWidth(80);
                table1.getColumnModel().getColumn(3).setPreferredWidth(120);

                Object[] rowData = new Object[4];
                rowData[0] = customerID;
                rowData[1] = customerName;

                if (accountResult.next()) {
                    rowData[2] = accountResult.getInt("num_accounts");
                    rowData[3] = accountResult.getDouble("total_balance");
                } else {
                    rowData[2] = 0;
                    rowData[3] = 0.0;
                }

                tableModel.addRow(rowData);
            } else {
                // No customer found with the given name
                JOptionPane.showMessageDialog(this, "No customer found with the given name.");
            }

        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendMessage() {
        try {
            String username = Login.getUsernamePT();
            int customerID = -1;
            int employeeID = -1;
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();

            // Get the selected row and retrieve the customer ID
            int selectedRow = table1.getSelectedRow();
            String customerName = (String) table1.getValueAt(selectedRow, 1);
            String customerQuery = "SELECT customers_id FROM customers WHERE customers_name = ?";
            PreparedStatement customerStatement = connection.prepareStatement(customerQuery);
            customerStatement.setString(1, customerName);
            ResultSet customerResult = customerStatement.executeQuery();
            if (customerResult.next()) {
                customerID = customerResult.getInt("customers_id");
            }

            // Retrieve the employee ID
            String employeeQuery = "SELECT employees_id FROM employees WHERE employees_username = ?";
            PreparedStatement employeeStatement = connection.prepareStatement(employeeQuery);
            employeeStatement.setString(1, username);
            ResultSet employeeResult = employeeStatement.executeQuery();
            if (employeeResult.next()) {
                employeeID = employeeResult.getInt("employees_id");
            }

            // Create the message dialog
            JTextArea messageArea = new JTextArea(10, 40);
            JScrollPane messageScrollPane = new JScrollPane(messageArea);
            JPanel messagePanel = new JPanel();
            messagePanel.add(messageScrollPane);
            int result = JOptionPane.showConfirmDialog(null, messagePanel, "Send Message",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String messageContent = messageArea.getText();

                // Insert the new message into the database
                String messageQuery = "INSERT INTO messages (message_id, customers_id, employees_id, messageContent) VALUES (?, ?, ?, ?)";
                PreparedStatement messageStatement = connection.prepareStatement(messageQuery);
                int messageID = getMaxMessageID() + 1;
                messageStatement.setInt(1, messageID);
                messageStatement.setInt(2, customerID);
                messageStatement.setInt(3, employeeID);
                messageStatement.setString(4, messageContent);
                messageStatement.executeUpdate();

                JOptionPane.showMessageDialog(null, "Message sent successfully.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getMaxMessageID() {
        int maxMessageID = -1;
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(message_id) AS max_id FROM messages");
            if (resultSet.next()) {
                maxMessageID = resultSet.getInt("max_id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PendingList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxMessageID;
    }
}
