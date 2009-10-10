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
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

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
    private JButton cancelButton;

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

    public void createGUI() {
        // locals
        JPanel formPanels[], serverPanel, loginPanel, passwordPanel, buttonPanel;

        // layout
        content.setLayout(new BorderLayout(0, 0));

        // header
        headerLabel = new JLabel();
        headerLabel.setPreferredSize(new Dimension(350, 50));
        // next 2 lines = temp till header image
        headerLabel.setBackground(Color.DARK_GRAY);
        headerLabel.setOpaque(true);
        // TODO: header image (from resource hopefully)
        content.add(headerLabel, BorderLayout.NORTH);

        // main
        formPanels = new JPanel[4];
        for (int i = 0; i < formPanels.length; i++) {
            formPanels[i] = new JPanel();
            if (i > 0) {
                formPanels[(i - 1)].add(formPanels[i], BorderLayout.CENTER);
            }
        }

        formPanels[0].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanels[0].setLayout(new BorderLayout());
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
        cancelButton = new JButton();

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
        loginPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        loginPanel.setLayout(new BorderLayout());
        loginPanel.add(formLabels[1], BorderLayout.WEST);
        loginPanel.add(loginText, BorderLayout.CENTER);

        formPanels[1].add(passwordPanel, BorderLayout.NORTH);
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        passwordPanel.setLayout(new BorderLayout());
        passwordPanel.add(formLabels[2], BorderLayout.WEST);
        passwordPanel.add(passwordText, BorderLayout.CENTER);

        // form - login button
        loginButton.setText("Login");
        //TODO: localtion
        //TODO: attach event
        //TODO: add si login code (sjorge)

        // form - cancel button
        cancelButton.setText("Login");
        //TODO: localtion
        //TODO: attach event
        //TODO: add si cancel code (sjorge)

        // status
        statusLabel = new JLabel();
        statusLabel.setPreferredSize(new Dimension(350, 22));
        statusLabel.setBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        content.add(statusLabel, BorderLayout.SOUTH);
    }
}