package com.nyit.japerz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;

public class DepositWithdraw extends JFrame implements ActionListener {
    private JPanel panel1;
    private JComboBox<String> accountCB;
    private JLabel balanceLabel;
    private JTextField depositTF;
    private JButton depositButton;
    private JTextField withdrawTF;
    private JButton withdrawButton;
    private JButton doneButton;

    private String selectedAccount;
    private BigDecimal accountBalance;

    public DepositWithdraw() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Deposit and Withdraw");
        setSize(300, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        doneButton.addActionListener(this);

        performAccountSearch();

        accountCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String accountNumber = (String) cb.getSelectedItem();
                try {
                    Connection connection = Database.connection;
                    String query = "SELECT balance FROM accounts WHERE account_number = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, accountNumber);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        BigDecimal balance = resultSet.getBigDecimal("balance");
                        accountBalance = balance;
                        balanceLabel.setText("Balance: $" + balance);
                        selectedAccount = accountNumber; // update the selected account number
                    }
                    resultSet.close();
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void performAccountSearch() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            String username = Login.getUsernamePT();
            String query = "SELECT account_number, balance, is_active FROM accounts WHERE customers_id = (SELECT customers_id FROM customers WHERE customers_username = '" + username + "')";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String accountNumber = resultSet.getString("account_number");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                boolean isActive = resultSet.getBoolean("is_active");
                accountCB.addItem(accountNumber);
                accountBalance = balance;
                balanceLabel.setText("Balance: $" + balance);
                selectedAccount = accountNumber; // set the selected account number

                if (!isActive) { // if account is not active, disable deposit and withdraw buttons
                    depositButton.setEnabled(false);
                    withdrawButton.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "This account is not activated yet.");
                }
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == depositButton) {
            try {
                BigDecimal amount = new BigDecimal(depositTF.getText());
                accountBalance = accountBalance == null ? BigDecimal.ZERO : accountBalance;
                accountBalance = accountBalance.add(amount);
                balanceLabel.setText("Balance: $" + accountBalance);
                updateAccountBalance();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            }
        } else if (source == withdrawButton) {
            try {
                BigDecimal amount = new BigDecimal(withdrawTF.getText());
                accountBalance = accountBalance == null ? BigDecimal.ZERO : accountBalance;
                if (amount.compareTo(accountBalance) <= 0) {
                    accountBalance = accountBalance.subtract(amount);
                    balanceLabel.setText("Balance: $" + accountBalance);
                    updateAccountBalance();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            }
        } else if (source == doneButton) {
            dispose();
        }
    }

    private void updateAccountBalance() {
        try {
            Connection connection = Database.connection;
            String query = "UPDATE accounts SET balance = ? WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            if (accountBalance == null) {
                accountBalance = BigDecimal.ZERO;
            }
            statement.setBigDecimal(1, accountBalance);
            statement.setString(2, selectedAccount);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 1) {
                System.out.println("Balance updated successfully.");
            } else {
                System.out.println("Failed to update balance.");
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
