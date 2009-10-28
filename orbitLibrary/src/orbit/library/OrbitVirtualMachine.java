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
     * Get the guest VirtualHardware
     * @return guest VirtualHardware
     */
    public VirtualHardware getHardware() {
        // local
        VirtualHardware vh;
        try {
            vh = this.vm.getConfig().getHardware();
        } catch (Exception e) {
            vh = null;
        }

        return vh;
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

        return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsNotRunning);
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
        String os;
        GuestInfo gi = this.getGuestInfo();

        os = gi.getGuestFullName();
        if (os == null || os.isEmpty()) {
            os = gi.getGuestFamily();
        }
        if (os == null || os.isEmpty()) {
            os = gi.getGuestId();
        }
        if (os == null || os.isEmpty()) {
            os = "Unknown";
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

        return (gi.getIpAddress() == null) ? "" : gi.getIpAddress();
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

        if (gni == null) {
            return null;
        }

        // loop ip's
        for (GuestNicInfo nic : gni) {
            for (String ip : nic.getIpAddress()) {
                result.add(ip);
            }
        }

        // result
        String[] resultArray = new String[1];
        result.toArray(resultArray);
        return resultArray;

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
    public Task powerOn() {
        // locals
        com.vmware.vim25.mo.Task t;

        // start vm
        try {

            if (this.getPowerState() == VirtualMachinePowerState.poweredOn) {
                return null;
            } else {
                t = this.getVirtualMachine().powerOnVM_Task(null);

                return t;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Power Off the VirtualMachine
     * @return true if powered off
     */
    public Task powerOff() {
        return this.powerOff(false);
    }

    /**
     * Power Off the VirtualMachine
     * @param useTools try to do shutdown if tools are running
     * @return true if powered off
     */
    public Task powerOff(boolean useTools) {
        // locals
        com.vmware.vim25.mo.Task t;

        // stop vm
        try {
            if (this.getPowerState() == VirtualMachinePowerState.poweredOff) {
                return null;
            } else if (useTools && this.isToolsRunning()) {
                this.getVirtualMachine().shutdownGuest();
                return null;
            } else {
                t = this.getVirtualMachine().powerOffVM_Task();
                return t;

            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Reset the VirtualMachine
     * @return true on success
     */
    public Task reset() {
        return this.reset(false);
    }

    /**
     * Reset the VirtualMachine
     * @param useTools try to do a restart if tools are running
     * @return true on success
     */
    public Task reset(boolean useTools) {
        // locals
        com.vmware.vim25.mo.Task t;

        // stop vm
        try {
            if (useTools && this.isToolsRunning()) {
                this.getVirtualMachine().rebootGuest();
                return null;
            } else {
                t = this.getVirtualMachine().resetVM_Task();
                return t;

            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Suspend the VirtualMachine
     * @return true on success
     */
    public Task suspend() {
        return this.suspend(false);
    }

    /**
     * Suspend the VirtualMachine
     * @param useTools try to do a standby if tools are running
     * @return true if suspended
     */
    public Task suspend(boolean useTools) {
        // locals
        com.vmware.vim25.mo.Task t;

        // start vm
        try {
            if (this.getPowerState() == VirtualMachinePowerState.suspended) {
                return null;
            } else if (this.getPowerState() == VirtualMachinePowerState.poweredOn) {
                if (useTools && this.isToolsRunning()) {
                    this.getVirtualMachine().standbyGuest();
                    return null;
                } else {
                    t = this.getVirtualMachine().suspendVM_Task();
                    return t;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
