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
    private JLabel formLabels[][];

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

        //TODO: action with dialogs for (reset/halt) and handle status
        //TODO: general tab (running: cpu/mem used + extended tooltip, notes)
        //TODO: better GuestOS
        //TODO: auto refresh every 3 sec

	// layout
	content.setLayout(new BorderLayout(0, 0));

	//labels
	formLabels = new JLabel[9][2];
	for (int i = 0; i < formLabels.length; i++) {
	    for (int j = 0; j < formLabels[i].length; j++) {
		formLabels[i][j] = new JLabel();
	    }
	}
	formLabels[0][0].setText("Virtual Machine:");
	formLabels[1][0].setText("Guest OS:");
	formLabels[2][0].setText("CPU:");
	formLabels[3][0].setText("Memory:");
	formLabels[4][0].setText("VMware Tools:");
	formLabels[5][0].setText("IP Addresses:");
	formLabels[6][0].setText("DNS Name:");
	formLabels[7][0].setText("State:");
	formLabels[8][0].setText("Host:");

	// header
	try {
	    machinePanel = new JImagePanel(ImageIO.read(window.getClass().getResource("/orbit/application/resources/header-controller.png")));
	} catch (IOException ioe) {
	    machinePanel = new JImagePanel();
	}
	machinePanel.setPreferredSize(new Dimension(450, 35));
	machinePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	machinePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	machinePanel.add(formLabels[0][0]);

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

	formPanels = new JPanel[formLabels.length];
	infoPanels = new JPanel[(formLabels.length - 1)];
	for (int i = 0; i < formPanels.length; i++) {
	    formPanels[i] = new JPanel();
	    formPanels[i].setLayout(new BorderLayout());
	    if (i > 0) {
		formPanels[(i - 1)].add(formPanels[i], BorderLayout.CENTER);
	    }
	    if (i < (formLabels.length - 1)) {
		infoPanels[i] = new JPanel();
		infoPanels[i].setLayout(new BorderLayout());
		//infoPanels[i].setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		formLabels[(i + 1)][0].setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		formLabels[(i + 1)][0].setPreferredSize(new Dimension(125, 20));
		infoPanels[i].add(formLabels[(i + 1)][0], BorderLayout.WEST);
		infoPanels[i].add(formLabels[(i + 1)][1], BorderLayout.CENTER);
		formPanels[i].add(infoPanels[i], BorderLayout.NORTH);
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
	showVM(virtualMachines[virtualMachineCombo.getSelectedIndex()]);
    }

    /**
     * Show VirtualMachine information
     * @param vm VirtualMachine
     */
    public void showVM(OrbitVirtualMachine vm) {

	// guest os
	formLabels[1][1].setText(vm.getGuestOSName());

	// hardware
	formLabels[2][1].setText(vm.getHardware().getNumCPU() + " vCPU");
	formLabels[3][1].setText(vm.getHardware().getMemoryMB() + "MB");

	// tools
	if (vm.isToolsInstalled()) {
	    if (vm.isToolsRunning()) {
		if (vm.isToolsUpgradable()) {
		    formLabels[4][1].setText("Running (needs upgrade)");
		} else if (vm.isToolsUnmanaged()) {
		    formLabels[4][1].setText("Unmanaged");
		} else {
		    formLabels[4][1].setText("Running");
		}
	    } else {
		formLabels[4][1].setText("Not Running");
	    }

	} else {
	    formLabels[4][1].setText("Not Installed");
	}

	// ips
	formLabels[5][1].setText(vm.getGuestPrimaryIP());
	if (vm.getGuestIPs() == null) {
	    formLabels[5][1].setToolTipText("");
	} else {
	    String ips = "";
	    for (String ip : vm.getGuestIPs()) {
		if (!ips.isEmpty()) {
		    ips = ips + ", ";
		}
		ips = ips + ip;
	    }
	    formLabels[5][1].setToolTipText(ips);
	}

	// dns
	formLabels[6][1].setText(vm.getGuestHostName());

	// state
	if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
	    formLabels[7][1].setText("Powered On");
            formLabels[7][1].setForeground(new Color(77, 144, 61));
	} else if (vm.getPowerState() == VirtualMachinePowerState.poweredOff) {
	    formLabels[7][1].setText("Powered Off");
            formLabels[7][1].setForeground(new Color(184, 45, 45));
	} else if (vm.getPowerState() == VirtualMachinePowerState.suspended) {
	    formLabels[7][1].setText("Suspended");
            formLabels[7][1].setForeground(new Color(219, 174, 18));
	} else {
	    formLabels[7][1].setText("");
            formLabels[7][1].setForeground(Color.black);
	}

	// host
	formLabels[8][1].setText(vm.getHost().getName());

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
	    showVM(virtualMachines[virtualMachineCombo.getSelectedIndex()]);
	}
    }
}
