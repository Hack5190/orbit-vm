package orbit.library;

/**
 * Orbit SSH Tunnel
 * @author sjorge
 */
/**
 * Imports
 */
import com.jcraft.jsch.*;

public class OrbitSSHTunnel {

    private JSch jsch;
    private Session session;
    private String remotehost;

    class localUserInfo implements UserInfo {

	String passwd;

	public localUserInfo(String passwd) {
	    this.passwd = passwd;
	}

	public String getPassword() {
	    return passwd;
	}

	public boolean promptYesNo(String str) {
	    return true;
	}

	public String getPassphrase() {
	    return null;
	}

	public boolean promptPassphrase(String message) {
	    return true;
	}

	public boolean promptPassword(String message) {
	    return true;
	}

	public void showMessage(String message) {
	}
    }

    public OrbitSSHTunnel(String host, String username, String password, String remotehost) throws Exception {
	// locals
	String hostname;
	int port = 22;


	// parse host
	if (host.indexOf(':') > -1) {
	    hostname = host.substring(0, host.indexOf(':'));
	    port = Integer.parseInt((host.substring((host.indexOf(':') + 1), host.length())));
	} else {
	    hostname = host;
	}

	// create ssh connetion
	jsch = new JSch();
	session = jsch.getSession(username, hostname,  port);
	session.setUserInfo(new localUserInfo(password));
	this.remotehost = remotehost;
	
    }

    public void Disconnect() throws Exception {
	session.delPortForwardingL(9123);
	session.disconnect();
    }

    public void Conenct() throws Exception {
	session.setPortForwardingL(9123, remotehost, 443);
	session.connect();
    }
}
