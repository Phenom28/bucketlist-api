package co.getdev.bucketlists.security.exception;

/**
 * Thrown if errors occur during the authorization process.
 *
 * @author Ogundipe Segun David
 */
public class AccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = -6929186371971621273L;
    
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
