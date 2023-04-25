package com.nyit.japerz;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.Connection;

class Database {
    public static Connection connection;
    public static void connect(){
        String DatabaseUserName = "root";
        String DatabasePassword = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/bank_db?serverTimezone=EST", DatabaseUserName, DatabasePassword);
            System.out.println("[INFO] Connected to Database " + DatabaseUserName + "!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to establish connection with the JM Bank server, please contact the bank employee. Error Code: N-001", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR] Cannot connect to Database " + DatabaseUserName + "! Please check the error message below!");
            ex.printStackTrace();
        }
    }

}
