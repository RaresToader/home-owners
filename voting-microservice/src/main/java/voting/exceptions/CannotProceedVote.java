package voting.exceptions;

import voting.annotations.Generated;

/**
 * Exception to indicate that a name already exists.
 */
@Generated // Exceptions may not be tested due to triviality
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
