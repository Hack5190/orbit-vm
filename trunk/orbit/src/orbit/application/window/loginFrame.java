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
import java.lang.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

// vijava
import java.net.*;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import orbit.library.*;

public class loginFrame extends JFrame {

    // variables
    private loginFrame window;
    private Container content;
    private Properties config;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JLabel[] formLabels;
    private JTextField serverText;
    private JTextField loginText;
    private JPasswordField passwordText;
    private JButton loginButton;
    private JButton closeButton;
    private JCheckBox tunnelCheck;
    private JTextField sshHostText;
    private JTextField sshUserText;
    private JPasswordField sshPassText;

    /**
     * loginFrame Constructor
     * @param args command line args
     */
    public loginFrame() {
	// self reference
	window = this;

	// get content
	content = window.getContentPane();

	// configuration
	config = readConfiguration();

	// main windows setup
	window.setTitle("Orbit Manager");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setResizable(false);
	window.setSize(new Dimension(350, 270));
	window.setPreferredSize(new Dimension(350, 270));
	window.setMaximumSize(new Dimension(350, 270));
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

	//TODO: ssh tunneling (http://www.beanizer.org/site/index.php/en/Articles/Java-ssh-tunneling-with-jsch.html)
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
	new closeButtonClick(closeButton);
	new tunnelCheckClick(tunnelCheck);
	new loginButtonClick(loginButton);
    }

    public Properties readConfiguration() {
	// locals
	Properties config = new Properties();

	try {
	    // load
	    if (new File("orbit.properties").exists()) {
		config.load(new FileInputStream("orbit.properties"));
	    }
	} catch (Exception e) {
	}

	return config;
    }

    /**
     * Store Session
     */
    public void storeSession() {
	try {
	    // set recent server/user
	    config.setProperty("recent.server", serverText.getText());
	    config.setProperty("recent.login", loginText.getText());
	    config.setProperty("recent.ssh.enable", Boolean.toString(tunnelCheck.isSelected()));
	    config.setProperty("recent.ssh.host", sshHostText.getText());
	    config.setProperty("recent.ssh.username", sshUserText.getText());

	    // save file
	    config.store(new FileOutputStream("orbit.properties"), null);
	} catch (IOException e) {
	    // we gave it our best
	}
    }

