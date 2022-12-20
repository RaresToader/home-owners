package nl.tudelft.sem.template.authmember.domain.exceptions;

/**
 * Exception to be thrown where a join hoa model is not suitable.
 */
public class BadJoinHoaModelException extends Exception {

    static final long serialVersionUID = -537232159562223148L;

    /**
     * Constructor for the bad join hoa model exception.
     *
     * @param message the message
     */
    public BadJoinHoaModelException(String message) {
        super(message);
    }
}
