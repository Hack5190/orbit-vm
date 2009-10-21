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
	JPanel formPanels[], infoPanels[];

	//TODO: auto refresh every 3 sec
	//TODO: update info lables to match client
        /*
	Guest OS, CPU, Mem, Mem Overhead, VM Tools (no running, ok, unmaged),
	IP Adresses, DNS Name, State, Host
	 */
	//TODO: confirm for reset and halt
	//TODO: handle sucess/fail and don't hang
        //TODO: wrapper for VirtualMachine (VirtualMachineMananger (in si, out vm's with search) - VirtualMachine (wrapper for common used data)

	// layout
	content.setLayout(new BorderLayout(0, 0));

	//labels
	formLabels = new JLabel[7][2];
	for (int i = 0; i < formLabels.length; i++) {
	    for (int j = 0; j < formLabels[i].length; j++) {
		formLabels[i][j] = new JLabel();
	    }
	}
	formLabels[0][0].setText("Virtual Machine:");
	formLabels[1][0].setText("Host:");
	formLabels[2][0].setText("Status:");
	formLabels[3][0].setText("Guest OS:");
	formLabels[4][0].setText("Hostname:");
	formLabels[5][0].setText("IP Address:");
	formLabels[6][0].setText("Tools:");

	// header
	try {
	    machinePanel = new JImagePanel(ImageIO.read(window.getClass().getResource("/orbit/application/resources/header-controller.png")));
	} catch (IOException ioe) {
	    machinePanel = new JImagePanel();
	}
	machinePanel.setPreferredSize(new Dimension(450, 35));
	machinePanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 0));
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
		infoPanels[i].setPreferredSize(new Dimension(450, 20));
		infoPanels[i].setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		formLabels[(i + 1)][0].setPreferredSize(new Dimension(70, 20));
		infoPanels[i].add(formLabels[(i + 1)][0], BorderLayout.WEST);
		infoPanels[i].add(formLabels[(i + 1)][1], BorderLayout.CENTER);
		formPanels[i].add(infoPanels[i], BorderLayout.NORTH);
	    }

	}

	formPanels[0].setPreferredSize(new Dimension(450, 200));
	formPanels[0].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	content.add(formPanels[0], BorderLayout.CENTER);

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
	// host
	formLabels[1][1].setText(vm.getHost().getName());

	// powerState
	if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
	    formLabels[2][1].setText("powered on");
	    formLabels[2][1].setForeground(Color.blue);
	} else if (vm.getPowerState() == VirtualMachinePowerState.suspended) {
	    formLabels[2][1].setText("suspended");
	    formLabels[2][1].setForeground(Color.yellow);
	} else {
	    formLabels[2][1].setText("powered off");
	    formLabels[2][1].setForeground(Color.red);
	}


	formLabels[3][1].setText((vm.getGuestInfo().getGuestFullName() == null) ? "-" : vm.getGuestInfo().getGuestFullName());
	formLabels[4][1].setText((vm.getGuestInfo().getHostName() == null) ? "-" : vm.getGuestInfo().getHostName());
	formLabels[5][1].setText((vm.getGuestInfo().getIpAddress() == null) ? "-" : vm.getGuestInfo().getIpAddress());

	// tools information
	if (vm.getGuestInfo().getToolsStatus() == VirtualMachineToolsStatus.toolsNotInstalled) {
	    formLabels[6][1].setText("not installed");
	    formLabels[6][1].setForeground(Color.orange);
	} else if (vm.getGuestInfo().getToolsStatus() == VirtualMachineToolsStatus.toolsNotRunning) {
	    formLabels[6][1].setText("not running");
	    formLabels[6][1].setForeground(Color.red);
	} else if (vm.getGuestInfo().getToolsStatus() == VirtualMachineToolsStatus.toolsOld) {
	    formLabels[6][1].setText("running, needs upgrade");
	    formLabels[6][1].setForeground(Color.yellow);
	} else if (vm.getGuestInfo().getToolsStatus() == VirtualMachineToolsStatus.toolsOk) {
	    if (vm.getGuestInfo().getToolsVersionStatus().equals("guestToolsUnmanaged")) {
		formLabels[6][1].setText("running, unmanaged");
	    } else {
		formLabels[6][1].setText("running");
	    }
	    formLabels[6][1].setForeground(Color.blue);
	} else {
	    formLabels[6][1].setText("unknown");
	    formLabels[6][1].setForeground(null);
	}

	// update toolbar
	startButton.setVisible(true);
	stopButton.setVisible(true);
	resetButton.setVisible(true);
	try {
	    if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
		startButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-suspend.png")));
	    } else if (vm.getPowerState() == VirtualMachinePowerState.suspended) {
		startButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-poweron.png")));
	    } else {
		startButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-poweron.png")));
	    }
	    resetButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-reset.png")));
	    stopButton.setIcon(new ImageIcon(window.getClass().getResource("/orbit/application/resources/vmware/icons/vm-poweroff.png")));

	} catch (Exception e) {
	    if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
		startButton.setText("Suspend");
	    } else if (vm.getPowerState() == VirtualMachinePowerState.suspended) {
		startButton.setText("Power On");
	    } else {
		startButton.setText("Power On");
	    }
	    if (vm.getGuestInfo().getToolsStatus() == VirtualMachineToolsStatus.toolsOk) {
		resetButton.setText("Restart");
	    } else {
		resetButton.setText("Reset");
	    }
	    if (vm.getGuestInfo().getToolsStatus() == VirtualMachineToolsStatus.toolsOk) {
		resetButton.setText("Shutdown");
	    } else {
		resetButton.setText("Power Off");
	    }

	} finally {
	    if (vm.getPowerState() == VirtualMachinePowerState.poweredOn) {
	    } else if (vm.getPowerState() == VirtualMachinePowerState.suspended) {
		stopButton.setVisible(false);
	    } else {
		stopButton.setVisible(false);
		resetButton.setVisible(false);
	    }
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

	public boolean start(VirtualMachine vm) {
	    // locals
	    com.vmware.vim25.mo.Task t;
	    VirtualMachinePowerState powerState;

	    // get powerstate
	    powerState = vm.getRuntime().getPowerState();

	    // start vm
	    try {

		if (powerState == VirtualMachinePowerState.poweredOn) {
		    t = vm.suspendVM_Task();
		    if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
			return true;
		    }
		} else {
		    t = vm.powerOnVM_Task(null);
		    if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
			return true;
		    }
		}
	    } catch (Exception ex) {
		return false;
	    }
	    return false;
	}

	public boolean stop(VirtualMachine vm) {
	    // locals
	    com.vmware.vim25.mo.Task t;
	    GuestInfo guestInfo;

	    // guest info
	    guestInfo = vm.getGuest();

	    // stop vm
	    try {
		if (guestInfo.getToolsStatus() == VirtualMachineToolsStatus.toolsOk) {
		    vm.shutdownGuest();
		    return true;
		} else {
		    t = vm.powerOffVM_Task();
		    if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
			return true;
		    }

		}
	    } catch (Exception ex) {
		return false;
	    }
	    return false;
	}

	public boolean restart(VirtualMachine vm) {
	    // locals
	    com.vmware.vim25.mo.Task t;
	    GuestInfo guestInfo;

	    // guest info
	    guestInfo = vm.getGuest();

	    // stop vm
	    try {
		if (guestInfo.getToolsStatus() == VirtualMachineToolsStatus.toolsOk) {
		    vm.rebootGuest();
		    return true;
		} else {
		    t = vm.resetVM_Task();
		    if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
			return true;
		    }

		}
	    } catch (Exception ex) {
		return false;
	    }
	    return false;
	}

	public void actionPerformed(ActionEvent e) {
	    // locals
	    OrbitVirtualMachine vm;

	    if (virtualMachineCombo.getSelectedIndex() > -1) {
		vm = virtualMachines[virtualMachineCombo.getSelectedIndex()];

		if (vm != null) {
		    if (action.equalsIgnoreCase("start")) {
			//TODO: update for orbit wrapper
			//this.start(vm);
		    }
		    if (action.equalsIgnoreCase("stop")) {
			//TODO: update for orbit wrapper
			//this.stop(vm);
		    }
		    if (action.equalsIgnoreCase("reset")) {
			//TODO: update for orbit wrapper
			//this.restart(vm);
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
