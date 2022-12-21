package voting.exceptions;

import voting.annotations.Generated;

/**
 * Exception to indicate that a name already exists.
 */
@Generated
public class ProposalAlreadyCreated extends Exception {
    static final long serialVersionUID = -3331451293132725248L;

    /**
     * Constructor for the exception.
     *
     * @param message the error message
     */
    public ProposalAlreadyCreated(String message) {
        super(message);
    }
}
