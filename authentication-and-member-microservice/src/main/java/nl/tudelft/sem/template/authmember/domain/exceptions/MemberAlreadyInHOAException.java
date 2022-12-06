package nl.tudelft.sem.template.authmember.domain.exceptions;

import nl.tudelft.sem.template.authmember.models.JoinHOAModel;

public class MemberAlreadyInHOAException extends Exception{

    public MemberAlreadyInHOAException(JoinHOAModel model) {
        super(model.getMemberID() + " " + model.getHoaID());
    }

}
