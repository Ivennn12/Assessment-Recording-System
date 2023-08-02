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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iven
 */
public class UpdateSubject {
    private JTable tblSubjects;
    private JTextField txtSubName;
    private JTextField txtSubcode;
    private JTextArea txtSubDescription;

    public UpdateSubject(JTable tblSubjects, JTextField txtSubName, JTextField txtSubcode, JTextArea txtSubDescription) {
        this.tblSubjects = tblSubjects;
        this.txtSubName = txtSubName;
        this.txtSubcode = txtSubcode;
        this.txtSubDescription = txtSubDescription;
    }

    public void updateSubject() {
        DefaultTableModel tblModel = (DefaultTableModel) tblSubjects.getModel();
        int selectedRow = tblSubjects.getSelectedRow();

        // Retrieve the data from the selected row
        String subName = tblModel.getValueAt(selectedRow, 0).toString();
        String subCode = tblModel.getValueAt(selectedRow, 1).toString();
        String subDescription = tblModel.getValueAt(selectedRow, 1).toString();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a subject to update.");
            return; // Return early if no row is selected
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            
            // Check if a subject with the exact name exists
//            String checkExactNameQuery = "SELECT subject_id FROM subjects WHERE subject_name = ?";
//            PreparedStatement checkExactNameStmt = conn.prepareStatement(checkExactNameQuery);
//            checkExactNameStmt.setString(1, txtSubName.getText());
//            ResultSet exactNameResultSet = checkExactNameStmt.executeQuery();
//            
//            if (txtSubName.getText().equals(subName) &&
//                txtSubcode.getText().equals(subCode) &&
//                txtSubDescription.getText().equals(subDescription)) {
//
//                // No changes made
//                JOptionPane.showMessageDialog(null, "No changes made to the subject.");
//                return;
//            }
//
//            if (exactNameResultSet.next()) {
//                // Exact name already exists, show warning
//                JOptionPane.showMessageDialog(null, "A subject with the exact same name already exists. Please choose a different name.");
//                return;
//            }

            // Get the subject_id based on the subject name and subject code
            String getSubjectIdQuery = "SELECT subject_id FROM subjects WHERE subject_name = ? AND subject_code = ?";
            PreparedStatement getSubjectIdStmt = conn.prepareStatement(getSubjectIdQuery);
            getSubjectIdStmt.setString(1, subName);
            getSubjectIdStmt.setString(2, subCode);
            ResultSet resultSet = getSubjectIdStmt.executeQuery();

            if (resultSet.next()) {
                int subjectId = resultSet.getInt("subject_id");

                // Update the subject data in the subjects table
                String updateSubjectQuery = "UPDATE subjects SET subject_name = ?, subject_code = ?, description = ? WHERE subject_id = ?";
                PreparedStatement updateSubjectStmt = conn.prepareStatement(updateSubjectQuery);
                updateSubjectStmt.setString(1, txtSubName.getText());
                updateSubjectStmt.setString(2, txtSubcode.getText());
                updateSubjectStmt.setString(3, txtSubDescription.getText());
                updateSubjectStmt.setInt(4, subjectId);
                int rowsAffected = updateSubjectStmt.executeUpdate();

                System.out.println("Rows affected: " + rowsAffected);

            } else {
                System.out.println("Subject not found");
            }

            // Display a success message
            JOptionPane.showMessageDialog(null, "Subject Updated Successfully.");

            // Reset the input fields
            txtSubName.setText("");
            txtSubcode.setText("");
            txtSubDescription.setText("");

            // Update the corresponding row in the table model
            tblModel.setValueAt(txtSubName.getText(), selectedRow, 0);
            tblModel.setValueAt(txtSubcode.getText(), selectedRow, 1);
            tblModel.setValueAt(txtSubDescription.getText(), selectedRow, 2);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Update Failed");
            JOptionPane.showMessageDialog(null, "Update Failed");
        }
    }
}
