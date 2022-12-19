package nl.tudelft.sem.template.authmember.domain.exceptions;

/**
 * Exception to be thrown when a bad registration model is given.
 */
public class BadRegistrationModelException extends Exception {
    static final long serialVersionUID = -2375142516954229148L;

    /**
     * Constructor for the bad registration model exception.
     *
     * @param message the message
     */
    public BadRegistrationModelException(String message) {
        super(message);
    }
}
