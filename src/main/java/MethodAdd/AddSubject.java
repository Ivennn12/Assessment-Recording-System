/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MethodAdd;

import OtherMethods.DatabaseConnector;
import OtherMethods.UpdateCBXSubjects;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iven
 */
public class AddSubject {
    private JTable tblSubjects;
    private JTextField txtSubName;
    private JTextField txtSubcode;
    private JTextArea txtSubDescription;

    public AddSubject(JTable tblSubjects, JTextField txtSubName, JTextField txtSubcode, JTextArea txtSubDescription) {
        this.tblSubjects = tblSubjects;
        this.txtSubName = txtSubName;
        this.txtSubcode = txtSubcode;
        this.txtSubDescription = txtSubDescription;
    }

    public void addSubject() {
        String subjectName = txtSubName.getText().toUpperCase();
        String subjectCode = txtSubcode.getText();
        String subDescription = txtSubDescription.getText();

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Check if the subject already exists
            String checkDuplicateSQL = "SELECT subject_name FROM subjects WHERE subject_name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkDuplicateSQL);
            checkStmt.setString(1, subjectName);
            ResultSet checkResult = checkStmt.executeQuery();

            if (checkResult.next()) {
                JOptionPane.showMessageDialog(null, "Subject already exists.");
            } else {
                // Insert into subject table
                String insertSubjectSQL = "INSERT INTO subjects ( subject_name, subject_code, description) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSubjectSQL);

                insertStmt.setString(1, subjectName);
                insertStmt.setString(2, subjectCode);
                insertStmt.setString(3, subDescription);
                insertStmt.executeUpdate();
                System.out.println("Subject data inserted!");
                JOptionPane.showMessageDialog(null, "Subject added successfully.");

                // Update the table model and ComboBox
                String recentData[] = { subjectName, subjectCode, subDescription };
                DefaultTableModel tblModel = (DefaultTableModel) tblSubjects.getModel();
                tblModel.addRow(recentData);

                // Reset the input fields
                txtSubName.setText("");
                txtSubcode.setText("");
                txtSubDescription.setText("");
                

            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error occurred.");
            e.printStackTrace();
        }
    }
}
