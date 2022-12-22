package nl.tudelft.sem.template.hoa.exception;

import nl.tudelft.sem.template.hoa.annotations.Generated;

@Generated
public class InvalidParticipantException extends Exception {

    static final long serialVersionUID = -3387576111124229948L;

    public InvalidParticipantException(String err) {
        super(err);
    }
}
