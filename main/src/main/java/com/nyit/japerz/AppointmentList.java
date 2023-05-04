package com.nyit.japerz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppointmentList extends JFrame{
    private JPanel panel1;
    private JTable table1;
    private JButton closeButton;

    public AppointmentList() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Appointment List");
        setSize(750, 650);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        table1.setDefaultEditor(Object.class, null);

        performAppointmentSearch();
    }

    private void performAppointmentSearch() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Appointment ID", "Customer Name", "Employee Name", "Date and Time"}, 0);

        try {
            Connection connection = Database.connection; // Connect to database
            Statement stm = connection.createStatement();

            // Select all appointments from appointments table
            ResultSet appointmentResults = stm.executeQuery("SELECT * FROM appointments");

            // Loop through each appointment
            while (appointmentResults.next()) {
                int appointmentId = appointmentResults.getInt("appointment_id");
                int customerId = appointmentResults.getInt("customers_id");
                int employeeId = appointmentResults.getInt("employees_id");
                String dateTime = appointmentResults.getString("appointment_date");

                // Get customer name from customers table using customer_id
                Statement customerStm = connection.createStatement();
                ResultSet customerResults = customerStm.executeQuery("SELECT * FROM customers WHERE customers_id = " + customerId);
                String customerName = "";
                if (customerResults.next()) {
                    customerName = customerResults.getString("customers_name");
                }
                customerResults.close();
                customerStm.close();

                // Get employee name from employees table using employee_id
                Statement employeeStm = connection.createStatement();
                ResultSet employeeResults = employeeStm.executeQuery("SELECT * FROM employees WHERE employees_id = " + employeeId);
                String employeeName = "";
                if (employeeResults.next()) {
                    employeeName = employeeResults.getString("employees_name");
                }
                employeeResults.close();
                employeeStm.close();

                // Add the appointment details to the table model
                model.addRow(new Object[]{appointmentId, customerName, employeeName, dateTime});
            }
            appointmentResults.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table1.setModel(model);
    }
}
