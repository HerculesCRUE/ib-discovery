package es.um.asio.audit.abstractions.exception;

/**
 * Exception thrown when an specific entity is not found.
 */
public class NoSuchEntityException extends Exception {

    /**
     * Version ID.
     */
    private static final long serialVersionUID = -7224656298167128280L;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public NoSuchEntityException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause. <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message
     *            the detail message (which is saved for later retrieval
     *            by the {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A {@code null} val is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public NoSuchEntityException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message
     *            the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public NoSuchEntityException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A {@code null} val is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public NoSuchEntityException(final Throwable cause) {
        super(cause);
    }

}
