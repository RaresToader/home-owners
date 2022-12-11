package voting.domain.exceptions;

/**
 * Exception to indicate that a name already exists.
 */
public class ElectionCannotBeCreated extends Exception {
    static final long serialVersionUID = -3521151297132725918L;

    /**
     * Constructor for the exception.
     *
     * @param message the error message
     */
    public ElectionCannotBeCreated(String message) {
        super(message);
    }
}
