package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHOAException;
import nl.tudelft.sem.template.authmember.models.JoinHOAModel;
import nl.tudelft.sem.template.authmember.models.GetHOAModel;
import nl.tudelft.sem.template.authmember.services.TimeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * A DDD service for member-related queries.
 */
@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;

    /**
     * Instantiates a new MembershipService.
     *
     */
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Register a new membership.
     * @Throws MemberAlreadyInHOAException if there is an active membership for that HOA.
     */
    public Membership saveMembership(JoinHOAModel model) throws MemberAlreadyInHOAException{
        Membership membership = new Membership(model.getMemberID(), model.getHoaID(), model.getAddress(), LocalDateTime.now(), null, false);
        if (membershipRepository.findByMemberIDAndHoaIDAndDurationIsNull(model.getMemberID(), model.getHoaID()).isPresent())  {
            throw new MemberAlreadyInHOAException(model);
        }
        membershipRepository.save(membership);
        return membership;
    }

    /**
     * Set membership as inactive. Adds a duration timestamp to membership.
     * @return Membership - the deactivated membership
     */
    public Membership stopMembership(GetHOAModel model) {
        Membership membership = getActiveMembershipByMemberAndHOA(model.getMemberID(), model.getHoaID());
        membership.setDuration(TimeUtils.absoluteDifference(membership.getStartTime(), LocalDateTime.now()));
        membershipRepository.save(membership);
        return membership;
    }

    public List<Membership> getMembershipsForMember(String memberID) {
        return membershipRepository.findAllByMemberID(memberID);
    }

    public List<Membership> getMembershipsByMemberAndHOA(String memberID, int hoaID) {
        return membershipRepository.findAllByMemberIDAndHoaID(memberID, hoaID);
    }

    public List<Membership> getActiveMemberships(String memberID) {
        return membershipRepository.findAllByMemberIDAndDurationIsNull(memberID);
    }

    /**
     * Returns the current membership in a given HOA, if one exists.
     */
    public Membership getActiveMembershipByMemberAndHOA(String memberID, int hoaID) {
        Optional<Membership> membership = membershipRepository.findByMemberIDAndHoaIDAndDurationIsNull(memberID, hoaID);
        if (membership.isPresent()) {
            return membership.get();
        }
        throw new IllegalArgumentException(memberID + " " + hoaID);
    }

    public Membership getMembership(String membershipID) {
        if (membershipRepository.existsByMembershipID(membershipID)) {
            return membershipRepository.findByMembershipID(membershipID).get();
        }
        throw new IllegalArgumentException(membershipID);
    }




}