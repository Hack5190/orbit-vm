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
import javax.swing.JFrame;
import orbit.application.window.*;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //TODO: set system style

        // create initial window
        JFrame loginFrame = new loginFrame();
        loginFrame.setVisible(true);

    }
}
