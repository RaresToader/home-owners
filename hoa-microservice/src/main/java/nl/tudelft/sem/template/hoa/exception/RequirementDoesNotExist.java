package nl.tudelft.sem.template.hoa.exception;

/**
 * Exception to indicate that an activity does not exist.
 */
public class RequirementDoesNotExist extends Exception {

    static final long serialVersionUID = -3375119931954227848L;

    /**
     * Constructor for the activity does not exist exception.
     *
     * @param message the error message
     */
    public RequirementDoesNotExist(String message) {
        super(message);
    }
}
