package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class NotBoardForTooLongValidator extends BaseValidator {
    long yearsMaxInBoard = TimeUtils.yearsToSeconds(10);

    @Override
    public boolean handle(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException {
        LocalDateTime curHoaTime = memberships.stream()
            .filter(m -> m.getHoaId() == hoaID && m.isBoard())
            .map(m -> Arrays.asList(m.getStartTime(), m.getDuration()))
            .map(l -> l.get(1) == null ? TimeUtils.absoluteDifference(l.get(0), LocalDateTime.now()) : l.get(1))
            .reduce(TimeUtils.getFirstEpochDate(), TimeUtils::sum);

        if (TimeUtils.seconds(curHoaTime) <= yearsMaxInBoard) {
            return super.checkNext(memberships, hoaID);
        }
        throw new InvalidParticipantException("Participant has been a board member for too long");
    }
}
