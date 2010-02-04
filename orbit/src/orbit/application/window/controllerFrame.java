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
    private OrbitSSHTunnel tun;
    private controllerFrame window;
    private Container content;
    private Properties config;
    private OrbitVirtualMachine[] virtualMachines;
    private JToolBar vmControlToolBar;
    private JButton startButton, stopButton, resetButton;
    private JComboBox virtualMachineCombo;
    private JLabel generalInfoLabels[][];
    private JLabel resourceInfoLabels[][];
    private JTextArea notesArea;
    private VMUpdateTimer vut;

    /**
     * controllerFrame Constructor
     * @param si ServiceInstance
     */
    public controllerFrame(ServiceInstance serviceInstant, OrbitSSHTunnel tunnel, Properties cfg) {
	// self reference
	window = this;
	config = cfg;
	si = serviceInstant;
	tun = tunnel;

	// get content
	content = window.getContentPane();

	// main windows setup
	window.setTitle("Orbit Controller");
	window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	window.addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(WindowEvent w) {
		// disconnect
		si.getServerConnection().logout();
		try {
		    tun.Disconnect();
		} catch (Exception ex) {
		}

		if (new Boolean(config.getProperty("interface.close", "true"))) {
		    System.exit(0);
		} else {
		    // create login window
		    JFrame loginWindow = new loginFrame();
		    loginWindow.setVisible(true);

		    // dispose this window
		    window.dispose();
		}
	    }

	    @Override
	    public void windowDeiconified(WindowEvent w) {
		if (vut != null) { // update and restart timer
		    new ShowVM(false).start();
		    vut.getTimer().start();
		}
	    }

	    @Override
	    public void windowIconified(WindowEvent w) {
		if (vut != null) { // stop timer
		    vut.getTimer().stop();
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
	Arrays.sort(virtualMachines, new VMComparator());

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
     * Compare VM's to sort them A-Z
     */
    class VMComparator implements Comparator<OrbitVirtualMachine> {

	// Comparator interface requires defining compare method.
	public int compare(OrbitVirtualMachine vm1, OrbitVirtualMachine vm2) {
	    return vm1.getName().compareToIgnoreCase(vm2.getName());
	}
    }

    /**
     * Create GUI
     */
    public void createGUI() {
	// locals
	JImagePanel machinePanel;
	JPanel vmInfoPanel, vmGeneralPanel, vmResourcePanel;

	JPanel formPanels[], infoPanels[], formResourcePanels[], infoResourcePanels[];

	//TODO: resource tab (with advance labels??)

	// layout
	content.setLayout(new BorderLayout(0, 0));

	//labels
	generalInfoLabels = new JLabel[10][2];
	resourceInfoLabels = new JLabel[7][2];

	for (int i = 0; i < generalInfoLabels.length; i++) {
	    for (int j = 0; j < generalInfoLabels[i].length; j++) {
		generalInfoLabels[i][j] = new JLabel();
	    }
	}
	for (int i = 0; i < resourceInfoLabels.length; i++) {
	    for (int j = 0; j < resourceInfoLabels[i].length; j++) {
		resourceInfoLabels[i][j] = new JLabel();
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

	resourceInfoLabels[0][0].setText("Consumed Host CPU:");
	resourceInfoLabels[1][0].setText("Consumed Host Memory:");
	resourceInfoLabels[2][0].setText("Active Guest Memory:");
	resourceInfoLabels[3][0].setText("Provisioned Storage:");
	resourceInfoLabels[4][0].setText("Non-shared Storage:");
	resourceInfoLabels[5][0].setText("Used Storage:");

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

	vmResourcePanel = new JPanel();
	vmResourcePanel.setLayout(new GridLayout(1, 1));
	vmResourcePanel.setBorder(BorderFactory.createTitledBorder(
		BorderFactory.createEtchedBorder(), " Resources "));
	vmInfoPanel.add(vmResourcePanel);

	formResourcePanels = new JPanel[resourceInfoLabels.length];
	infoResourcePanels = new JPanel[(resourceInfoLabels.length - 1)];
	for (int i = 0; i < formResourcePanels.length; i++) {
	    formResourcePanels[i] = new JPanel();
	    formResourcePanels[i].setLayout(new BorderLayout());

	    if (i > 0) {
		formResourcePanels[(i - 1)].add(formResourcePanels[i], BorderLayout.CENTER);
	    }

	    if (i < (resourceInfoLabels.length - 1)) {
		infoResourcePanels[i] = new JPanel();
		infoResourcePanels[i].setLayout(new BorderLayout());
		//infoPanels[i].setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		resourceInfoLabels[i][0].setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		resourceInfoLabels[i][1].setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		resourceInfoLabels[i][0].setPreferredSize(new Dimension(180, 20));
		infoResourcePanels[i].add(resourceInfoLabels[i][0], BorderLayout.CENTER);
		infoResourcePanels[i].add(resourceInfoLabels[i][1], BorderLayout.EAST);
		formResourcePanels[i].add(infoResourcePanels[i], BorderLayout.NORTH);
	    } else {
	    }

	}

	// spacing
	infoResourcePanels[2].setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

	vmResourcePanel.add(formResourcePanels[0], BorderLayout.CENTER);

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
		    generalInfoLabels[i][1].setToolTipText("");
		}

		for (int i = 0; i < resourceInfoLabels.length; i++) {
		    resourceInfoLabels[i][1].setText("");
		    resourceInfoLabels[i][1].setToolTipText("");
		}
	    }

	    if (clearLabels) {
		virtualMachineCombo.setEnabled(false);
	    }
	}

	@Override
	public void run() {
	    // locals
	    VirtualHardware vh;
	    VirtualMachinePowerState vp;
	    String[] stringSize = {"KB", "MB", "GB", "TB"};
	    long div = 0;

	    // stop timer
	    vut.getTimer().stop();

	    // get vm
	    OrbitVirtualMachine vm = virtualMachines[virtualMachineCombo.getSelectedIndex()];

	    // toolbar
	    vp = vm.getPowerState();
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
		    stopButton.setEnabled(true);
		    resetButton.setEnabled(true);
		} else {
		    stopButton.setEnabled(false);
		    resetButton.setEnabled(false);
		}
	    }

	    // powerstate
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

	    // guest os
	    generalInfoLabels[1][1].setText(vm.getGuestOSName());
	    generalInfoLabels[1][1].setToolTipText(vm.getGuestOSName());

	    // hardware
	    vh = vm.getHardware();
	    generalInfoLabels[2][1].setText(vh.getNumCPU() + " vCPU");
	    generalInfoLabels[3][1].setText(vh.getMemoryMB() + " MB");

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
	    generalInfoLabels[6][1].setToolTipText(vm.getGuestHostName());

	    // host
	    generalInfoLabels[8][1].setText(vm.getHost().getName());
	    generalInfoLabels[8][1].setToolTipText(vm.getHost().getName());

	    // notes
	    try {
		notesArea.setText(vm.getVirtualMachine().getSummary().getConfig().getAnnotation());
	    } catch (Exception e) {
		notesArea.setText("");
	    }

	    // cpu usage
	    resourceInfoLabels[0][1].setText(vm.getVirtualMachine().getSummary().getQuickStats().overallCpuUsage + " MHz");

	    // memory overhead
	    resourceInfoLabels[1][1].setText(vm.getVirtualMachine().getSummary().getQuickStats().hostMemoryUsage + " MB");

	    // memory active
	    resourceInfoLabels[2][1].setText(vm.getVirtualMachine().getSummary().getQuickStats().guestMemoryUsage + " MB");

	    // storage
	    double diskNonShared = 0, diskUsed = 0, diskProv = 0;
	    for (VirtualMachineUsageOnDatastore vmuod : vm.getVirtualMachine().getStorage().getPerDatastoreUsage()) {
		diskNonShared += vmuod.getUnshared();
		diskUsed += vmuod.getCommitted();
		diskProv += (vmuod.getUncommitted() + vmuod.getCommitted());
	    }

	    // diskprov
	    div = 1024L;
	    for (int i = 0; i < 3; i++) {
		if ((double) (diskProv / div) <= 1024d) {
		    resourceInfoLabels[3][1].setText(String.format("%.2f %s", (double) (diskProv / div), stringSize[i]));
		    break;
		}
		div *= 1024L;
	    }

	    // disk non share
	    div = 1024L;
	    for (int i = 0; i < 3; i++) {
		if ((double) (diskNonShared / div) <= 1024d) {
		    resourceInfoLabels[4][1].setText(String.format("%.2f %s", (double) (diskNonShared / div), stringSize[i]));
		    break;
		}
		div *= 1024L;
	    }

	    // disk used
	    div = 1024L;
	    for (int i = 0; i < 3; i++) {
		if ((double) (diskUsed / div) <= 1024d) {
		    resourceInfoLabels[5][1].setText(String.format("%.2f %s", (double) (diskUsed / div), stringSize[i]));
		    break;
		}
		div *= 1024L;
	    }

	    // run timer
	    virtualMachineCombo.setEnabled(true);
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
			    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to suspend this vm?") == JOptionPane.YES_OPTION) {
				vm.suspend();
			    }
			} else {
			    vm.powerOn();
			}
		    } else if (action.equalsIgnoreCase("stop")) {
			if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
			    String actionString = "";
			    if (vm.isToolsRunning()) {
				actionString = "shutdown";
			    } else {
				actionString = "power off";
			    }

			    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to " + actionString + " this vm?") == JOptionPane.YES_OPTION) {
				vm.powerOff(true);
			    }
			}
		    } else if (action.equalsIgnoreCase("reset")) {
			if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
			    String actionString = "";
			    if (vm.isToolsRunning()) {
				actionString = "restart";
			    } else {
				actionString = "reset";
			    }

			    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to " + actionString + " this vm?") == JOptionPane.YES_OPTION) {
				vm.reset(true);
			    }
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
