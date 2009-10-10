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
import javax.swing.*;

public class loginFrame extends JFrame {
    // variables

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
        // get content
        content = this.getContentPane();

        // main windows setup
        this.setTitle("Orbit Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //TODO: icon (somehow get the resources in orbit.application.resources
        this.setResizable(false);
        this.setSize(new Dimension(350, 250));
        this.setPreferredSize(new Dimension(350, 250));
        this.setMaximumSize(new Dimension(350, 250));
        this.centerScreen();

        // gui
        this.createGUI();
        this.attachEvents();
        this.restoreSession();
    }

    /**
     * Center on screen
     */
    public void centerScreen() {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        this.setLocation(
                (dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
        this.requestFocus();
    }

    /**
     * Attach events to components
     */
    public void attachEvents() {
        new closeButtonClick(closeButton);
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
        JPanel formPanels[], serverPanel, loginPanel, passwordPanel, buttonPanel;

        // layout
        content.setLayout(new BorderLayout(0, 0));

        // header
        headerLabel = new JLabel();
        headerLabel.setPreferredSize(new Dimension(350, 75));
        // next 2 lines = temp till header image
        headerLabel.setBackground(Color.DARK_GRAY);
        headerLabel.setOpaque(true);
        // TODO: header image (from resource hopefully)
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

        // form - status/button
        content.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2,5,5,5));
        buttonPanel.setPreferredSize(new Dimension(350, 30));
        buttonPanel.add(statusLabel);
        buttonPanel.add(loginButton);
        buttonPanel.add(closeButton);

        loginButton.setText("Login");
        //TODO: add login code (sjorge)

        closeButton.setText("Close");
        //TODO: add cancel code (sjorge)

    }
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
