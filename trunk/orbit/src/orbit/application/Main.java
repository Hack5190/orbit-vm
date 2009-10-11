/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orbit.application;

/**
 * Management Application
 * @author sjorge
 */
/**
 * Imports
 */
import javax.swing.*;
import orbit.application.window.*;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // system look & feel
        try {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { /* do nothing */ }
        
        // create login window
        JFrame loginWindow = new loginFrame(args);
        loginWindow.setVisible(true);

    }
}
