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

public class controllerFrame extends JFrame {

    // variables
    private ServiceInstance si;
    private controllerFrame window;
    private Container content;
    private Properties config;
    private VirtualMachine[] virtualMachines;
    private JComboBox virtualMachineCombo;
    private JLabel[] vmInfoLabels;
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

        // collect virtual machines
        virtualMachines = window.getVirtualMachines();

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
        JPanel controlPanel, formPanels[], infoPanels[];
        String vmSearchButtonsText[] = {"Start", "Stop", "Reset"}; //TODO: remove this

        //TODO: disable on no vm's
        //TODO: infoPanel (maybe move current header into info and add header image?)

        // layout
        content.setLayout(new BorderLayout(0, 0));

        //labels
        formLabels = new JLabel[6][2];
        for (int i = 0; i < formLabels.length; i++) {
            for (int j = 0; j < formLabels[i].length; j++) {
                formLabels[i][j] = new JLabel();
            }
        }
        formLabels[0][0].setText("Virtual Machine:");
        formLabels[1][0].setText("Status:");
        formLabels[2][0].setText("CPU:");
        formLabels[3][0].setText("Mem:");
        formLabels[4][0].setText("Network:");
        formLabels[5][0].setText("Tools:");

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
        for (VirtualMachine vm : virtualMachines) {
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
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(450, 30));
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        content.add(controlPanel, BorderLayout.SOUTH);

        /*
        vmControlButtons = new JButton[3];
        for (int i = 0; i < vmControlButtons.length; i++) {
            vmControlButtons[i] = new JButton();
            //TODO: replace with icons and remove temp text variable
            vmControlButtons[i].setText(vmSearchButtonsText[i]);
            controlPanel.add(vmControlButtons[i]);
        }
        new poweronButtonClick(vmControlButtons[0]);
        */

        showVM(virtualMachines[virtualMachineCombo.getSelectedIndex()]);
    }

    /**
     * Get all virtual machine and update combo
     */
    public VirtualMachine[] getVirtualMachines() {
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
            return null;
        }

        return vms;
    }

    /**
     * Show VirtualMachine information
     * @param vm VirtualMachine
     */
    public void showVM(VirtualMachine vm) {
        // local
        VirtualMachineSummary summary;
        
        // get data

        formLabels[1][1].setText("status - " + vm.getName());
        formLabels[2][1].setText("cpu");
        formLabels[3][1].setText("mem");
        formLabels[4][1].setText("network");
        formLabels[5][1].setText("tools");
    }

    /**
     * close button click event
     * @author sjorge
     */
    class poweronButtonClick implements ActionListener {

        public poweronButtonClick(JButton button) {
            button.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            // locals
            VirtualMachine vm;
            boolean successBool = false;

            if (virtualMachineCombo.getSelectedIndex() > -1) {
                vm = virtualMachines[virtualMachineCombo.getSelectedIndex()];


                if (vm != null) {
                    try {
                        Task task = vm.powerOnVM_Task(null);
                        if (task.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
                            successBool = true;
                        }
                    } catch (Exception ex) {
                    }

                    if (successBool) {
                        //TODO: show visually
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
