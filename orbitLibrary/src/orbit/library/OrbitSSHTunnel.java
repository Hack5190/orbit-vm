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
	jsch = new JSch();
	session = jsch.getSession(username, host.substring(0, host.indexOf(':')), Integer.getInteger(host.substring(host.indexOf(':'), host.length())));
	session.setUserInfo(new localUserInfo(password));
	session.setPortForwardingL(98123, remotehost, 443);
    }

    public void Disconnect() {
	session.disconnect();
    }

    public void Conenct() throws Exception {
	session.connect();
    }
}
