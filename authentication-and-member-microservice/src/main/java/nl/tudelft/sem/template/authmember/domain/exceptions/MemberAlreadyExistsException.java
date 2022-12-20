package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.RegistrationModel;

/**
 * Exception to indicate the memberID is already in use.
 */
public class MemberAlreadyExistsException extends Exception {
    static final long serialVersionUID = -3375142516954229948L;

    public MemberAlreadyExistsException(RegistrationModel model) {
        super(model.getMemberId());
    }
}
