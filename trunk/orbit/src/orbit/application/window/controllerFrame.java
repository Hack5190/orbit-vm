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

// orbit
import orbit.component.*;
import orbit.library.*;

public class controllerFrame extends JFrame {

    // variables
    private ServiceInstance si;
    private controllerFrame window;
    private Container content;
    private Properties config;
    private OrbitVirtualMachine[] virtualMachines;
    private JToolBar vmControlToolBar;
    private JButton startButton, stopButton, resetButton;
    private JComboBox virtualMachineCombo;
    private JLabel generalInfoLabels[][];
    private JTextArea notesArea;
    private VMUpdateTimer vut;

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
        window.setTitle("Orbit Controller");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent w) {
                if (new Boolean(config.getProperty("interface.close", "true"))) {
                    System.exit(0);
                } else {
                    // create login window
                    JFrame loginWindow = new loginFrame();
                    loginWindow.setVisible(true);

                    // disconnect
                    si.getServerConnection().logout();

                    // dispose this window
                    window.dispose();
                }
            }
        });
        window.setResizable(false);
        window.setSize(new Dimension(600, 360));
        window.setPreferredSize(new Dimension(600, 360));
        window.setMaximumSize(new Dimension(600, 360));
        window.centerScreen();
        // icon
        try {
            window.setIconImage(ImageIO.read(window.getClass().getResource("/orbit/application/resources/icons/orbit-icon-16.png")));
        } catch (Exception e) {
            // nothing to do
        }

        // collect virtual machines
        virtualMachines = (new OrbitVirtualMachineManager(serviceInstant)).getAllVirualMachines();

        // exit if no vm's
        if (virtualMachines == null) {
            JOptionPane.showMessageDialog(null,
                    "No virtual machines where found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-404);
        }

        // gui
        window.createGUI();
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
     * Create GUI
     */
    public void createGUI() {
        // locals
        JImagePanel machinePanel;
        JPanel vmInfoPanel, vmGeneralPanel, vmResourcePanel;

        JPanel formPanels[], infoPanels[];

        //TODO: window catch focus to stop/start timeout?
        //TODO: action with dialogs halt/shutdown/reset/restart
        //TODO: resource tab (with advance labels??)
        
        // layout
        content.setLayout(new BorderLayout(0, 0));

        //labels
        generalInfoLabels = new JLabel[10][2];
        for (int i = 0; i < generalInfoLabels.length; i++) {
            for (int j = 0; j < generalInfoLabels[i].length; j++) {
                generalInfoLabels[i][j] = new JLabel();
            }
        }
        generalInfoLabels[0][0].setText("Virtual Machine:");
        generalInfoLabels[1][0].setText("Guest OS:");
        generalInfoLabels[2][0].setText("CPU:");
        generalInfoLabels[3][0].setText("Memory:");
        generalInfoLabels[4][0].setText("VMware Tools:");
        generalInfoLabels[5][0].setText("IP Addresses:");
        generalInfoLabels[6][0].setText("DNS Name:");
        generalInfoLabels[7][0].setText("State:");
        generalInfoLabels[8][0].setText("Host:");
        generalInfoLabels[9][0].setText("Notes:");

        // header
        try {
            machinePanel = new JImagePanel(ImageIO.read(window.getClass().getResource("/orbit/application/resources/header-controller.png")));
        } catch (IOException ioe) {
            machinePanel = new JImagePanel();
        }
        machinePanel.setPreferredSize(new Dimension(450, 35));
        machinePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        machinePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        machinePanel.add(generalInfoLabels[0][0]);

        virtualMachineCombo = new JComboBox();
        for (OrbitVirtualMachine vm : virtualMachines) {
            virtualMachineCombo.addItem(vm.getName());
        }
        machinePanel.add(virtualMachineCombo);
        new virtualMachineComboChange(virtualMachineCombo);
        content.add(machinePanel, BorderLayout.NORTH);

        // main
        vmInfoPanel = new JPanel();
        vmInfoPanel.setLayout(new GridLayout(1, 2));
        content.add(vmInfoPanel, BorderLayout.CENTER);

        vmGeneralPanel = new JPanel();
        vmGeneralPanel.setLayout(new GridLayout(1, 1));
        vmGeneralPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " General "));
        vmInfoPanel.add(vmGeneralPanel);

        vmResourcePanel = new JPanel();
        vmResourcePanel.setLayout(new GridLayout(1, 1));
        vmResourcePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Resources "));
        vmInfoPanel.add(vmResourcePanel);

        notesArea = new JTextArea();
        notesArea.setEditable(false);
        notesArea.setAutoscrolls(true);
        notesArea.setBorder(BorderFactory.createEtchedBorder());

        formPanels = new JPanel[generalInfoLabels.length];
        infoPanels = new JPanel[(generalInfoLabels.length - 1)];
        for (int i = 0; i < formPanels.length; i++) {
            formPanels[i] = new JPanel();
            formPanels[i].setLayout(new BorderLayout());
            if (i > 0) {
                formPanels[(i - 1)].add(formPanels[i], BorderLayout.CENTER);
            }
            if (i < (generalInfoLabels.length - 1)) {
                infoPanels[i] = new JPanel();
                infoPanels[i].setLayout(new BorderLayout());
                //infoPanels[i].setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
                generalInfoLabels[(i + 1)][0].setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                generalInfoLabels[(i + 1)][0].setPreferredSize(new Dimension(125, 20));
                infoPanels[i].add(generalInfoLabels[(i + 1)][0], BorderLayout.WEST);
                infoPanels[i].add(generalInfoLabels[(i + 1)][1], BorderLayout.CENTER);
                formPanels[i].add(infoPanels[i], BorderLayout.NORTH);
            } else {
                formPanels[i].add(notesArea, BorderLayout.CENTER);
            }

        }

        // spacing
        infoPanels[2].setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        infoPanels[5].setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        vmGeneralPanel.add(formPanels[0], BorderLayout.CENTER);

        // footer
        vmControlToolBar = new JToolBar("Virtual Machine Controls");
        vmControlToolBar.setFloatable(true);
        content.add(vmControlToolBar, BorderLayout.SOUTH);

        // control buttons
        startButton = new JButton();
        new controleButtonClick(startButton, "start");
        vmControlToolBar.add(startButton);

        stopButton = new JButton();
        new controleButtonClick(stopButton, "stop");
        vmControlToolBar.add(stopButton);

        resetButton = new JButton();
        new controleButtonClick(resetButton, "reset");
        vmControlToolBar.add(resetButton);

        // load data
        new ShowVM(true).start();
    }

    /**
     * Show VirtualMachine information
     * @param vm VirtualMachine
     */
    class ShowVM extends Thread {

        public ShowVM(boolean clearLabels) {

            if (vut == null) {
                vut = new VMUpdateTimer(3000, false);
            }

            if (clearLabels) {
                notesArea.setText("");

                for (int i = 0; i < generalInfoLabels.length; i++) {
                    generalInfoLabels[i][1].setText("");
                }
            }
        }

        @Override
        public void run() {
            // locals
            VirtualHardware vh;
            VirtualMachinePowerState vp;

            // stop timer
            vut.getTimer().stop();

            // get vm
            OrbitVirtualMachine vm = virtualMachines[virtualMachineCombo.getSelectedIndex()];

            // guest os
            generalInfoLabels[1][1].setText(vm.getGuestOSName());

            // hardware
            vh = vm.getHardware();
            generalInfoLabels[2][1].setText(vh.getNumCPU() + " vCPU");
            generalInfoLabels[3][1].setText(vh.getMemoryMB() + "MB");

            // tools
            if (vm.isToolsInstalled()) {
                if (vm.isToolsRunning()) {
                    if (vm.isToolsUpgradable()) {
                        generalInfoLabels[4][1].setText("Running (needs upgrade)");
                    } else if (vm.isToolsUnmanaged()) {
                        generalInfoLabels[4][1].setText("Unmanaged");
                    } else {
                        generalInfoLabels[4][1].setText("Running");
                    }
                } else {
                    generalInfoLabels[4][1].setText("Not Running");
                }

            } else {
                generalInfoLabels[4][1].setText("Not Installed");
            }

            // ips
            generalInfoLabels[5][1].setText(vm.getGuestPrimaryIP());
            if (vm.getGuestIPs() == null) {
                generalInfoLabels[5][1].setToolTipText("");
            } else {
                String ips = "";
                for (String ip : vm.getGuestIPs()) {
                    if (!ips.isEmpty()) {
                        ips = ips + ", ";
                    }
                    ips = ips + ip;
                }
                generalInfoLabels[5][1].setToolTipText(ips);
            }

            // dns
            generalInfoLabels[6][1].setText(vm.getGuestHostName());

            // state
            vp = vm.getPowerState();
            if (vp == VirtualMachinePowerState.poweredOn) {
                generalInfoLabels[7][1].setText("Powered On");
                generalInfoLabels[7][1].setForeground(new Color(77, 144, 61));
            } else if (vp == VirtualMachinePowerState.poweredOff) {
                generalInfoLabels[7][1].setText("Powered Off");
                generalInfoLabels[7][1].setForeground(new Color(184, 45, 45));
            } else if (vp == VirtualMachinePowerState.suspended) {
                generalInfoLabels[7][1].setText("Suspended");
                generalInfoLabels[7][1].setForeground(new Color(219, 174, 18));
            } else {
                generalInfoLabels[7][1].setText("");
                generalInfoLabels[7][1].setForeground(Color.black);
            }

            // host
            generalInfoLabels[8][1].setText(vm.getHost().getName());

            // notes
            try {
                notesArea.setText(vm.getVirtualMachine().getSummary().getConfig().getAnnotation());
            } catch (Exception e) {
                notesArea.setText("");
            }

            // toolbar
            try {
                if (vp == VirtualMachinePowerState.poweredOn) {
                    startButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-suspend.png")));
                } else if (vm.getPowerState() == VirtualMachinePowerState.suspended) {
                    startButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-poweron.png")));
                } else {
                    startButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-poweron.png")));
                }
                resetButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-reset.png")));
                stopButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-poweroff.png")));
            } catch (Exception e) {
                if (vp == VirtualMachinePowerState.poweredOn) {
                    startButton.setText("Suspend");
                } else {
                    startButton.setText("Power On");
                }
                if (vm.isToolsRunning()) {
                    resetButton.setText("Restart");
                } else {
                    resetButton.setText("Reset");
                }
                if (vm.isToolsRunning()) {
                    stopButton.setText("Shutdown");
                } else {
                    stopButton.setText("Power Off");
                }

            } finally {
                if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
                    stopButton.setVisible(true);
                    resetButton.setVisible(true);
                } else {
                    stopButton.setVisible(false);
                    resetButton.setVisible(false);
                }
            }

            // run timer
            vut.getTimer().start();
        }
    }

    class VMUpdateTimer implements ActionListener {

        // locals
        javax.swing.Timer t;

        public VMUpdateTimer(int interval, boolean start) {
            t = new javax.swing.Timer(interval, this);
            if (start) {
                t.start();
            }
        }

        public javax.swing.Timer getTimer() {
            return t;
        }

        public void actionPerformed(ActionEvent e) {
            new ShowVM(false).start();
        }
    }

    /**
     * controle button click event
     * @author sjorge
     */
    class controleButtonClick implements ActionListener {

        private String action;

        public controleButtonClick(JButton button, String action) {
            button.addActionListener(this);
            this.action = action;
        }

        public void actionPerformed(ActionEvent e) {
            // locals
            OrbitVirtualMachine vm;

            if (virtualMachineCombo.getSelectedIndex() > -1) {
                vm = virtualMachines[virtualMachineCombo.getSelectedIndex()];

                if (vm != null) {
                    if (action.equalsIgnoreCase("start")) {
                        if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
                            vm.suspend();
                        } else {
                            vm.powerOn();
                        }
                    } else if (action.equalsIgnoreCase("stop")) {
                        if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
                            vm.powerOff(true);
                        }
                    } else if (action.equalsIgnoreCase("reset")) {
                        if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
                            vm.reset(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * virtual machine combo change event
     * @author sjorge
     */
    class virtualMachineComboChange implements ActionListener {

        public virtualMachineComboChange(JComboBox combo) {
            combo.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            new ShowVM(true).start();
        }
    }
}
