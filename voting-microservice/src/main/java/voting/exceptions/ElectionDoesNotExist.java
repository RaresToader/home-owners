package voting.exceptions;

import voting.annotations.Generated;

/**
 * Exception to indicate that a name already exists.
 */
@Generated
public class ElectionDoesNotExist extends Exception {
    static final long serialVersionUID = -3371151293132425948L;

    /**
     * Constructor for the exception.
     *
     * @param message the error message
     */
    public ElectionDoesNotExist(String message) {
        super(message);
    }
}
