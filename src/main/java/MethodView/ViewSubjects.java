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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iven
 */
public class ViewSubjects {
    private JTable tblSubjects;
    private JTabbedPane tpDashboard;

    public ViewSubjects(JTable tblSubjects, JTabbedPane tpDashboard) {
        this.tblSubjects = tblSubjects;
        this.tpDashboard = tpDashboard;
    }

    public void viewSubjects() {
        DefaultTableModel tblModel = (DefaultTableModel) tblSubjects.getModel();
        tblModel.setRowCount(0); // Clear existing rows

        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM subjects";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            boolean hasRecords = false; // Flag to check if there are records

            while (rs.next()) {
                hasRecords = true; // Set the flag to true if there's at least one record

                String subjectName = rs.getString("subject_name");
                String subjectCode = rs.getString("subject_code");
                String description = rs.getString("description");

                String subjectData[] = { subjectName, subjectCode, description };
                tblModel.addRow(subjectData);
            }

            int pnlAddRecordIndex = 2; // Set the index of pnlAddRecord directly
            int pnlRecordsIndex = 3;
            int pnlAverageIndex = 4;

            if (hasRecords) {
                // If there are records, enable and show the pnlAddRecord panel
                tpDashboard.setEnabledAt(pnlAddRecordIndex, true);
                tpDashboard.setVisible(true);
                
                tpDashboard.setEnabledAt(pnlRecordsIndex, true);
                tpDashboard.setVisible(true);
                
                tpDashboard.setEnabledAt(pnlAverageIndex, true);
                tpDashboard.setVisible(true);
                
            } else {
                // If there are no records, disable and hide the pnlAddRecord panel
                tpDashboard.setEnabledAt(pnlAddRecordIndex, false);
                tpDashboard.setVisible(true);
                
                tpDashboard.setEnabledAt(pnlRecordsIndex, false);
                tpDashboard.setVisible(true);
                
                tpDashboard.setEnabledAt(pnlAverageIndex, false);
                tpDashboard.setVisible(true);
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
