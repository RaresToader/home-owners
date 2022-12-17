package nl.tudelft.sem.template.hoa.exception;

/**
 * Exception that indicates that the Hoa to be created does not comply to the format.
 */
public class BadFormatHoaException extends Exception {
    static final long serialVersionUID = -3332131699195239948L;

    /**
     * Constructor for the BadFormatHoaException.
     * @param message the message
     */
    public BadFormatHoaException(String message) {
        super(message);
    }
}
