package orbit.application.window;

/**
 * Login Window
 * @author sjorge
 */
/**
 * Imports
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

// vijava
import java.net.*;
import java.rmi.*;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class loginFrame extends JFrame {
    // variables

    private loginFrame window;
    private Container content;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JLabel[] formLabels;
    private JTextField serverText;
    private JTextField loginText;
    private JPasswordField passwordText;
    private JButton loginButton;
    private JButton closeButton;

    public loginFrame() {
	// self reference
	window = this;

	// get content
	content = window.getContentPane();

	// main windows setup
	window.setTitle("Orbit Manager");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setResizable(false);
	window.setSize(new Dimension(350, 250));
	window.setPreferredSize(new Dimension(350, 250));
	window.setMaximumSize(new Dimension(350, 250));
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
	window.restoreSession();
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
	window.requestFocus();
    }

    /**
     * Attach events to components
     */
    public void attachEvents() {
	new closeButtonClick(closeButton);
	new loginButtonClick(loginButton);
    }

    /**
     * Store Session
     */
    public void storeSession() {
	//TODO: store session
    }

    /**
     * Restore Session
     */
    public void restoreSession() {
	//TODO: retrieve session
	//TODO: push to ui
    }

    /**
     * Create GUI
     */
    public void createGUI() {
	// locals
	JPanel formPanels[], serverPanel, loginPanel,
		passwordPanel, buttonPanel, statusPanel;

	// layout
	content.setLayout(new BorderLayout(0, 0));

	// header
	headerLabel = new JLabel();
	headerLabel.setPreferredSize(new Dimension(350, 75));
	try {
	    headerLabel.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/header.png")));
	} catch (Exception e) {
	    headerLabel.setBackground(Color.DARK_GRAY);
	    headerLabel.setOpaque(true);
	}
	content.add(headerLabel, BorderLayout.NORTH);

	// main
	formPanels = new JPanel[3];
	for (int i = 0; i < formPanels.length; i++) {
	    formPanels[i] = new JPanel();
	    formPanels[i].setLayout(new BorderLayout());
	    if (i > 0) {
		formPanels[(i - 1)].add(formPanels[i], BorderLayout.CENTER);
	    }
	}

	formPanels[0].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	content.add(formPanels[0], BorderLayout.CENTER);

	// form - initilization
	serverPanel = new JPanel();
	serverText = new JTextField();
	loginPanel = new JPanel();
	loginText = new JTextField();
	passwordPanel = new JPanel();
	passwordText = new JPasswordField();
	buttonPanel = new JPanel();
	loginButton = new JButton();
	closeButton = new JButton();
	statusPanel = new JPanel();
	statusLabel = new JLabel();

	formLabels = new JLabel[3];
	for (int i = 0; i < formLabels.length; i++) {
	    formLabels[i] = new JLabel();
	    formLabels[i].setPreferredSize(new Dimension(70, 20));
	}

	// form - set labels
	formLabels[0].setText("Server:");
	formLabels[1].setText("Login:");
	formLabels[2].setText("Password:");

	// form - input
	formPanels[0].add(serverPanel, BorderLayout.NORTH);
	serverPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	serverPanel.setLayout(new BorderLayout());
	serverPanel.add(formLabels[0], BorderLayout.WEST);
	serverPanel.add(serverText, BorderLayout.CENTER);

	formPanels[1].add(loginPanel, BorderLayout.NORTH);
	loginText.setText("root");
	loginPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	loginPanel.setLayout(new BorderLayout());
	loginPanel.add(formLabels[1], BorderLayout.WEST);
	loginPanel.add(loginText, BorderLayout.CENTER);

	formPanels[2].add(passwordPanel, BorderLayout.NORTH);
	passwordPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	passwordPanel.setLayout(new BorderLayout());
	passwordPanel.add(formLabels[2], BorderLayout.WEST);
	passwordPanel.add(passwordText, BorderLayout.CENTER);

	// form - statusbar
	content.add(statusPanel, BorderLayout.SOUTH);
	statusPanel.setLayout(new BorderLayout());
	statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
	statusPanel.setPreferredSize(new Dimension(350, 30));
	statusPanel.add(statusLabel, BorderLayout.CENTER);

	// form - buttons
	statusPanel.add(buttonPanel, BorderLayout.EAST);
	buttonPanel.setLayout(new GridLayout(1, 2));
	buttonPanel.setPreferredSize(new Dimension(170, 20));
	buttonPanel.add(loginButton);
	buttonPanel.add(closeButton);

	window.getRootPane().setDefaultButton(loginButton);
	loginButton.setText("Login");
	closeButton.setText("Close");

    }

    /**
     * close button click event
     * @author sjorge
     */
    class closeButtonClick implements ActionListener {

	public closeButtonClick(JButton button) {
	    button.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
	    System.exit(0);
	}
    }

    /**
     * login button click event
     * @author sjorge
     */
    class loginButtonClick implements ActionListener {

	public loginButtonClick(JButton button) {
	    button.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
	    // disable button
	    loginButton.setEnabled(false);
	    loginButton.repaint();

	    // setup connection
	    ServerConnector sc = new ServerConnector(
		    serverText.getText(),
		    loginText.getText(),
		    new String(passwordText.getPassword()));
	    sc.start();

	}
    }

    /**
     * Connection thread
     */
    class ServerConnector extends Thread {

	// variables
	private boolean valid = true;
	private URL urlServer = null;
	private String stringUser, stringPassword;
	private ServiceInstance si = null;

	ServerConnector(String url, String user, String password) {
	    // status
	    this.statusMessage("Connecting ...");

	    // store variables
	    this.stringUser = user;
	    this.stringPassword = password;
	    if (!url.isEmpty()) {
		try {
		    if (url.endsWith("/sdk")) { // allow for full url entry
			urlServer = new URL(url);
		    } else {
			urlServer = new URL("https://" + url + "/sdk");
		    }
		} catch (java.net.MalformedURLException mue) {
		    valid = false;
		    this.statusMessage("Invalid server name!");
		}
	    } else {
		valid = false;
		this.statusMessage("Enter server name.");
	    }

	    if (valid && stringUser.isEmpty()) {
		valid = false;
		this.statusMessage("Please enter login.");
	    }

	}

	public void statusMessage(String msg) {
	    //TODO: statatus icon
	    statusLabel.setText(msg);
	    statusLabel.repaint();
	}

	public void run() {
	    // connect
	    if (valid) {
		try {
		    si = new ServiceInstance(urlServer, stringUser, stringPassword, true);
		} catch (com.vmware.vim25.InvalidLogin il) {
		    valid = false;
		    this.statusMessage("Invalid login!");
		} catch (Exception ex) {
		    valid = false;
		    this.statusMessage("Connection failed!");
		}
		//TODO: if valid, show form
	    }

	    // clean status
	    if (!valid) {
		loginButton.setEnabled(true);
	    }
	    loginButton.repaint();
	    try {
		sleep(2500);
	    } catch (Exception ex) {
	    } finally {
		this.statusMessage("");
	    }
	}
    }
}
