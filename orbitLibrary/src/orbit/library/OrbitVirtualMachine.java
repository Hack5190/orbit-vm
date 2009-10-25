package orbit.library;

/**
 * Orbit Virtual Machine Wrapper
 * @author sjorge
 */
/**
 * Imports
 */
import java.util.*;

// vijava
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class OrbitVirtualMachine {

    //variables
    VirtualMachine vm;

    /**
     * Create OrbitVirtualMachine from a VirtualMachine
     * @param vm Virtual Machine that you want to wrap
     */
    public OrbitVirtualMachine(VirtualMachine vm) {
        this.vm = vm;
    }

    /**
     * Get the internal VirtualMachine
     * @return the virtual machine
     */
    public VirtualMachine getVirtualMachine() {
        return vm;
    }

    /**
     * Get the name of the virtual machine
     * @return name of virtual machine
     */
    public String getName() {
        return vm.getName();
    }

    /**
     * Get the VirtualMachinePowerState from the VirtualMachine
     * @return VitualMachinePowerState
     */
    public VirtualMachinePowerState getPowerState() {
        return vm.getRuntime().getPowerState();
    }

    /**
     * Get the guest state
     * @return guest state
     */
    public String getGuestState() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getGuestState();
    }

    /**
     * Get the GuestInfo form the VirtualMachine
     * @return Guest Info
     */
    public GuestInfo getGuestInfo() {
        return vm.getGuest();
    }

    /**
     * Check if tools are installed
     * @return true if installed
     */
    public boolean isToolsInstalled() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsNotInstalled);
    }

    /**
     * 
     * Check if tools are running
     * @return true if running
     */
    public boolean isToolsRunning() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsOk);
    }

    /**
     * Check if tools are up to date
     * @return true if up to date
     */
    public boolean isToolsUpgradable() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return (gi.getToolsStatus() == VirtualMachineToolsStatus.toolsOld);
    }

    /**
     * Check if tools are unmanaged
     * @return true if unmanaged
     */
    public boolean isToolsUnmanaged() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getToolsVersionStatus().equals("guestToolsUnmanaged");
    }

    /**
     * Get the most complete OS name
     * @return os name
     */
    public String getGuestOSName() {
        // locals
        String os = "unknown";
        GuestInfo gi = this.getGuestInfo();

        os = gi.getGuestFullName();
        if (os == null || os.isEmpty()) {
            os = gi.getGuestFamily();
        }
        if (os == null | os.isEmpty()) {
            os = gi.getGuestId();
        }

        return os;
    }

    /**
     * Get hostname of guest if available
     * @return hostname
     */
    public String getGuestHostName() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getHostName();
    }

    /**
     * Get primary ip of guest if available
     * @return primary ip
     */
    public String getGuestPrimaryIP() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getIpAddress();
    }

    /**
     * Get all ip's of guest if available
     * @return ip's of guest
     */
    public String[] getGuestIPs() {
        // local
        ArrayList<String> result = new ArrayList<String>();
        GuestInfo gi = this.getGuestInfo();
        GuestNicInfo gni[] = gi.getNet();

        // loop ip's
        for (GuestNicInfo nic : gni) {
            for (String ip : nic.getIpAddress()) {
                result.add(ip);
            }
        }

        return (String[]) result.toArray();
    }

    /**
     * Get the Host on which the VirtualMachien runs
     * @return host
     */
    public HostSystem getHost() {
        // locals
        HostSystem host;
        Folder rootFolder;

        try {
            // locals
            ManagedObjectReference hostMOR;

            rootFolder = vm.getServerConnection().getServiceInstance().getRootFolder();
            ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
            if (mes == null || mes.length == 0) {
                throw new NullPointerException();
            }

            host = null;
            hostMOR = vm.getRuntime().getHost();
            for (int i = 0; i < mes.length; i++) {
                if (mes[i].getMOR().equals(hostMOR)) {
                    host = (HostSystem) mes[i];
                }
            }
        } catch (Exception e) {
            return null;
        }

        return host;
    }

    /**
     * Power On the VirtualMachine
     * @return true if powered on
     */
    public boolean powerOn() {
        // locals
        com.vmware.vim25.mo.Task t;

        // start vm
        try {

            if (this.getPowerState() == VirtualMachinePowerState.poweredOn) {
                return true;
            } else {
                t = this.getVirtualMachine().powerOnVM_Task(null);
                if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Power Off the VirtualMachine
     * @return true if powered off
     */
    public boolean powerOff() {
        return this.powerOff(false);
    }

    /**
     * Power Off the VirtualMachine
     * @param useTools try to do shutdown if tools are running
     * @return true if powered off
     */
    public boolean powerOff(boolean useTools) {
        // locals
        com.vmware.vim25.mo.Task t;

        // stop vm
        try {
            if (this.getPowerState() == VirtualMachinePowerState.poweredOff) {
                return true;
            } else if (useTools && this.isToolsRunning()) {
                this.getVirtualMachine().shutdownGuest();
                return true;
            } else {
                t = this.getVirtualMachine().powerOffVM_Task();
                if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
                    return true;
                }

            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Reset the VirtualMachine
     * @return true on success
     */
    public boolean reset() {
        return this.reset(false);
    }

    /**
     * Reset the VirtualMachine
     * @param useTools try to do a restart if tools are running
     * @return true on success
     */
    public boolean reset(boolean useTools) {
        // locals
        com.vmware.vim25.mo.Task t;

        // stop vm
        try {
            if (useTools && this.isToolsRunning()) {
                this.getVirtualMachine().rebootGuest();
                return true;
            } else {
                t = this.getVirtualMachine().resetVM_Task();
                if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
                    return true;
                }

            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    /**
     * Suspend the VirtualMachine
     * @return true on success
     */
    public boolean suspend() {
        return this.suspend(false);
    }

    /**
     * Suspend the VirtualMachine
     * @param useTools try to do a standby if tools are running
     * @return true if suspended
     */
    public boolean suspend(boolean useTools) {
        // locals
        com.vmware.vim25.mo.Task t;

        // start vm
        try {
            if (this.getPowerState() == VirtualMachinePowerState.suspended) {
                return true;
            } else if (this.getPowerState() == VirtualMachinePowerState.poweredOn) {
                if (useTools && this.isToolsRunning()) {
                    this.getVirtualMachine().standbyGuest();
                    return true;
                } else {
                    t = this.getVirtualMachine().suspendVM_Task();
                    if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }
}
