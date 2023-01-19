package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import java.util.List;

public class NotInAnyOtherBoardValidator extends BaseValidator {

    @Override
    public boolean handle(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException {
        boolean isInOtherBoard = memberships.stream()
            .anyMatch(m -> m.getHoaId() != hoaID && m.isBoardMember());

        if (!isInOtherBoard) {
            return super.checkNext(memberships, hoaID);
        }

        throw new InvalidParticipantException("Participant is already board of another HOA");
    }

}
