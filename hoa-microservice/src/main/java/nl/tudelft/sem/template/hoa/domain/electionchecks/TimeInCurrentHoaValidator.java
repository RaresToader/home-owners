package nl.tudelft.sem.template.hoa.domain.electionchecks;

import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TimeInCurrentHoaValidator extends BaseValidator {

    long yearsRequiredInHoa = TimeUtils.yearsToSeconds(3);

    @Override
    public boolean handle(List<MembershipResponseModel> memberships, long hoaID) throws InvalidParticipantException {
        LocalDateTime curHoaTime = memberships.stream()
            .filter(m -> m.getHoaId() == hoaID)
            .map(m -> Arrays.asList(m.getStartTime(), m.getDuration()))
            .map(l -> l.get(1) == null ? TimeUtils.absoluteDifference(l.get(0), LocalDateTime.now()) : l.get(1))
            .reduce(TimeUtils.getFirstEpochDate(), TimeUtils::sum);

        if (TimeUtils.seconds(curHoaTime) >= yearsRequiredInHoa) {
            return super.checkNext(memberships, hoaID);
        }
        throw new InvalidParticipantException("Participant hasn't been in the HOA long enough");
    }
}
