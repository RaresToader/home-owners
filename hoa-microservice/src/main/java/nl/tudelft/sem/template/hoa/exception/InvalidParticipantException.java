package nl.tudelft.sem.template.hoa.exception;

public class InvalidParticipantException extends Exception {

    static final long serialVersionUID = -3387576111124229948L;

    public InvalidParticipantException(String err) {
        super(err);
    }
}
