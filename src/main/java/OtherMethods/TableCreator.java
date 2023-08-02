/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OtherMethods;

/**
 *
 * @author Iven
 */
import OtherMethods.DatabaseConnector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator {
    
    public static void createTables() {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create admin table
            String createAdminTableSQL = "CREATE TABLE IF NOT EXISTS admin ("
                    + " admin_id SERIAL PRIMARY KEY,"
                    + " admin VARCHAR(50) NOT NULL,"
                    + " password VARCHAR(50) NOT NULL,"
                    + " hint VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createAdminTableSQL);
            System.out.println("Users table created successfully!");

            // Check if the default user exists
            String checkUserExistsSQL = "SELECT COUNT(*) FROM admin WHERE admin = 'Admin'";
            try (ResultSet rs = stmt.executeQuery(checkUserExistsSQL)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Default user does not exist, perform the insert
                    String insertDefaultUserSQL = "INSERT INTO admin (admin, password, hint) VALUES ('Admin', 'password', 'passw*rd')";
                    stmt.executeUpdate(insertDefaultUserSQL);
                    System.out.println("Admin added successfully!");
                } else {
                    System.out.println("Default user already exists. No insert needed.");
                }
            }
            
            // Create students table
            String createStudentsTableSQL = "CREATE TABLE IF NOT EXISTS students ("
                    + "student_id SERIAL PRIMARY KEY, "
                    + "first_name VARCHAR(50) NOT NULL, "
                    + "last_name VARCHAR(50) NOT NULL, "
                    + "middle_name VARCHAR(10), "
                    + "course VARCHAR(50) NOT NULL, "
                    + "year VARCHAR(50) NOT NULL)";
            stmt.executeUpdate(createStudentsTableSQL);
            System.out.println("Students table created successfully!");

            // Create subjects table
            String createSubjectsTableSQL = "CREATE TABLE IF NOT EXISTS subjects ("
                    + "subject_id SERIAL PRIMARY KEY, "
                    + "subject_name VARCHAR(50) NOT NULL, "
                    + "subject_code VARCHAR(50) NOT NULL, "
                    + "description VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createSubjectsTableSQL);
            System.out.println("Subjects table created successfully!");

            // Create student_subjects table
            String createStudentSubjectsTableSQL = "CREATE TABLE IF NOT EXISTS student_subjects ("
                    + "student_subject_id SERIAL PRIMARY KEY, "
                    + "student_id INT NOT NULL, "
                    + "subject_id INT NOT NULL, "
                    + "FOREIGN KEY (student_id) REFERENCES students (student_id), "
                    + "FOREIGN KEY (subject_id) REFERENCES subjects (subject_id))";
            stmt.executeUpdate(createStudentSubjectsTableSQL);
            System.out.println("Student_subjects table created successfully!");

            // Create assessments table
            String createAssessmentsTableSQL = "CREATE TABLE IF NOT EXISTS assessments ("
                    + "assessment_id SERIAL PRIMARY KEY, "
                    + "student_subject_id INT NOT NULL, "
                    + "assessment_type VARCHAR(50) NOT NULL, "
                    + "assessment_no INT NOT NULL, "
                    + "item INT NOT NULL, "
                    + "score INT NOT NULL, "
                    + "FOREIGN KEY (student_subject_id) REFERENCES student_subjects (student_subject_id), "
                    + "CONSTRAINT uc_assessment_record UNIQUE (assessment_id, student_subject_id, assessment_type, assessment_no))";
            stmt.executeUpdate(createAssessmentsTableSQL);
            System.out.println("Assessments table created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Table creation failed!");
        }
    }
}
