package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.JoinHoaModel;

public class MemberAlreadyInHoaException extends Exception {
    static final long serialVersionUID = -3371231249954229948L;

    public MemberAlreadyInHoaException(JoinHoaModel model) {
        super(model.getMemberId() + " " + model.getHoaId());
    }

}
