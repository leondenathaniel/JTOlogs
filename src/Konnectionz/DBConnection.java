/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Konnectionz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages the connection to the MySQL database.
 * Other DAO classes will use this class whenever they need database access.
 */
public class DBConnection {

    // Change these values based on your local MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3307/jeepney_terminal_organizer";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Opens and returns a database connection.
     * If the connection fails, it returns null.
     */
    public static Connection getConnection() {
        Connection conn = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }

        return conn;
    }
}