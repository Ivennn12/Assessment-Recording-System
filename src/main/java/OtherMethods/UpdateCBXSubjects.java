/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OtherMethods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;

/**
 *
 * @author Iven
 */
public class UpdateCBXSubjects {
    private JComboBox<String> cbxSubjects;
    private JComboBox<String> cbxSelectedSub;
    private JComboBox<String> cbxYourSubject;
    

    public UpdateCBXSubjects(JComboBox<String> cbxSubjects, JComboBox<String> cbxSelectedSub,
                           JComboBox<String> cbxYourSubject) {
        this.cbxSubjects = cbxSubjects;
        this.cbxSelectedSub = cbxSelectedSub;
        this.cbxYourSubject = cbxYourSubject;
        
    }
    
    // Update the subjects combobox
    public void updateSubjectsComboBox() {
        cbxSubjects.removeAllItems();
        cbxSelectedSub.removeAllItems();
        cbxYourSubject.removeAllItems();
        
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM subjects";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Add the subject name to the combobox
                cbxSubjects.addItem(rs.getString("subject_name"));
                cbxSelectedSub.addItem(rs.getString("subject_name"));
                cbxYourSubject.addItem(rs.getString("subject_name"));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
