package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.TimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class NotBoardForTooLongValidator extends BaseValidator {
    transient long yearsMaxInBoard = TimeUtils.yearsToSeconds(10);

    @Override
    public boolean handle(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException {
        Duration curHoaTime = memberships.stream()
            .filter(m -> m.getHoaId() == hoaID && m.isBoardMember())
            .map(m -> m.getDuration() == null
                    ? TimeUtils.absoluteDifference(LocalDateTime.now(), m.getStartTime())
                    : m.getDuration())
            .reduce(Duration.ZERO, TimeUtils::sum);

        if (curHoaTime.getSeconds() <= yearsMaxInBoard) {
            return super.checkNext(memberships, hoaID);
        }
        throw new InvalidParticipantException("Participant has been a board member for too long");
    }
}
