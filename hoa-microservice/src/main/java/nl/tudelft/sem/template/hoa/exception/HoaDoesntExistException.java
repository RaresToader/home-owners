package nl.tudelft.sem.template.hoa.exception;

import nl.tudelft.sem.template.hoa.annotations.Generated;

@Generated
/**
 * Exception to indicate that a Hoa does not exist.
 */
public class HoaDoesntExistException extends Exception {
    static final long serialVersionUID = -3387576193124229948L;

    /**
     * Constructor for the hoa does not exist exception.
     *
     * @param message the error message
     */
    public HoaDoesntExistException(String message) {
        super(message);
    }
}
