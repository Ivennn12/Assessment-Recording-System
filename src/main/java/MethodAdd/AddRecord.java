/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MethodAdd;

import OtherMethods.DatabaseConnector;
import OtherMethods.InputValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Iven
 */
public class AddRecord {
    
    // Global Variable
    private JTextField txtFName;
    private JTextField txtMName;
    private JTextField txtLName;
    private JComboBox<String> cbxCourse;
    private JComboBox<String> cbxYearLvl;
    private JComboBox<String> cbxSubjects;
    private JTextField txtScode;
    private JComboBox<String> cbxAssessmentType;
    private JTextField txtNumOfItems;
    private JTextField txtTotalScore;
    private JSpinner spnrAssessmentNum;
    private JTable tblRecent;
    
    // Constructor
    public AddRecord(JTextField txtFName, JTextField txtMName, JTextField txtLName,
                     JComboBox<String> cbxCourse, JComboBox<String> cbxYearLvl,
                     JComboBox<String> cbxSubjects, JTextField txtScode,
                     JComboBox<String> cbxAssessmentType, JTextField txtNumOfItems,
                     JTextField txtTotalScore, JSpinner spnrAssessmentNum,
                     JTable tblRecent) {
        
        this.txtFName = txtFName;
        this.txtMName = txtMName;
        this.txtLName = txtLName;
        this.cbxCourse = cbxCourse;
        this.cbxYearLvl = cbxYearLvl;
        this.cbxSubjects = cbxSubjects;
        this.txtScode = txtScode;
        this.cbxAssessmentType = cbxAssessmentType;
        this.txtNumOfItems = txtNumOfItems;
        this.txtTotalScore = txtTotalScore;
        this.spnrAssessmentNum = spnrAssessmentNum;
        this.tblRecent = tblRecent;
    }
    
    public void addRecord() {

        String firstName = txtFName.getText();
        
        String middleName = txtMName.getText();
        middleName = middleName.isEmpty() ? "" : middleName + ".";
        
        String lastName = txtLName.getText();
        String course = (String) cbxCourse.getSelectedItem();
        String yearLevel = (String) cbxYearLvl.getSelectedItem();

        String subjectName = (String) cbxSubjects.getSelectedItem();
        String subjectCode = txtScode.getText();

        String assessmentType = (String) cbxAssessmentType.getSelectedItem();
        String items = txtNumOfItems.getText();
        String score = txtTotalScore.getText();
        int assessmentNumber = (int) spnrAssessmentNum.getValue();
        
        boolean isInvalid = InputValidator.isInvalidInput(txtNumOfItems.getText(), txtTotalScore.getText(), txtFName, txtLName, txtNumOfItems, txtTotalScore, cbxCourse);
        if (isInvalid) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                int studentId = -1;
                int subjectId = -1;
                int studentSubjectId = -1;

                // Check if student already exists
                String checkStudentQuery = "SELECT student_id FROM students WHERE first_name = ? AND last_name = ? AND course = ? AND year = ?";
                PreparedStatement checkStudentStmt = conn.prepareStatement(checkStudentQuery);
                checkStudentStmt.setString(1, firstName);
                checkStudentStmt.setString(2, lastName);
                checkStudentStmt.setString(3, course);
                checkStudentStmt.setString(4, yearLevel);
                ResultSet checkStudentRs = checkStudentStmt.executeQuery();

                if (checkStudentRs.next()) {
                    // Student already exists, retrieve the student ID
                    studentId = checkStudentRs.getInt("student_id");
                } else {
                    
                    // Check if student already exists based on first name and last name
                    String checkStudentNameQuery = "SELECT student_id FROM students WHERE first_name = ? AND last_name = ?";
                    PreparedStatement checkStudentNameStmt = conn.prepareStatement(checkStudentNameQuery);
                    checkStudentNameStmt.setString(1, firstName);
                    checkStudentNameStmt.setString(2, lastName);
                    ResultSet checkStudentNameRs = checkStudentNameStmt.executeQuery();

                    if (checkStudentNameRs.next()) {
                        // Student with the same first name and last name already exists.
                        JOptionPane.showMessageDialog(null, "Student with the same name already exists in other course or year.");
                        return;
                    }
                    
                    // Insert into student table
                    String insertStudentSQL = "INSERT INTO students (first_name, last_name, middle_name, course, year) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement studentStmt = conn.prepareStatement(insertStudentSQL, Statement.RETURN_GENERATED_KEYS);
                    studentStmt.setString(1, firstName);
                    studentStmt.setString(2, lastName);
                    studentStmt.setString(3, middleName);
                    studentStmt.setString(4, course);
                    studentStmt.setString(5, yearLevel);
                    studentStmt.executeUpdate();

                    ResultSet generatedKeys = studentStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        studentId = generatedKeys.getInt(1); // Retrieve the generated student ID
                    }
                }

