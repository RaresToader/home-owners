package voting.exceptions;

/**
 * Exception to indicate that a name already exists.
 */
public class CannotProceedVote extends Exception {
    static final long serialVersionUID = -3521454513612421248L;

    /**
     * Constructor for the exception.
     *
     * @param message the error message
     */
    public CannotProceedVote(String message) {
        super(message);
    }
}
