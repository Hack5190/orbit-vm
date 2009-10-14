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
    private JComboBox virtualMachineCombo;
    private JButton vmSearchButton;
    private JButton[] vmControlButtons;

    /**
     * controllerFrame Constructor
     * @param si ServiceInstance
     */
    public controllerFrame(ServiceInstance serviceInstant, Properties cfg) {
        // self reference
        window = this;
        config = cfg;
        si = serviceInstant;

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

                    // disconnect
                    si.getServerConnection().logout();

                    // dispose this window
                    window.dispose();
                } else {
                    System.exit(0);
                }
            }
        });
        window.setResizable(false);
        window.setSize(new Dimension(450, 260));
        window.setPreferredSize(new Dimension(450, 260));
        window.setMaximumSize(new Dimension(450, 260));
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
        window.getVirtualMachines();
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
        // locals
        JPanel controlPanel, machinePanel, infoPanel;
        JLabel[] formLabels;
        String[] vmSearchButtonsText = {"Start", "Stop", "Reset"};

        //TODO: create smaller header image
        //TODO: borders for correct spacing simular to loginWindow
        //TODO: infoPanel (maybe move current header into info and add header image?)

        // layout
        content.setLayout(new BorderLayout(0, 0));

        //labels
        formLabels = new JLabel[6];
        for (int i = 0; i < formLabels.length; i++) {
            formLabels[i] = new JLabel();
        }

        // header
        machinePanel = new JPanel();
        machinePanel.setPreferredSize(new Dimension(450, 30));
        machinePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        content.add(machinePanel, BorderLayout.NORTH);

        formLabels[0].setText("Virtual Machine:");
        machinePanel.add(formLabels[0]);

        virtualMachineCombo = new JComboBox();
        machinePanel.add(virtualMachineCombo);

        /** not sure about this yet, do we need it?
        vmSearchButton = new JButton();
        vmSearchButton.setText("Search");
        machinePanel.add(vmSearchButton);
         */
        // main
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(450, 200));
        content.add(infoPanel, BorderLayout.CENTER);

        // footer
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(450, 30));
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        content.add(controlPanel, BorderLayout.SOUTH);

        vmControlButtons = new JButton[3];
        for (int i = 0; i < vmControlButtons.length; i++) {
            vmControlButtons[i] = new JButton();
            //TODO: replace with icons and remove temp text variable
            vmControlButtons[i].setText(vmSearchButtonsText[i]);
            controlPanel.add(vmControlButtons[i]);
        }

        /**
         * Virtual Machine: <dropdown> <button ... (for searching)>
         * Status: Running
         * CPU: xxxmhz
         * MEM: xxxMB
         * Networking: ip, ip, ip
         * Tools: installed/unmaged/not running
         * ---------------------
         * <button start> <button shutdown/power.off> <button reset>
         */
    }

    /**
     * Get all virtual machine and update combo
     */
    public void getVirtualMachines() {
        // locals
        Folder rootFolder;
        VirtualMachine[] vms;

        // get rootFolder and look for vm's
        try {
            rootFolder = si.getRootFolder();
            ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
            if (mes == null || mes.length == 0) {
                throw new NullPointerException();
            }
            vms = new VirtualMachine[mes.length];
            for (int i = 0; i < mes.length; i++) {
                vms[i] = (VirtualMachine) mes[i];
            }
        } catch (Exception e) {
            // TODO: handle no vms (disable form controls?)
            return;
        }

        //TODO: add vms to combo
        
    }
}