                // Retrieve subject ID
                String subjectIdQuery = "SELECT subject_id FROM subjects WHERE subject_name = ? AND subject_code = ?";
                PreparedStatement subjectIdStmt = conn.prepareStatement(subjectIdQuery);
                subjectIdStmt.setString(1, subjectName);
                subjectIdStmt.setString(2, subjectCode);
                ResultSet subjectIdRs = subjectIdStmt.executeQuery();

                if (subjectIdRs.next()) {
                    subjectId = subjectIdRs.getInt("subject_id");
                }

                // Retrieve student_subject_id
                String studentSubjectIdQuery = "SELECT student_subject_id FROM student_subjects WHERE student_id = ? AND subject_id = ?";
                PreparedStatement studentSubjectIdStmt = conn.prepareStatement(studentSubjectIdQuery);
                studentSubjectIdStmt.setInt(1, studentId);
                studentSubjectIdStmt.setInt(2, subjectId);
                ResultSet studentSubjectIdRs = studentSubjectIdStmt.executeQuery();

                if (studentSubjectIdRs.next()) {
                    studentSubjectId = studentSubjectIdRs.getInt("student_subject_id");
                } else {
                    // Insert into student_subjects table
                    String insertStudentSubjectSQL = "INSERT INTO student_subjects (student_id, subject_id) VALUES (?, ?)";
                    PreparedStatement studentSubjectStmt = conn.prepareStatement(insertStudentSubjectSQL, Statement.RETURN_GENERATED_KEYS);
                    studentSubjectStmt.setInt(1, studentId);
                    studentSubjectStmt.setInt(2, subjectId);
                    studentSubjectStmt.executeUpdate();

                    ResultSet generatedKeys = studentSubjectStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        studentSubjectId = generatedKeys.getInt(1); // Retrieve the generated student_subject_id
                    }
                }

                // Check for existing record with the same details
                String checkExistingQuery = "SELECT * FROM assessments "
                        + "WHERE student_subject_id = ? AND assessment_type = ? AND assessment_no = ?";
                PreparedStatement checkExistingStmt = conn.prepareStatement(checkExistingQuery);
                checkExistingStmt.setInt(1, studentSubjectId);
                checkExistingStmt.setString(2, assessmentType);
                checkExistingStmt.setInt(3, assessmentNumber);
                ResultSet existingRs = checkExistingStmt.executeQuery();

                if (existingRs.next()) {
                    // Record already exists
                    System.out.println("Record already exists.");
                    JOptionPane.showMessageDialog(null, "Record already exists.");
                    
                } else {
                    // Insert into assessment table
                    String insertAssessmentSQL = "INSERT INTO assessments (student_subject_id, assessment_type, assessment_no, item, score) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement assessmentStmt = conn.prepareStatement(insertAssessmentSQL);
                    assessmentStmt.setInt(1, studentSubjectId);
                    assessmentStmt.setString(2, assessmentType);
                    assessmentStmt.setInt(3, assessmentNumber);
                    assessmentStmt.setInt(4, Integer.parseInt(items));
                    assessmentStmt.setInt(5, Integer.parseInt(score));

                    // Insert the new assessment record
                    assessmentStmt.executeUpdate();
                    System.out.println("Assessment data inserted!");

                    // Display success message or update UI as needed
                    JOptionPane.showMessageDialog(null, "Record added successfully.");

                    // Update the recent table model with the new data
                    String recentData[] = {txtLName.getText() + ", " + txtFName.getText() + " " + txtMName.getText(), cbxCourse.getSelectedItem().toString() + "-" + cbxYearLvl.getSelectedItem().toString(), cbxSubjects.getSelectedItem().toString(), cbxAssessmentType.getSelectedItem().toString(), spnrAssessmentNum.getValue().toString(), txtNumOfItems.getText(), txtTotalScore.getText()};
                    DefaultTableModel tblModel = (DefaultTableModel) tblRecent.getModel();
                    tblModel.addRow(recentData);
                }

                conn.close(); // Close the database connection

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "An error occurred while inserting the record.");
                e.printStackTrace();
            }
        }
    }
}
