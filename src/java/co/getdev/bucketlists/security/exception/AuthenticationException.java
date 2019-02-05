package co.getdev.bucketlists.security.exception;

/**
 * Thrown if errors occur during the authentication process.
 *
 * @author Ogundipe Segun David
 */
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 3905113049964010047L;
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
