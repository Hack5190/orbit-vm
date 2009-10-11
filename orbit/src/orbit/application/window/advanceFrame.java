package orbit.application.window;

/**
 * Advance Mangement Interface
 * @author sjorge
 */
/**
 * Imports
 */
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

// vijava
import java.net.*;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class advanceFrame extends JFrame {

    // variables
    private ServiceInstance si;
    private advanceFrame window;
    private Container content;

    /**
     * advanceFrame Constructor
     * @param si ServiceInstance
     */
    public advanceFrame(String name, ServiceInstance si) {
        // self reference
        window = this;

        // get content
        content = window.getContentPane();

        // main windows setup
        window.setTitle(String.format("Orbit Manager (%s)", name));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setSize(new Dimension(750, 500));
        window.setPreferredSize(new Dimension(750, 500));
        //TODO: maximize on open (also check into mac/linux)
        window.centerScreen();
        // icon
        try {
            window.setIconImage(ImageIO.read(window.getClass().getResource("/orbit/application/resources/icons/orbit-icon-16.png")));
        } catch (Exception e) {
            // nothing to do
        }

        // gui
        window.createGUI();
        window.attachEvents();

    }

    /**
     * Center on screen
     */
    public void centerScreen() {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        window.setLocation(
                (dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
        window.requestFocusInWindow();
    }

    /**
     * Attach events to components
     */
    public void attachEvents() {
    }

    /**
     * Create GUI
     */
    public void createGUI() {
        //TODO: advance management gui
    }
}
