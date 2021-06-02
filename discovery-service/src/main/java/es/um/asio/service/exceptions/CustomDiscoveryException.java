package es.um.asio.service.exceptions;

/**
 * Exception CustomDiscoveryException.
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public class CustomDiscoveryException extends RuntimeException {

    private static final long serialVersionUID = 7718828512143293558L;

    /**
     * Constructor. Build with message and cause
     * @param message String. The message
     * @param cause Throwable
     */
    public CustomDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor. Build with message
     * @param message String. The message
     */
    public CustomDiscoveryException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public CustomDiscoveryException() {
    }
}
