/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MethodView;

import OtherMethods.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ViewFilterRecord {
    
    // GLOBAL VARIABLES
    private JTable tblRecords;
    private JComboBox<String> cbxCourses;
    private JComboBox<String> cbxYearLevel;
    private JComboBox<String> cbxYourSubject;
    private JComboBox<String> cbxAssessmentTypes;

    // CONSTRUCTOR
    public ViewFilterRecord(JTable tblRecords, JComboBox<String> cbxCourses, JComboBox<String> cbxYearLevel,
                      JComboBox<String> cbxYourSubject, JComboBox<String> cbxAssessmentTypes) {
        this.tblRecords = tblRecords;
        this.cbxCourses = cbxCourses;
        this.cbxYearLevel = cbxYearLevel;
        this.cbxYourSubject = cbxYourSubject;
        this.cbxAssessmentTypes = cbxAssessmentTypes;
    }

    //Method to view filtered records
    public void filterData() {
        String selectedCourse = cbxCourses.getSelectedItem().toString();
        String selectedYear = cbxYearLevel.getSelectedItem().toString();
        String selectedSubject = cbxYourSubject.getSelectedItem().toString();
        String selectedAssessmentType = cbxAssessmentTypes.getSelectedItem().toString();
        
        // Check if one category is selected
        if (selectedCourse.equals("SELECT") || selectedYear.equals("SELECT") || selectedSubject.equals("SELECT") || selectedAssessmentType.equals("SELECT")) {
            JOptionPane.showMessageDialog(null, "Please select an item for every category to view records.");
            return; // Exit the method without proceeding further
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            DefaultTableModel tblModel = (DefaultTableModel) tblRecords.getModel();
            tblModel.setRowCount(0); // Clear existing rows

            // Create the base query with conditions
            String query = "SELECT DISTINCT s.first_name, s.last_name, s.middle_name, s.course, s.year, sub.subject_name, a.assessment_type, a.assessment_no, a.item, a.score "
                    + "FROM students s "
                    + "INNER JOIN student_subjects ss ON s.student_id = ss.student_id "
                    + "INNER JOIN subjects sub ON ss.subject_id = sub.subject_id "
                    + "INNER JOIN assessments a ON ss.student_subject_id = a.student_subject_id "
                    + "WHERE s.course = ? AND s.year = ? AND sub.subject_name = ? AND a.assessment_type = ?";

            // Prepare the query
            PreparedStatement statement = conn.prepareStatement(query);

            // Set parameter values
            statement.setString(1, selectedCourse);
            statement.setString(2, selectedYear);
            statement.setString(3, selectedSubject);
            statement.setString(4, selectedAssessmentType);

            // Execute the query and fetch the data
            ResultSet resultSet = statement.executeQuery();

            // Check if the result set is empty
            if (!resultSet.isBeforeFirst()) { // The isBeforeFirst() method returns true if the result set is empty
                JOptionPane.showMessageDialog(null, "No records found for the selected category.");
            } else {
                
                // Iterate over the result set and add rows to the table model
                while (resultSet.next()) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String midName = resultSet.getString("middle_name");
                    String course = resultSet.getString("course");
                    String year = resultSet.getString("year");
                    String subjectName = resultSet.getString("subject_name");
                    String assessmentType = resultSet.getString("assessment_type");
                    int assessmentNo = resultSet.getInt("assessment_no");
                    int item = resultSet.getInt("item");
                    int score = resultSet.getInt("score");

                    Object[] row = {
                            lastName + ", " + firstName + " " + midName,
                            course + "-" + year,
                            subjectName,
                            assessmentType,
                            assessmentNo,
                            item,
                            score
                    };
                    tblModel.addRow(row);
                }
            }

            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }
}

