package nl.tudelft.sem.template.authmember.domain.db;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.MembershipResponseModel;
import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.springframework.stereotype.Service;

import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.*;

/**
 * A DDD service for member-related queries.
 */
@Service
public class MembershipService {
    private final transient MembershipRepository membershipRepository;
    private final transient MemberRepository memberRepository;

    /**
     * Instantiates a new MembershipService.
     */
    public MembershipService(MembershipRepository membershipRepository, MemberRepository memberRepository) {
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Register a new membership.
     *
     * @throws MemberAlreadyInHoaException if there is an active membership for that HOA.
     */
    public boolean saveMembership(JoinHoaModel model, boolean asBoard)
            throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        if (!validate(model)) {
            throw new BadJoinHoaModelException("Bad model!");
        }
        if (memberRepository.findByMemberId(model.getMemberId()).isEmpty()) {
            throw new IllegalArgumentException("Member not found!");
        }
        if (membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull(model.getMemberId(),
                model.getHoaId()).isPresent()) {
            throw new MemberAlreadyInHoaException(model);
        } else {
            membershipRepository.save(new Membership(model.getMemberId(),
                    model.getHoaId(), model.getAddress(), LocalDateTime.now(), null, asBoard));
            return true;
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

    /**
     * Retrieves all memberships for a certain memberId.
     *
     * @param memberId the memberId
     * @return the list of memberships
     */
    public List<Membership> getMembershipsForMember(String memberId) {
        return membershipRepository.findAllByMemberId(memberId);
    }

    /**
     * Retrieves all memberships for a member, for a certain hoa.
     *
     * @param memberId the memberId
     * @param hoaId    the hoaId
     * @return all the memberships
     */
    public List<Membership> getMembershipsByMemberAndHoa(String memberId, long hoaId) {
        return membershipRepository.findAllByMemberIdAndHoaId(memberId, hoaId);
    }

    public List<Membership> getActiveMemberships(String memberId) {
        return membershipRepository.findAllByMemberIdAndDurationIsNull(memberId);
    }

    public List<Membership> getActiveMembershipsByHoaId(long hoaId) {
        return membershipRepository.findAllByDurationIsNull().stream()
                .filter(m -> m.getHoaId() == hoaId).collect(Collectors.toList());
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

    /**
     * Toggle a membership's board status by stopping the current membership,
     * and starting a new one with toggled status
     *
     * @param m             Membership to consider
     * @param shouldPromote Whether it should be promoted - logically the same as final board status of member
     * @throws MemberAlreadyInHoaException thrown if member is already in hoa when saved
     *                                     SHOULD NOT HAPPEN DUE TO METHOD DESIGN
     * @throws BadJoinHoaModelException    thrown if hoa model is smelly
     *                                     SHOULD NOT HAPPEN DUE TO METHOD DESIGN
     */
    public void changeBoard(MembershipResponseModel m, boolean shouldPromote)
            throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        GetHoaModel model = new GetHoaModel();
        model.setHoaId(m.getHoaId());
        model.setMemberId(m.getMemberId());
        Membership old = stopMembership(model);
        JoinHoaModel jmodel = new JoinHoaModel();
        jmodel.setAddress(old.getAddress());
        jmodel.setMemberId(m.getMemberId());
        jmodel.setHoaId(m.getHoaId());
        saveMembership(jmodel, shouldPromote);
    }
}