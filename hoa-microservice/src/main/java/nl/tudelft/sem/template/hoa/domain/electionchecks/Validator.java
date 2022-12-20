package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;

import java.util.List;

public interface Validator {

    void setNext(Validator handler);

    boolean handle(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException;
}