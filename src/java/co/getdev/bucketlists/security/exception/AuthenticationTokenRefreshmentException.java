package co.getdev.bucketlists.security.exception;

/**
 * Thrown if an authentication token cannot be refreshed.
 *
 * @author Ogundipe Segun David
 */
public class AuthenticationTokenRefreshmentException extends RuntimeException {

    private static final long serialVersionUID = -8440826752106032033L;
    
    public AuthenticationTokenRefreshmentException(String message) {
        super(message);
    }

    public AuthenticationTokenRefreshmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
