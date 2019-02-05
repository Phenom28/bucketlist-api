package co.getdev.bucketlists.security.exception;

/**
 * Thrown if an authentication token is invalid.
 *
 * @author Ogundipe Segun David
 */
public class InvalidAuthenticationTokenException extends RuntimeException {
    
    private static final long serialVersionUID = -8988884641365537599L;
    
    public InvalidAuthenticationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
