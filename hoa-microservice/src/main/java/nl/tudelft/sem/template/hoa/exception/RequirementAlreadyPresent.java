package nl.tudelft.sem.template.hoa.exception;

/**
 * Exception to indicate that an activity does not exist.
 */
public class RequirementAlreadyPresent extends Exception {

    static final long serialVersionUID = -3375169931954227848L;

    /**
     * Constructor for the activity does not exist exception.
     *
     * @param message the error message
     */
    public RequirementAlreadyPresent(String message) {
        super(message);
    }
}
