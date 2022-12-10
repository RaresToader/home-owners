package nl.tudelft.sem.template.authmember.domain.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.services.TimeUtils;
import org.springframework.stereotype.Service;

/**
 * A DDD service for member-related queries.
 */
@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;

    /**
     * Instantiates a new MembershipService.
     */
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Register a new membership.
     *
     * @throws MemberAlreadyInHoaException if there is an active membership for that HOA.
     */
    public void saveMembership(JoinHoaModel model) throws MemberAlreadyInHoaException {
        if (membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull(model.getMemberId(),
                model.getHoaId()).isPresent()) {
            throw new MemberAlreadyInHoaException(model);
        } else {
            membershipRepository.save(new Membership(model.getMemberId(),
                    model.getHoaId(), model.getAddress(), LocalDateTime.now(), null, false));
        }
    }

    /**
     * Set membership as inactive. Adds a duration timestamp to membership.
     *
     * @return Membership - the deactivated membership
     */
    public Membership stopMembership(GetHoaModel model) {
        Membership membership = getActiveMembershipByMemberAndHoa(model.getMemberId(), model.getHoaId());
        membership.setDuration(TimeUtils.absoluteDifference(membership.getStartTime(), LocalDateTime.now()));
        membershipRepository.save(membership);
        return membership;
    }

    public List<Membership> getMembershipsForMember(String memberId) {
        return membershipRepository.findAllByMemberId(memberId);
    }

    public List<Membership> getMembershipsByMemberAndHoa(String memberId, long hoaId) {
        return membershipRepository.findAllByMemberIdAndHoaId(memberId, hoaId);
    }

    public List<Membership> getActiveMemberships(String memberId) {
        return membershipRepository.findAllByMemberIdAndDurationIsNull(memberId);
    }

    /**
     * Returns the current membership in a given Hoa, if one exists.
     */
    public Membership getActiveMembershipByMemberAndHoa(String memberId, long hoaId) {
        Optional<Membership> membership = membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull(memberId, hoaId);
        if (membership.isPresent()) {
            return membership.get();
        }
        throw new IllegalArgumentException(memberId + " " + hoaId);
    }

    /**
     * Method to query a membership by id.
     *
     * @param membershipId the id of the membership
     * @return the membership, if found
     */
    public Membership getMembership(long membershipId) {
        if (membershipRepository.findByMembershipId(membershipId).isPresent()) {
            return membershipRepository.findByMembershipId(membershipId).get();
        } else {
            throw new IllegalArgumentException(String.valueOf(membershipId));
        }
    }

    /**
     * Get all the memberships in the repository.
     *
     * @return all the memberships
     */
    public List<Membership> getAll() {
        return this.membershipRepository.findAll();
    }
}