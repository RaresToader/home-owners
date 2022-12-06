package nl.tudelft.sem.template.hoa.exception;

/**
 * Exception to indicate that a name already exists.
 */
public class HoaNameAlreadyTakenException extends Exception {
    static final long serialVersionUID = -3378516993122429948L;

    /**
     * Constructor for the exception.
     *
     * @param message the error message
     */
    public HoaNameAlreadyTakenException(String message) {
        super(message);
    }
}
