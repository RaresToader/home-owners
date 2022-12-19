package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;

import java.util.List;

public abstract class BaseValidator implements Validator {
    private Validator next;

    public void setNext(Validator h) {
        this.next = h;
    }

    /**
     * Runs check on the next object in chain or ends traversing if we're in
     * last object in chain.
     */
    protected boolean checkNext(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException {
        if (next == null) {
            return true;
        }
        return next.handle(memberships, hoaID);
    }

}