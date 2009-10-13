package orbit.application.window;

/**
 * Simple VM Controller
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

public class controllerFrame extends JFrame {

    // variables
    private ServiceInstance si;
    private controllerFrame window;
    private Container content;
    private Properties config;

    /**
     * controllerFrame Constructor
     * @param si ServiceInstance
     */
    public controllerFrame(ServiceInstance si, Properties cfg) {
	// self reference
	window = this;
	config = cfg;

	// get content
	content = window.getContentPane();

	// main windows setup
	window.setTitle("Orbit Manager");
	window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	window.addWindowListener(new WindowAdapter() {

	    public void windowClosing(WindowEvent w) {
		if (config.getProperty("close.action", "close").equals("login")) {
		    // create login window
		    JFrame loginWindow = new loginFrame();
		    loginWindow.setVisible(true);

		    // dispose this window
		    window.dispose();
		} else {
		    System.exit(0);
		}
	    }
	});
	window.setResizable(false);
	window.setSize(new Dimension(300, 150));
	window.setPreferredSize(new Dimension(300, 150));
	window.setMaximumSize(new Dimension(300, 150));
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
	//TODO: simple gui
	/**
	 * ---------------------
	 * Host: <dropdown> - summery memory/cpu usage
	 * ---------------------
	 * Virtual Machine: <dropdown>
	 * Status: Running
	 * CPU: xxxmhz
	 * MEM: xxxMB
	 * Networking: ip, ip, ip
	 * Tools: installed/unmaged/not running
	 * ---------------------
	 * <button start> <button shutdown/power.off> <button reset>
	 */
    }
}
