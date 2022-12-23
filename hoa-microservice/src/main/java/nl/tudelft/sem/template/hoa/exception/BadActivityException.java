package nl.tudelft.sem.template.hoa.exception;

import nl.tudelft.sem.template.hoa.annotations.Generated;


/**
 * Exception to be thrown when an activity does not satisfy the constraints.
 */
@Generated
public class BadActivityException extends Exception {

    static final long serialVersionUID = -3332324699195239948L;

    /**
     * The constructor for this exception.
     * @param message the message
     */
    public BadActivityException(String message) {
        super(message);
    }
}
