package nl.tudelft.sem.template.hoa.exception;

import nl.tudelft.sem.template.hoa.annotations.Generated;

@Generated
/**
 * Exception to indicate that an activity does not exist.
 */
public class ActivityDoesntExistException extends Exception {

    static final long serialVersionUID = -3375169931954229948L;

    /**
     * Constructor for the activity does not exist exception.
     *
     * @param message the error message
     */
    public ActivityDoesntExistException(String message) {
        super(message);
    }
}
