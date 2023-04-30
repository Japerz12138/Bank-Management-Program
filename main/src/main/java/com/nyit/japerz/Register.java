package com.nyit.japerz;

import com.mysql.cj.log.Log;
import com.nyit.japerz.utils.HashingUtils;
import com.nyit.japerz.utils.ValidationUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends JFrame{
    private JPanel panel1;
    private JTextField nameTF;
    private JTextField emailTF;
    private JTextArea addressTA;
    private JPasswordField passwordPTF;
    private JPasswordField confirmPasswordPTF;
    private JButton registerButton;
    private JButton cancelButton;
    private JTextField usernameTF;
    private JTextField dob_monthTF;
    private JTextField dob_dayTF;
    private JTextField dob_yearTF;

    //Initialize the window
    public Register() {
        ImageIcon img = new ImageIcon("I:\\CODE\\Bank-Management-Program\\bank-flat.png");
        setContentPane(panel1);
        setTitle("JM Bank - Register");
        setSize(800, 550);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(img.getImage());

        setLookAndFeel();

        //Register Button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        //Cancel Button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                dispose();
            }
        });
    }

    //Main Registration Function
    private void performRegistration(){
        try{

            //Open connection to database
            Connection connection = Database.connection; // Connect to database
            Statement stm = connection.createStatement();

            //Get contents from the text field
            String name = nameTF.getText();
            String username = usernameTF.getText();
            String email = emailTF.getText();
            int dob_month = Integer.parseInt(dob_monthTF.getText());
            int dob_day = Integer.parseInt(dob_dayTF.getText());
            int dob_year = Integer.parseInt(dob_yearTF.getText());

            String address = addressTA.getText();
            String password = new String(passwordPTF.getPassword());
            String confirmPassword = new String(confirmPasswordPTF.getPassword());

            //Set date format and check if the date is valid
            String dobStr = dob_month + "/" + dob_day + "/" + dob_year;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            try {
                Date dob = sdf.parse(dobStr);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Please enter date in the format: MM/DD/YYYY");
                return;
            }

            //Change date format to what mysql can read
            String dobSqlStr = dob_year + "-" + dob_month + "-" + dob_day;

            //Invalidate email address message
            if (!ValidationUtils.emailChecker(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email address! Please enter a valid email address.");
                return;
            }

            //Password not match message
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Oops!, passwords do not match!");
                return;
            }

            //Encrypt password with SHA256
            String hashedPasswordEntered = HashingUtils.sha256(password);

            //Check the customers_id position
            String query = "SELECT MAX(customers_id) FROM customers";
            ResultSet rs = stm.executeQuery(query);
            int customerID = 1;
            if (rs.next()){
                customerID = rs.getInt(1) + 1;
            }

            query = String.format("INSERT INTO customers(customers_id, customers_name, customers_dob, customers_username, customers_email, customers_address, customers_password) VALUES(%d, '%s', '%s', '%s', '%s', '%s', '%s')", customerID, name, dobSqlStr, username, email, address, hashedPasswordEntered);
            stm.executeUpdate(query);

            JOptionPane.showMessageDialog(this, "Registration successful! Please login!");

            //Close current window and back to log in window
            Login login = new Login();
            dispose();


        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering customer! Please contact a bank employee.");
        }
    }

    //Set the window looks modern on Windows
    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
