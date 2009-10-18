package orbit.library;

/**
 * Orbit Virtual Machine Manager
 * @author sjorge
 */
/**
 * Imports
 */
import java.util.*;

// vijava
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class VirtualMachineManager {

    // variables
    private ServiceInstance si;

    public VirtualMachineManager(ServiceInstance serviceInstant) {
	// self reference
	this.si = serviceInstant;


    }

    public VirtualMachineWrapper[] getAllVirualMachines() {
	// locals
	Folder rootFolder;
	VirtualMachineWrapper[] vms;

	// get rootFolder and look for vm's
	try {
	    rootFolder = si.getRootFolder();
	    ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
	    if (mes == null || mes.length == 0) {
		throw new NullPointerException();
	    }
	    vms = new VirtualMachineWrapper[mes.length];
	    for (int i = 0; i < mes.length; i++) {
		String nameString = ((VirtualMachine) mes[i]).getName();

		vms[i] = new VirtualMachineWrapper((VirtualMachine) mes[i]);

	    }
	} catch (Exception e) {
	    return null;
	}

	return vms;
    }

    public VirtualMachineWrapper[] getVirtualMachinesByName(String name) {
	// locals
	ArrayList vms = new ArrayList();

	for (VirtualMachineWrapper vm : this.getAllVirualMachines()) {
	    boolean match = false;

	    if (name.startsWith("%") && !name.endsWith("%")) {
		match = vm.getName().endsWith(name.substring(1));
	    } else if (name.endsWith("%") && !name.startsWith("%")) {
		match = vm.getName().startsWith(name.substring(0, -1));
	    } else if (name.startsWith("%") && name.endsWith("%")) {
		match = vm.getName().contains(name.substring(1, -1));
	    } else {
		match = vm.getName().equalsIgnoreCase(name);
	    }

	    if (match) {
		vms.add(vm);
	    }
	}

	return (VirtualMachineWrapper[]) vms.toArray();
    }

    public VirtualMachineWrapper[] getVirtualMachinesByHost(HostSystem host) {
	// locals
	ArrayList vms = new ArrayList();

	for (VirtualMachineWrapper vm : this.getAllVirualMachines()) {
	    if (vm.getHost() == host) {
		vms.add(vm);
	    }
	}

	return (VirtualMachineWrapper[]) vms.toArray();
    }
}
