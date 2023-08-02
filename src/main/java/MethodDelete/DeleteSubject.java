/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MethodDelete;

import OtherMethods.DatabaseConnector;
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
public class DeleteSubject {
    
    private final JTable tblSubjects;
    private JTextField txtSubName;
    private JTextField txtSubcode;
    private JTextArea txtSubDescription;
    
    public DeleteSubject(JTable tblSubjects, JTextField txtSubName, JTextField txtSubcode,JTextArea txtSubDescription) {
        this.tblSubjects = tblSubjects;
        this.txtSubName = txtSubName;
        this.txtSubcode = txtSubcode;
        this.txtSubDescription = txtSubDescription;
    }
    
    // Method to delete Subject
    public void deleteSubject() {
        int selectedRow = tblSubjects.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No subject selected. Please select a subject to delete.");
            return;
        }

        DefaultTableModel tblModel = (DefaultTableModel) tblSubjects.getModel();

        // Retrieve the data from the selected row
        String subjectName = tblModel.getValueAt(selectedRow, 0).toString();
        String subjectCode = tblModel.getValueAt(selectedRow, 1).toString();

        // Confirm deletion with the user
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the subject and its related records?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                
                // Get the subject_id for the selected subject
                String getSubjectIdQuery = "SELECT subject_id FROM subjects WHERE subject_name = ? AND subject_code = ?";
                try (PreparedStatement getSubjectIdStmt = conn.prepareStatement(getSubjectIdQuery)) {
                    getSubjectIdStmt.setString(1, subjectName);
                    getSubjectIdStmt.setString(2, subjectCode);
                    ResultSet rs = getSubjectIdStmt.executeQuery();

                    if (rs.next()) {
                        int subjectId = rs.getInt("subject_id");

                        // Delete related records from assessments table
                        String deleteAssessmentsQuery = "DELETE FROM assessments WHERE student_subject_id IN (SELECT student_subject_id FROM student_subjects WHERE subject_id = ?)";
                        try (PreparedStatement deleteAssessmentsStmt = conn.prepareStatement(deleteAssessmentsQuery)) {
                            deleteAssessmentsStmt.setInt(1, subjectId);
                            deleteAssessmentsStmt.executeUpdate();
                        }

                        // Delete related records from student_subjects table
                        String deleteStudentSubjectsQuery = "DELETE FROM student_subjects WHERE subject_id = ?";
                        try (PreparedStatement deleteStudentSubjectsStmt = conn.prepareStatement(deleteStudentSubjectsQuery)) {
                            deleteStudentSubjectsStmt.setInt(1, subjectId);
                            deleteStudentSubjectsStmt.executeUpdate();
                        }
                    }
                }

                // Delete the subject itself
                String deleteSubjectQuery = "DELETE FROM subjects WHERE subject_name = ? AND subject_code = ?";
                try (PreparedStatement deleteSubjectStmt = conn.prepareStatement(deleteSubjectQuery)) {
                    deleteSubjectStmt.setString(1, subjectName);
                    deleteSubjectStmt.setString(2, subjectCode);
                    int rowsAffected = deleteSubjectStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Remove the selected row from the table model
                        tblModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(null, "Subject and related records deleted successfully.");
                        
                        txtSubDescription.setText("");
                        txtSubName.setText("");
                        txtSubcode.setText("");
                        
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete subject and related records.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage());
                System.out.println(ex);
            }
        }
    }
}