    /**
     * Restore Session
     */
    public void restoreSession() {
	// set recent server/user
	serverText.setText(config.getProperty("recent.server", ""));
	loginText.setText(config.getProperty("recent.login", "root"));
	tunnelCheck.setSelected(Boolean.parseBoolean(config.getProperty("recent.ssh.enable", "false")));
	if (tunnelCheck.isSelected()) {
	    window.setSize(new Dimension(350, 370));
	    window.setPreferredSize(new Dimension(350, 370));
	    window.setMaximumSize(new Dimension(350, 370));
	}

	sshHostText.setText(config.getProperty("recent.ssh.host", ""));
	sshUserText.setText(config.getProperty("recent.ssh.username", ""));

	// focus password if needed
	if (!serverText.getText().isEmpty()) {
	    if (!loginText.getText().isEmpty()) {
		window.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy() {

		    @Override
		    public Component getFirstComponent(Container aContainer) {
			return passwordText;
		    }
		});

	    }
	}
    }

    /**
     * Create GUI
     */
    public void createGUI() {
	// locals
	JPanel formPanels[], serverPanel, loginPanel,
		passwordPanel, buttonPanel, statusPanel,
		tunnelPanel, sshUserPanel, sshPassPanel,
		sshHostPanel;

	// layout
	content.setLayout(new BorderLayout(0, 0));

	// header
	headerLabel = new JLabel();
	headerLabel.setPreferredSize(new Dimension(350, 75));
	try {
	    headerLabel.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/header-login.png")));
	} catch (Exception e) {
	    headerLabel.setBackground(Color.DARK_GRAY);
	    headerLabel.setOpaque(true);
	}
	content.add(headerLabel, BorderLayout.NORTH);

	// main
	formPanels = new JPanel[7];
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
	tunnelPanel = new JPanel();
	tunnelCheck = new JCheckBox();
	sshHostPanel = new JPanel();
	sshHostText = new JTextField();
	sshUserPanel = new JPanel();
	sshUserText = new JTextField();
	sshPassPanel = new JPanel();
	sshPassText = new JPasswordField();

	formLabels = new JLabel[6];
	for (int i = 0; i < formLabels.length; i++) {
	    formLabels[i] = new JLabel();
	    formLabels[i].setPreferredSize(new Dimension(70, 20));
	}

	// form - set labels
	formLabels[0].setText("Server:");
	formLabels[1].setText("Login:");
	formLabels[2].setText("Password:");
	formLabels[3].setText("SSH Server:");
	formLabels[4].setText("Username:");
	formLabels[5].setText("Password:");

	// form - input
	formPanels[0].add(serverPanel, BorderLayout.NORTH);
	serverPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	serverPanel.setLayout(new BorderLayout());
	formLabels[0].setLabelFor(serverText);
	serverPanel.add(formLabels[0], BorderLayout.WEST);
	serverPanel.add(serverText, BorderLayout.CENTER);

	formPanels[1].add(loginPanel, BorderLayout.NORTH);
	loginText.setText("root");
	loginPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	loginPanel.setLayout(new BorderLayout());
	formLabels[1].setLabelFor(loginText);
	loginPanel.add(formLabels[1], BorderLayout.WEST);
	loginPanel.add(loginText, BorderLayout.CENTER);

	formPanels[2].add(passwordPanel, BorderLayout.NORTH);
	passwordPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	passwordPanel.setLayout(new BorderLayout());
	formLabels[2].setLabelFor(passwordText);
	passwordPanel.add(formLabels[2], BorderLayout.WEST);
	passwordPanel.add(passwordText, BorderLayout.CENTER);

	formPanels[3].add(tunnelPanel, BorderLayout.NORTH);
	tunnelPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	tunnelPanel.setLayout(new BorderLayout());
	tunnelCheck.setText("Enable SSH Tunnel");
	tunnelPanel.add(tunnelCheck, BorderLayout.EAST);

	formPanels[4].add(sshHostPanel, BorderLayout.NORTH);
	sshHostPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	sshHostPanel.setLayout(new BorderLayout());
	formLabels[3].setLabelFor(sshHostText);
	sshHostPanel.add(formLabels[3], BorderLayout.WEST);
	sshHostPanel.add(sshHostText, BorderLayout.CENTER);

	formPanels[5].add(sshUserPanel, BorderLayout.NORTH);
	sshUserPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	sshUserPanel.setLayout(new BorderLayout());
	formLabels[4].setLabelFor(sshUserText);
	sshUserPanel.add(formLabels[4], BorderLayout.WEST);
	sshUserPanel.add(sshUserText, BorderLayout.CENTER);

	formPanels[6].add(sshPassPanel, BorderLayout.NORTH);
	sshPassPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
	sshPassPanel.setLayout(new BorderLayout());
	formLabels[5].setLabelFor(sshPassText);
	sshPassPanel.add(formLabels[5], BorderLayout.WEST);
	sshPassPanel.add(sshPassText, BorderLayout.CENTER);

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
     * tunnel checkbox click event
     * @author sjorge
     */
    class tunnelCheckClick implements ActionListener {

	public tunnelCheckClick(JCheckBox check) {
	    check.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
	    if (tunnelCheck.isSelected()) {
		window.setSize(new Dimension(350, 370));
		window.setPreferredSize(new Dimension(350, 370));
		window.setMaximumSize(new Dimension(350, 370));
	    } else {
		window.setSize(new Dimension(350, 270));
		window.setPreferredSize(new Dimension(350, 270));
		window.setMaximumSize(new Dimension(350, 270));
	    }

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
	    this.statusMessage("Connecting...", "processing");

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
		    this.statusMessage("Invalid server name!", "error");
		    serverText.requestFocusInWindow();
		}
	    } else {
		valid = false;
		this.statusMessage("Enter server name.", "alert");
		serverText.requestFocusInWindow();
	    }

	    if (valid && stringUser.isEmpty()) {
		valid = false;
		this.statusMessage("Please enter login.", "alert");
		loginText.requestFocusInWindow();
	    }

	}

	public void statusMessage(String msg) {
	    this.statusMessage(msg, null);
	}

	public void statusMessage(String msg, String icon) {
	    statusLabel.setText(msg);
	    if (icon == null || icon.isEmpty()) {
		statusLabel.setIcon(null);
	    } else {
		try {
		    statusLabel.setIcon(new ImageIcon(window.getClass().getResource(String.format("/orbit/application/resources/statusbar/%s.%s", icon, ((!icon.equalsIgnoreCase("processing")) ? "png" : "gif")))));
		} catch (Exception e) {
		    statusLabel.setIcon(null);
		}
	    }

	    statusLabel.repaint();
	}

	public void run() {
	    // connect
	    if (valid) {
		try {
		    si = new ServiceInstance(urlServer, stringUser, stringPassword, true);
		} catch (com.vmware.vim25.InvalidLogin il) {
		    valid = false;
		    this.statusMessage("Invalid login!", "alert");
		    passwordText.requestFocusInWindow();
		} catch (Exception ex) {
		    valid = false;
		    this.statusMessage("Connection failed!", "error");
		} finally {
		    if (si != null) {
			window.storeSession();
			this.statusMessage("Connected!", "ok");

			// open manager frame
			JFrame managerWindow = ((config.getProperty("interface", "manager").equalsIgnoreCase("controller")) ? new controllerFrame(si, config) : new managerFrame(si, config));
			managerWindow.setVisible(true);
			window.dispose();
		    }
		}
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
