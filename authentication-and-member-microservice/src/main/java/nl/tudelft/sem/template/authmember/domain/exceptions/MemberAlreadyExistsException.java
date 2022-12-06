package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;

/**
 * Exception to indicate the memberID is already in use.
 */
public class MemberAlreadyExistsException extends Exception {
    public MemberAlreadyExistsException(RegistrationModel model) {
        super(model.getMemberID());
    }
}
