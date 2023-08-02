/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MethodUpdate;

import OtherMethods.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iven
 */
public class UpdateRecords {
    
    private final JTable tblRecords;
    private final String SName;
    private JTextField txtSName;
    private JTextField txtCourse;
    private JTextField txtYearLvl;
    private JTextField txtSubject;
    private JTextField txtAssessmentType;
    private JTextField txtAssessNum;
    private JTextField txtItems;
    private JTextField txtScores;
    private final int items;
    private final int scores;

    public UpdateRecords(JTable tblRecords,String SName, int items, int scores, JTextField txtSName, JTextField txtCourse, JTextField txtYearLvl, JTextField txtSubject, JTextField txtAssessmentType, JTextField txtAssessNum, JTextField txtItems, JTextField txtScores) {
        this.tblRecords = tblRecords;
        this.items = items;
        this.scores = scores;
        this.SName = SName;
        this.txtSName = txtSName;
        this.txtCourse =txtCourse;
        this.txtYearLvl=txtYearLvl;
        this.txtSubject =txtSubject;
        this.txtAssessmentType =txtAssessmentType;
        this.txtAssessNum = txtAssessNum;
        this.txtItems = txtItems;
        this.txtScores = txtScores;
    }

    public void updateAssessmentRecord() {
        int selectedRow = tblRecords.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No record selected. Please select an assessment to update.");
            return;
        }

        DefaultTableModel tblModel = (DefaultTableModel) tblRecords.getModel();

        // Retrieve the data from the selected row
        String studentName = tblModel.getValueAt(selectedRow, 0).toString();
        String subName = tblModel.getValueAt(selectedRow, 2).toString();
        String assessType = tblModel.getValueAt(selectedRow, 3).toString();
        int assessmentNum = Integer.parseInt(tblModel.getValueAt(selectedRow, 4).toString());
        int item = Integer.parseInt(tblModel.getValueAt(selectedRow, 5).toString());
        int score = Integer.parseInt(tblModel.getValueAt(selectedRow, 6).toString());

        int assessmentId = -1; // Default value indicating not found

        String query = "SELECT a.assessment_id, s.student_id FROM assessments a "
                + "INNER JOIN student_subjects ss ON a.student_subject_id = ss.student_subject_id "
                + "INNER JOIN students s ON ss.student_id = s.student_id "
                + "WHERE CONCAT(s.last_name, ', ', s.first_name, ' ', s.middle_name) = ? "
                + "AND ss.subject_id = (SELECT subject_id FROM subjects WHERE subject_name = ?) "
                + "AND a.assessment_type = ? AND a.assessment_no = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentName);
            stmt.setString(2, subName);
            stmt.setString(3, assessType);
            stmt.setInt(4, assessmentNum);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    assessmentId = rs.getInt("assessment_id");
                    int studentId = rs.getInt("student_id");

                    boolean isStudentNameChanged = false;
                    boolean isAssessmentRecordChanged = false;

                    if (SName == null || SName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Student name cannot be empty. Update canceled.");
                        return;
                    }

                    // Check if the student name has changed
                    if (!studentName.equals(SName)) {
                        isStudentNameChanged = true;
                        String[] nameParts = SName.split(", ");
                        if (nameParts.length != 2) {
                            JOptionPane.showMessageDialog(null, "Invalid student name format. Please use Lastname, Firstname MiddleName format.");
                            return;
                        }

                        String lastName = nameParts[0];
                        String firstAndMiddleName = nameParts[1].trim(); // Trim to remove leading/trailing spaces

                        // Find the last space in the firstAndMiddleName
                        int lastSpaceIndex = firstAndMiddleName.lastIndexOf(" ");
                        String firstName;
                        String middleName;

                        if (lastSpaceIndex == -1) {
                            // If there is no space, the entire firstAndMiddleName is considered as the first name
                            firstName = firstAndMiddleName;
                            middleName = "";
                        } else {
                            // Split the firstAndMiddleName into first name and middle name using the last space
                            firstName = firstAndMiddleName.substring(0, lastSpaceIndex);
                            middleName = firstAndMiddleName.substring(lastSpaceIndex + 1);
                        }
                        
                        // Check if the middle name exceeds two letters
                        if (middleName.length() > 2) {
                            JOptionPane.showMessageDialog(null, "Middle name should have a maximum of two letters.");
                            return;
                        }

                        String updateStudentQuery = "UPDATE students SET last_name = ?, first_name = ?, middle_name = ? "
                                + "WHERE student_id = ?;";
                        PreparedStatement updateStudentStmt = conn.prepareStatement(updateStudentQuery);

                        // Set the parameters for the prepared statement
                        updateStudentStmt.setString(1, lastName);
                        updateStudentStmt.setString(2, firstName);
                        updateStudentStmt.setString(3, middleName);
                        updateStudentStmt.setInt(4, studentId);

                        int rowsAffected = updateStudentStmt.executeUpdate();
                        System.out.println("Student record updated. Rows affected: " + rowsAffected);
                        isStudentNameChanged = true;
                    }

                    // Check if the assessment record has changed
                    if (item != items || score != scores) {
                        String updateQuery = "UPDATE assessments SET item = ?, score = ? "
                                + "WHERE assessment_id = ?;";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);

                        updateStmt.setInt(1, items);
                        updateStmt.setInt(2, scores);
                        updateStmt.setInt(3, assessmentId);

                        int rowsAffected = updateStmt.executeUpdate();
                        System.out.println("Assessment record updated. Rows affected: " + rowsAffected);
                        isAssessmentRecordChanged = true;
                    }

                    // Show appropriate message based on changes
                    if (isStudentNameChanged && isAssessmentRecordChanged) {
                        JOptionPane.showMessageDialog(null, "Student name and assessment record updated successfully.");
                    } else if (isStudentNameChanged) {
                        JOptionPane.showMessageDialog(null, "Student name updated successfully.");
                    } else if (isAssessmentRecordChanged) {
                        JOptionPane.showMessageDialog(null, "Assessment record updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "No changes made to the record.");
                    }
                    
                    // Clear inputs
                    txtSName.setText("");
                    txtCourse.setText("");
                    txtYearLvl.setText("");
                    txtSubject.setText("");
                    txtAssessmentType.setText("");
                    txtAssessNum.setText("");
                    txtItems.setText("");
                    txtScores.setText("");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // System.out.println("Update Failed");
        }
    }
}
