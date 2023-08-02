/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OtherMethods;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author loena
 */
public class DatabaseConnector {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/grade-calculator-db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    
   
//public static void main(String[] args) throws SQLException {
//      try (Connection conn = DatabaseConnector.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Create the table
//            String sql = "CREATE TABLE users (id SERIAL PRIMARY KEY, fullname VARCHAR(50) NOT NULL, username VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL)";
//            stmt.executeUpdate(sql);
//            System.out.println("Database created successfully!");
//            // Insert default user
////            String insertQuery = "INSERT INTO users (fullname, username, password) VALUES ('Admin','admin1', 'admin1')";
////            stmt.executeUpdate(insertQuery);
//
//            System.out.println("Table created successfully...");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
