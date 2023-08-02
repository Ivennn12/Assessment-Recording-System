/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MethodDelete;

import OtherMethods.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iven
 */
public class DeleteRecord {
    //Global Variable
    private final JTable tblRecords;
    private JTextField txtSName;
    private JTextField txtCourse;
    private JTextField txtYearLvl;
    private JTextField txtSubject;
    private JTextField txtAssessmentType;
    private JTextField txtAssessNum;
    private JTextField txtItems;
    private JTextField txtScores;
    
    //Constructor
    public DeleteRecord(JTable tblRecords, JTextField txtSName, JTextField txtCourse, JTextField txtYearLvl, JTextField txtSubject, JTextField txtAssessmentType, JTextField txtAssessNum, JTextField txtItems, JTextField txtScores ) {
        this.tblRecords = tblRecords;
        this.txtSName = txtSName;
        this.txtCourse =txtCourse;
        this.txtYearLvl=txtYearLvl;
        this.txtSubject =txtSubject;
        this.txtAssessmentType =txtAssessmentType;
        this.txtAssessNum = txtAssessNum;
        this.txtItems = txtItems;
        this.txtScores = txtScores;
    }
    
    //Method to delete Assessment
    public void deleteAssessment() {
        int selectedRow = tblRecords.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No record selected. Please select an assessment to delete.");
            return;
        }

        DefaultTableModel tblModel = (DefaultTableModel) tblRecords.getModel();

        // Retrieve the data from the selected row
        String fullName = tblModel.getValueAt(selectedRow, 0).toString(); // Assuming the concatenated name is in the first column.
        String subject = tblModel.getValueAt(selectedRow, 2).toString();
        String assessmentType = tblModel.getValueAt(selectedRow, 3).toString();
        int assessmentNum = Integer.parseInt(tblModel.getValueAt(selectedRow, 4).toString());

        // Delete the corresponding record from the database
        try (Connection conn = DatabaseConnector.getConnection()) {
            String deleteSQL = "DELETE FROM assessments "
                + "WHERE student_subject_id IN (SELECT ss.student_subject_id FROM student_subjects ss "
                + "INNER JOIN students s ON ss.student_id = s.student_id "
                + "WHERE CONCAT(s.last_name, ', ' , s.first_name, ' ' , s.middle_name) = ? "
                + "AND ss.subject_id = (SELECT subject_id FROM subjects WHERE subject_name = ?)) "
                + "AND assessment_type = ? "
                + "AND assessment_no = ?";
            
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {
                deleteStmt.setString(1, fullName);
                deleteStmt.setString(2, subject);
                deleteStmt.setString(3, assessmentType);
                deleteStmt.setInt(4, assessmentNum);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Remove the selected row from the table model
                    tblModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(null, "Record deleted successfully.");
                    
                    txtSName.setText("");
                    txtCourse.setText("");
                    txtYearLvl.setText("");
                    txtSubject.setText("");
                    txtAssessmentType.setText("");
                    txtAssessNum.setText("");
                    txtItems.setText("");
                    txtScores.setText("");
                    
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete the record.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage());
        }
    }
}



