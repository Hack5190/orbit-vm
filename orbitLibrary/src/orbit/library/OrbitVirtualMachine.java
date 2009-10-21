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

    public OrbitVirtualMachine(VirtualMachine vm) {
        this.vm = vm;
    }

    public VirtualMachine getVirtualMachine() {
        return vm;
    }

    public String getName() {
        return vm.getName();
    }

    public VirtualMachinePowerState getPowerState() {
        return vm.getRuntime().getPowerState();
    }

    public String getGuestState() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getGuestState();
    }

    public GuestInfo getGuestInfo() {
        return vm.getGuest();
    }

    public boolean isToolsInstalled() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsNotInstalled);
    }

    public boolean isToolsRunning() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsOk);
    }

    public boolean isToolsUpgradable() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return (gi.getToolsStatus() == VirtualMachineToolsStatus.toolsOld);
    }

    public boolean isToolsUnmanaged() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getToolsVersionStatus().equals("guestToolsUnmanaged");
    }

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

    public String getGuestHostName() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getHostName();
    }

    public String getGuestPrimaryIP() {
        // local
        GuestInfo gi = this.getGuestInfo();

        return gi.getIpAddress();
    }

    public String[] getGuestIPs() {
        // local
        ArrayList result = new ArrayList();
        GuestInfo gi = this.getGuestInfo();
        GuestNicInfo gni[] = gi.getNet();

        // loops ip's
        for (GuestNicInfo nic : gni) {
            for (String ip : nic.getIpAddress()) {
                result.add(ip);
            }
        }

        return (String[]) result.toArray();
    }

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

    public boolean powerOff() {
        return this.powerOff(false);
    }

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

    public boolean reset() {
        return this.reset(false);
    }

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

    public boolean suspend() {
        // locals
        com.vmware.vim25.mo.Task t;

        // start vm
        try {
            if (this.getPowerState() == VirtualMachinePowerState.suspended) {
                return true;
            } else if (this.getPowerState() == VirtualMachinePowerState.poweredOn) {
                t = this.getVirtualMachine().suspendVM_Task();
                if (t.waitForMe().equalsIgnoreCase(Task.SUCCESS)) {
                    return true;
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
