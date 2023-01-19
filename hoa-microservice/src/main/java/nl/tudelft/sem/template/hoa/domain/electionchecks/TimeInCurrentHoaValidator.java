package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.TimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TimeInCurrentHoaValidator extends BaseValidator {

    transient long yearsRequiredInHoa = TimeUtils.yearsToSeconds(3);

    @Override
    public boolean handle(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException {
        Duration curHoaTime = memberships.stream()
                .filter(m -> m.getHoaId() == hoaID)
                .map(m -> m.getDuration() == null
                        ? TimeUtils.absoluteDifference(LocalDateTime.now(), m.getStartTime())
                        : m.getDuration())
                .reduce(Duration.ZERO, TimeUtils::sum);

        if (curHoaTime.getSeconds() >= yearsRequiredInHoa) {
            return super.checkNext(memberships, hoaID);
        }
        throw new InvalidParticipantException("Participant hasn't been in the HOA long enough");
    }
}
