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

public class PendingList extends JFrame{
    private JButton refreshButton;
    private JTable table1;
    private JButton closeButton;
    private JPanel panel1;

    public PendingList() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Accounts List");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        performSearch();


        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
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
            public void mouseReleased(MouseEvent e) {
                int r = table1.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table1.getRowCount()) {
                    table1.setRowSelectionInterval(r, r);
                } else {
                    table1.clearSelection();
                }
                Object accountNumber = table1.getValueAt(r, 1);

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem approveItem = new JMenuItem("Approve");
                    approveItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            try {
                                Connection connection = Database.connection;
                                Statement statement = connection.createStatement();
                                String updateQuery = "UPDATE accounts SET is_active = true, employees_id = (SELECT employees_id FROM employees WHERE employees_username = '" +
                                        Login.getUsernamePT() + "') WHERE account_number = " + accountNumber;
                                statement.executeUpdate(updateQuery);

                                // Get the customer ID for the account
                                String getCustomerIdQuery = "SELECT customers_id FROM accounts WHERE account_number = " + accountNumber;
                                ResultSet rs = statement.executeQuery(getCustomerIdQuery);
                                int customerId = -1;
                                if (rs.next()) {
                                    customerId = rs.getInt("customers_id");
                                }

                                // Insert a new message into the messages table
                                String insertQuery = "INSERT INTO messages (customers_id, employees_id, messageContent) VALUES (" +
                                        customerId + ", (SELECT employees_id FROM employees WHERE employees_username = '" +
                                        Login.getUsernamePT() + "'), 'Your account " + accountNumber + " has been approved.')";
                                statement.executeUpdate(insertQuery);

                                statement.close();
                                performSearch();
                                JOptionPane.showMessageDialog(PendingList.this, "Account " + accountNumber + " has been approved!\nA message has send to the customer.");
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    popup.add(approveItem);

                    // Add a "Deny" option to the popup menu
                    JMenuItem denyItem = new JMenuItem("Deny");
                    denyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            try {
                                Connection connection = Database.connection;
                                Statement statement = connection.createStatement();
                                String deleteQuery = "DELETE FROM accounts WHERE account_number = " + accountNumber;
                                statement.executeUpdate(deleteQuery);

                                // Get the customer ID for the account
                                String getCustomerIdQuery = "SELECT customers_id FROM accounts WHERE account_number = " + accountNumber;
                                ResultSet rs = statement.executeQuery(getCustomerIdQuery);
                                int customerId = -1;
                                if (rs.next()) {
                                    customerId = rs.getInt("customers_id");
                                }

                                // Insert a new message into the messages table
                                String insertQuery = "INSERT INTO messages (customers_id, employees_id, messageContent) VALUES (" +
                                        customerId + ", (SELECT employees_id FROM employees WHERE username = '" +
                                        Login.getUsernamePT() + "'), 'Your account " + accountNumber + " has been declined.')";
                                statement.executeUpdate(insertQuery);

                                statement.close();
                                performSearch();
                                JOptionPane.showMessageDialog(PendingList.this, "Account " + accountNumber + " has been denied!\nA message has send to the customer.");
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    popup.add(denyItem);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

    }

    private void performSearch() {
        try {
            String username = Login.getUsernamePT();

            Connection connection = Database.connection;
            Statement statement = connection.createStatement();

            // Select all accounts with is_active = false
            String query = "SELECT * FROM accounts WHERE is_active = false";
            ResultSet rs = statement.executeQuery(query);

            // Create a default table model to hold the results of the query
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Customer Name", "Account Number", "Balance", "Status"}, 0);

            while (rs.next()) {
                int customerID = rs.getInt("customers_id");
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");
                boolean isActive = rs.getBoolean("is_active");

                // Get the customer name from customers table
                Statement statement2 = connection.createStatement();
                String query2 = "SELECT customers_name FROM customers WHERE customers_id = " + customerID;
                ResultSet rs2 = statement2.executeQuery(query2);
                String customerName = "";
                if (rs2.next()) {
                    customerName = rs2.getString("customers_name");
                }
                statement2.close();

                // Add a row to the table model for each account
                model.addRow(new Object[]{customerName, accountNumber, balance, isActive ? "Active" : "Pending"});
            }

            // Set the table model for table1
            table1.setModel(model);


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
