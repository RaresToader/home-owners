package nl.tudelft.sem.template.authmember.domain.converters;

import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.models.MembershipResponseModel;

import java.util.List;
import java.util.stream.Collectors;

public class MembershipConverter {

    /**
     * Move data between Membership and MRM objects
     */
    public static MembershipResponseModel convert(Membership membership) {
        return new MembershipResponseModel(membership.getMembershipId(),
            membership.getMemberId(), membership.getHoaId(),
            membership.getAddress().getCountry(), membership.getAddress().getCity(),
            membership.isInBoard(), membership.getStartTime(), membership.getDuration());
    }

    public static List<MembershipResponseModel> convertMany(List<Membership> membershipList) {
        return membershipList.stream().map(MembershipConverter::convert).collect(Collectors.toList());
    }

}
