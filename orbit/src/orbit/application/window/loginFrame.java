/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbit.application.window;

/**
 * Login Window
 * @author sjorge
 */

/**
 * Imports
 */
import javax.swing.*;
import java.awt.*;

public class loginFrame extends JFrame {
    // variables
    private Container content;

    public loginFrame() {
        // get content
        content = this.getContentPane();

        // main windows setup
        this.setTitle("Orbit Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //TODO: icon
        this.setResizable(false);
        this.setSize(new Dimension(350, 250));
        this.setPreferredSize(new Dimension(350, 250));
        this.setMaximumSize(new Dimension(350, 250));
        //TODO: center window

    }
}