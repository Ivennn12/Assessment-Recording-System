/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OtherMethods;

/**
 *
 * @author Iven
 */

import java.sql.*;
import javax.swing.JTextField;

public class AdminHandler {
    
    // for login
    public static boolean loginAdmin(String admin, String pass) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM admin WHERE admin = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, admin);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // User exists with the provided username and password
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // No user found with the provided username and password
        return false;
    }
    
    public static void updateAdminPassword(String newPassword) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "UPDATE admin SET password = ? WHERE admin_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, 1);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Admin password updated successfully!");
            } else {
                System.out.println("Admin password update failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Admin password update failed!");
        }
    }
    
    // Validate the current password for the specified admin
    public static boolean validateAdminPassword(String admin, String currentPassword) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM admin WHERE admin = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, admin);
            pstmt.setString(2, currentPassword);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Returns true if the admin and password match, false otherwise
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // An error occurred during validation, return false as a fallback
        return false;
    }
    
    public static void updateAdminHint(String newHint) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "UPDATE admin SET hint = ? WHERE admin_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newHint);
            pstmt.setInt(2, 1); 

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Admin hint updated successfully!");
            } else {
                System.out.println("Admin hint update failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Admin hint update failed!");
        }
    }
    
    // Get the hint for the admin
    public static String getHintForAdmin(String admin) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT hint FROM admin WHERE admin = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, admin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Hint found, return the hint value
                return rs.getString("hint");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Hint not found for the admin
        return "Hint not available.";
    }
    
    // Get the hint for the admin
    public static String getPasswordForAdmin(String admin) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT password FROM admin WHERE admin = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, admin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Hint found, return the hint value
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Hint not found for the admin
        return "Hint not available.";
    }
}