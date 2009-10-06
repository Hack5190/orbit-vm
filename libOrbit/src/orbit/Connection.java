package orbit;

/**
 * Imports
 */
import java.net.URL;
import orbit.exceptions.*;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

/**
 *
 * @author sjorge
 */
public class Connection {
    /** configuration */
    private String ServiceURL;
    private String Username = "root";
    private String Password = "";

    /** internal */
    ServiceInstance si = null;

    /**
     * Create Connection
     * @param ServiceURL url for the VMWare ESXi/vSphere Server
     * @param Username username
     * @param Password password
     * @throws Exception
     */
    public Connection(String ServiceURL, String Username, String Password) throws Exception  {
        // validate
        if (Username == null || Username.isEmpty()) {
            throw new ConnectionException("Username cannot be empty!");
        }

        if (Password == null) {
            throw new ConnectionException("Password cannot be null.");
        }

        if (ServiceURL == null ||  ServiceURL.isEmpty()) {
            throw new ConnectionException("ServiceURL cannot be empty.");
        }

        // store variables
        this.ServiceURL = ServiceURL;
        this.Username = Username;
        this.Password = Password;
        
    }

    /**
     * Create connection and return ServiceInstance
     * @return ServiceInstance for this connection
     */
    public ServiceInstance connect() throws Exception {
        if (si == null) {
            // validate serviceurl
            if (!ServiceURL.endsWith("/sdk")) {
                ServiceURL += "/sdk";
            }

            // connect
            si = new ServiceInstance(new URL(ServiceURL), Username, Password, true);
        }
        
        return si;
    }


    
}
