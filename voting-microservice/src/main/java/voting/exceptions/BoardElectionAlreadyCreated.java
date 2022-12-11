package voting.exceptions;

/**
 * Exception to indicate that a name already exists.
 */
public class BoardElectionAlreadyCreated extends Exception {
    static final long serialVersionUID = -3521454213672125248L;

    /**
     * Constructor for the exception.
     *
     * @param message the error message
     */
    public BoardElectionAlreadyCreated(String message) {
        super(message);
    }
}
