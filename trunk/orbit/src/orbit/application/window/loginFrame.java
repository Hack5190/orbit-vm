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
    private JPanel mainPanel;
    private JLabel[] formLabels;
    private JTextField serverText;
    private JTextField loginText;
    private JPasswordField passwordText;
    private JButton loginButton;

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
        // layout
        content.setLayout(new BorderLayout(0, 0));

        // header
        headerLabel = new JLabel();
        headerLabel.setPreferredSize(new Dimension(350, 50));
        // TODO: header image (from resource hopefully)
        content.add(headerLabel, BorderLayout.NORTH);

        // main
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //TODO: label input * 3 + one row with right aligned button
        mainPanel.setLayout(null);
        content.add(mainPanel, BorderLayout.CENTER);

        // form - initilization
        serverText = new JTextField();
        mainPanel.add(serverText);

        loginText = new JTextField();
        mainPanel.add(loginText);

        passwordText = new JPasswordField();
        mainPanel.add(passwordText);

        loginButton = new JButton();
        mainPanel.add(loginButton);

        formLabels = new JLabel[3];
        for (int i = 0; i < formLabels.length; i++) {
            formLabels[i] = new JLabel();
            mainPanel.add(formLabels[i]);
        }

        // form - set labels
        formLabels[0].setText("Server:");
        formLabels[1].setText("Login:");
        formLabels[2].setText("Password:");
        //TODO: setLocation/setPrefSize

        // form - input
        //TODO: add server,login and password field
        
        // form - login button
        loginButton.setText("Login");
        //TODO: localtion
        //TODO: attach event
        //TODO: add si login code (sjorge)

        // status
        statusLabel = new JLabel();
        statusLabel.setPreferredSize(new Dimension(350, 22));
        statusLabel.setBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        content.add(statusLabel, BorderLayout.SOUTH);
    }
}
