/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OtherMethods;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Iven
 */
public class InputValidator {
     public static boolean isInvalidInput(String itemText, String scoreText, JTextField txtFName, JTextField txtLName, JTextField txtNumOfItems, JTextField txtTotalScore, JComboBox<String> cbxCourse) {
        
        if (txtFName.getText().isEmpty() || txtLName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please provide student data.");
            return false;
        }

        if (txtNumOfItems.getText().isEmpty() || txtTotalScore.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please provide input on item and/or score.");
            return false;
        }

        try {
            int score = Integer.parseInt(scoreText);
            int item = Integer.parseInt(itemText);

            if (score > item) {
                JOptionPane.showMessageDialog(null, "Score can't be more than the item.");
                return false;
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values for item and score.");
            return false;
        }

        if (cbxCourse.getSelectedItem().equals("SELECT")) {
            JOptionPane.showMessageDialog(null, "Please select a course.");
            return false;
        }
        
        return true;
    }
}
