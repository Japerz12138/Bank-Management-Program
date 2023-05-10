package com.nyit.japerz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountList extends JFrame{
    private JTable table1;
    private JButton closeButton;
    private JPanel panel1;
    private JButton refreshButton;
    private JButton applicationPendingButton;


    public AccountList() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Accounts List");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        table1.setDefaultEditor(Object.class, null);

        performSearch();

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
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

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete");
                    deleteItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            int option = JOptionPane.showConfirmDialog(AccountList.this,
                                    "Are you sure you want to delete this account?\nThis process cannot be undo!\nPlease make sure you have notify the customer first!",
                                    "Delete Account", JOptionPane.YES_NO_OPTION);
                            if (option == JOptionPane.YES_OPTION) {
                                int row = table1.getSelectedRow();
                                String accountNumber = (String) table1.getValueAt(row, 1);

                                try {
                                    Connection connection = Database.connection;
                                    Statement statement = connection.createStatement();

                                    String deleteQuery = "DELETE FROM accounts WHERE account_number = '" + accountNumber + "'";
                                    int result = statement.executeUpdate(deleteQuery);

                                    if (result == 1) {
                                        performSearch();
                                        JOptionPane.showMessageDialog(AccountList.this, "Account deleted successfully.");
                                    } else {
                                        JOptionPane.showMessageDialog(AccountList.this, "Error deleting account.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }

                                    statement.close();
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });
                    popup.add(deleteItem);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });


        applicationPendingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PendingList pl = new PendingList();
            }
        });
    }

    private void performSearch() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();

            String query = "SELECT customers.customers_name, accounts.account_number, accounts.balance " +
                    "FROM customers " +
                    "JOIN accounts ON customers.customers_id = accounts.customers_id " +
                    "ORDER BY customers.customers_name ASC"; //This ASC means ascending order, DESC is descending

            ResultSet rs = statement.executeQuery(query);

            // Create a default table model to hold the results of the query
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Customer Name", "Account Number", "Balance"}, 0);

            while (rs.next()) {
                String customerName = rs.getString("customers_name");
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");

                // Add a row to the table model for each account
                model.addRow(new Object[]{customerName, accountNumber, balance});
            }

            // Set the table model for table1
            table1.setModel(model);

            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
