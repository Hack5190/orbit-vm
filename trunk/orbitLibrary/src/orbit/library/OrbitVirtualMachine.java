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

    public boolean getToolsInstalled() {
	// local
	GuestInfo gi = this.getGuestInfo();

	return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsNotInstalled);
    }

    public boolean getToolsRunning() {
	// local
	GuestInfo gi = this.getGuestInfo();

	return !(gi.getToolsStatus() == VirtualMachineToolsStatus.toolsOk);
    }

    public boolean getToolsUpgradable() {
	// local
	GuestInfo gi = this.getGuestInfo();

	return (gi.getToolsStatus() == VirtualMachineToolsStatus.toolsOld);
    }

    public boolean getToolsUnmanaged() {
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
}
