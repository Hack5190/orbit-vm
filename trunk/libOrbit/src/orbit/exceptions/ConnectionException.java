/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orbit.exceptions;

/**
 *
 * @author sjorge
 */
public class ConnectionException extends Exception {
    String message;

    public ConnectionException() {
        super();
        message = "unknown";
    }

    public ConnectionException(String err) {
        super(err);
        message = err;
    }

    public String getError() {
        return message;
    }
}

