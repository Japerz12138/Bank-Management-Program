package com.nyit.japerz;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OnlineAppointment extends JFrame{
    private JComboBox employeeCB;
    private JSpinner monthS;
    private JSpinner dayS;
    private JSpinner yearS;
    private JSpinner hourS;
    private JSpinner minuteS;
    private JComboBox ampmCB;
    private JButton makeAppointmentButton;
    private JButton cancelButton;
    private JPanel panel1;

    public OnlineAppointment() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Make Appointment");
        setSize(650, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        performEmployeeSearch();

        makeAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performMakeAppointment();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void performEmployeeSearch() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            String query = "SELECT employees_id, employees_name FROM employees";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String employeeName = resultSet.getString("employees_name");
                employeeCB.addItem(employeeName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void performMakeAppointment() {
        try {
            Connection connection = Database.connection;
            Statement statement = connection.createStatement();
            String appointmentDate = monthS.getValue() + "/" + dayS.getValue() + "/" + yearS.getValue() + " " +
                    hourS.getValue() + ":" + minuteS.getValue() + " " + ampmCB.getSelectedItem();
            String customersIdQuery = "SELECT customers_id FROM customers WHERE customers_username = '" +
                    Login.getUsernamePT() + "'";
            ResultSet resultSet = statement.executeQuery(customersIdQuery);
            resultSet.next();
            int customersId = resultSet.getInt("customers_id");
            String employeesName = (String) employeeCB.getSelectedItem();
            String employeesIdQuery = "SELECT employees_id FROM employees WHERE employees_name = '" +
                    employeesName + "'";
            ResultSet resultSet2 = statement.executeQuery(employeesIdQuery);
            resultSet2.next();
            int employeesId = resultSet2.getInt("employees_id");
            String insertQuery = "INSERT INTO appointments (customers_id, employees_id, appointment_date) " +
                    "VALUES (" + customersId + ", " + employeesId + ", '" + appointmentDate + "')";
            statement.executeUpdate(insertQuery);

            JOptionPane.showMessageDialog(this, "You have successfully made an appointment with " + employeesName + " on \n " + appointmentDate);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
