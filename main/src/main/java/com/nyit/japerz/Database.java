package com.nyit.japerz;

import java.sql.DriverManager;
import java.sql.Connection;
import javax.xml.crypto.Data;
class Database {
    public static Connection connection;
    public static void connect(){
        String DatabaseUserName = "root";
        String DatabasePassword = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/bank_db?serverTimezone=EST", DatabaseUserName, DatabasePassword);
            System.out.println("[INFO] Connected to Database " + DatabaseUserName + "!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
