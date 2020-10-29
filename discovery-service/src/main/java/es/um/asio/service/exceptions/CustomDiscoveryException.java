package es.um.asio.service.exceptions;

public class CustomDiscoveryException extends RuntimeException {

    private static final long serialVersionUID = 7718828512143293558L;

    public CustomDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomDiscoveryException(String message) {
        super(message);
    }

    public CustomDiscoveryException() {
    }
}
