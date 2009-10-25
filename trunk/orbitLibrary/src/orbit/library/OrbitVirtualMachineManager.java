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

public class OrbitVirtualMachineManager {

    // variables
    private ServiceInstance si;

    /**
     * Create a Virtual Machine Manager
     * @param serviceInstant connection to get virtual machines from
     */
    public OrbitVirtualMachineManager(ServiceInstance serviceInstant) {
	// self reference
	this.si = serviceInstant;


    }

    /**
     * Get all virtual machines
     * @return virtual machines
     */
    public OrbitVirtualMachine[] getAllVirualMachines() {
	// locals
	Folder rootFolder;
	OrbitVirtualMachine[] vms;

	// get rootFolder and look for vm's
	try {
	    rootFolder = si.getRootFolder();
	    ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
	    if (mes == null || mes.length == 0) {
		throw new NullPointerException();
	    }
	    vms = new OrbitVirtualMachine[mes.length];
	    for (int i = 0; i < mes.length; i++) {
		String nameString = ((VirtualMachine) mes[i]).getName();

		vms[i] = new OrbitVirtualMachine((VirtualMachine) mes[i]);

	    }
	} catch (Exception e) {
	    return null;
	}

	return vms;
    }

    /**
     * Get all virtual machines matching name
     * @param name name of virtual machine, wildcards using %name, name% and %name%
     * @return virtual machines
     */
    public OrbitVirtualMachine[] getVirtualMachinesByName(String name) {
	// locals
	ArrayList<OrbitVirtualMachine> vms = new ArrayList<OrbitVirtualMachine>();

	for (OrbitVirtualMachine vm : this.getAllVirualMachines()) {
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

	return (OrbitVirtualMachine[]) vms.toArray();
    }

    /**
     * Get all virtual machines by host
     * @param host host
     * @return virtual machines
     */
    public OrbitVirtualMachine[] getVirtualMachinesByHost(HostSystem host) {
	// locals
	ArrayList<OrbitVirtualMachine> vms = new ArrayList<OrbitVirtualMachine>();

	for (OrbitVirtualMachine vm : this.getAllVirualMachines()) {
	    if (vm.getHost() == host) {
		vms.add(vm);
	    }
	}

	return (OrbitVirtualMachine[]) vms.toArray();
    }
}
