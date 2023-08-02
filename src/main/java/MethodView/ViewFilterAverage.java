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

/**
 *
 * @author Iven
 */
public class ViewFilterAverage {
    
    private JTable tblAverage;
    private JComboBox<String> cbxSelectedCourse;
    private JComboBox<String> cbxSelectedYear;
    private JComboBox<String> cbxSelectedSub;
    
    public ViewFilterAverage(JTable tblAverage, JComboBox<String> cbxSelectedCourse,
                      JComboBox<String> cbxSelectedYear, JComboBox<String> cbxSelectedSub) {
        this.tblAverage = tblAverage;
        this.cbxSelectedCourse = cbxSelectedCourse;
        this.cbxSelectedYear = cbxSelectedYear;
        this.cbxSelectedSub = cbxSelectedSub;
    }
    
    public void filterAverage() {
        String selectedCourse = (String) cbxSelectedCourse.getSelectedItem();
        String selectedYear = (String) cbxSelectedYear.getSelectedItem();
        String selectedSubject = (String) cbxSelectedSub.getSelectedItem();
        
        // Check if one category is selected
        if (selectedCourse.equals("SELECT") || selectedYear.equals("SELECT") || selectedSubject.equals("SELECT")) {
            JOptionPane.showMessageDialog(null, "Please select an item for every category to view average.");
            return; // Exit the method without proceeding further
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            DefaultTableModel tblModel = (DefaultTableModel) tblAverage.getModel();
            tblModel.setRowCount(0); // Clear existing rows

            // Create the base query
            String query = "SELECT DISTINCT s.last_name, s.first_name, s.middle_name, s.course, s.year, sub.subject_name, "
                    + "MAX(CASE WHEN a.assessment_type = 'Quiz' THEN a.percentage * 0.15 END) AS quiz_percentage, "
                    + "MAX(CASE WHEN a.assessment_type = 'Assignment' THEN a.percentage * 0.15 END) AS assignment_percentage, "
                    + "MAX(CASE WHEN a.assessment_type = 'Activity' THEN a.percentage * 0.2 END) AS activity_percentage, "
                    + "MAX(CASE WHEN a.assessment_type = 'Midterm' THEN a.percentage * 0.2 END) AS midterm_percentage, "
                    + "MAX(CASE WHEN a.assessment_type = 'Final' THEN a.percentage * 0.3 END) AS final_percentage, "
                    + "SUM(a.assessment_grade) AS total_grade "
                    + "FROM ( "
                    + "    SELECT a.student_subject_id, a.assessment_type, "
                    + "           (SUM(a.score) * 100) / SUM(a.item) AS percentage, "
                    + "           (SUM(a.score) * 100) / SUM(a.item) * "
                    + "           CASE "
                    + "               WHEN a.assessment_type = 'Quiz' THEN 0.15 "
                    + "               WHEN a.assessment_type = 'Assignment' THEN 0.15 "
                    + "               WHEN a.assessment_type = 'Activity' THEN 0.2 "
                    + "               WHEN a.assessment_type = 'Midterm' THEN 0.2 "
                    + "               WHEN a.assessment_type = 'Final' THEN 0.3 "
                    + "               ELSE 0 "
                    + "           END AS assessment_grade "
                    + "    FROM assessments a "
                    + "    WHERE a.assessment_type IN ('Quiz', 'Assignment', 'Midterm', 'Final', 'Activity') "
                    + "    GROUP BY a.student_subject_id, a.assessment_type "
                    + ") AS a "
                    + "INNER JOIN student_subjects ss ON ss.student_subject_id = a.student_subject_id "
                    + "INNER JOIN students s ON s.student_id = ss.student_id "
                    + "INNER JOIN subjects sub ON sub.subject_id = ss.subject_id "
                    + "WHERE s.course = ? AND s.year = ? AND sub.subject_name = ? "
                    + "GROUP BY s.last_name, s.first_name, s.middle_name, s.course, s.year, sub.subject_name";

            // Prepare the query
            PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, selectedCourse);
                stmt.setString(2, selectedYear);
                stmt.setString(3, selectedSubject);
           
            // Execute the query and fetch the data
            ResultSet rs = stmt.executeQuery();
            
            // Check if any records are found
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No records found for the selected category.");
            } else {

                // Iterate over the result set and add rows to the table model
                while (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String midName = rs.getString("middle_name");
                    String course = rs.getString("course");
                    String year = rs.getString("year");
                    String subject = rs.getString("subject_name");
                    double quizGrade = rs.getDouble("quiz_percentage");
                    double assignmentGrade = rs.getDouble("assignment_percentage");
                    double activityGrade = rs.getDouble("activity_percentage");
                    double midtermGrade = rs.getDouble("midterm_percentage");
                    double finalGrade = rs.getDouble("final_percentage");
                    double overallGrade = rs.getDouble("total_grade");

                    // Do something with the data
                    String[] recordAverage = {
                            lastName + ", " + firstName + " " + midName,
                            course + "-" + year,
                            subject,
                            String.format("%.2f", quizGrade),
                            String.format("%.2f", assignmentGrade),
                            String.format("%.2f", activityGrade),
                            String.format("%.2f", midtermGrade),
                            String.format("%.2f", finalGrade),
                            String.format("%.2f", overallGrade)
                    };
                    tblModel.addRow(recordAverage);
                }
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage());
        }
    }
}
