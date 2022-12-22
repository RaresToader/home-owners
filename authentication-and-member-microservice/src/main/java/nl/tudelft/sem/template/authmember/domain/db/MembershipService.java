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
import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.springframework.stereotype.Service;

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
    public void saveMembership(JoinHoaModel model, boolean asBoard)
            throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        if (!validateCountryCityStreet(model.getAddress().getCity())
                || !validateCountryCityStreet(model.getAddress().getCountry())
                || !validateCountryCityStreet(model.getAddress().getStreet())
                || !validateStreetNumber(model.getAddress().getHouseNumber())
                || !validatePostalCode(model.getAddress().getPostalCode())) {
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
        }
    }

    /**
     * Validates the input for country, city, and street name. It must contain
     * only letters. It must be non-null, non-empty, non-blank and must contain only letters.
     * The first letter must be uppercase.
     *
     * @param name the name
     * @return true if the name satisfies the right format
     */
    public boolean validateCountryCityStreet(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            return false;
        }
        String trimmed = name.trim();
        if (trimmed.length() < 4 || trimmed.length() > 50) {
            return false;
        }
        if (!Character.isUpperCase(trimmed.charAt(0))) {
            return false;
        }
        for (int i = 1; i < trimmed.length(); i++) {
            if (!Character.isLetter(trimmed.charAt(i)) && !Character.isWhitespace(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;

    }

    /**
     * Method that validates the format of a postal code. It must be of type
     * "DDDDLL", where "D" represents a digit, and "L" a letter.
     *
     * @param postalCode the postal code
     * @return true if the postal code matches the format, false otherwise
     */
    public boolean validatePostalCode(String postalCode) {
        if (postalCode == null) {
            return false;
        }
        String trimmed = postalCode.trim();
        final int max = 6;
        if (trimmed.length() != max) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        for (int j = 4; j < 6; j++) {
            if (!Character.isLetter(trimmed.charAt(j))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the street number provided. Valid street numbers are the following:
     * "80A", "23". Leading and trailing spaces are allowed.
     *
     * @param number the street number
     * @return true if the number satisfies the format, false otherwise
     */
    public boolean validateStreetNumber(String number) {
        if (number == null || number.isBlank() || number.isEmpty()) {
            return false;
        }
        String trimmed = number.trim();
        for (int i = 0; i < trimmed.length() - 1; i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        if (!Character.isDigit(trimmed.charAt(trimmed.length() - 1))
                && !Character.isLetter(trimmed.charAt(trimmed.length() - 1))) {
            return false;
        }
        return true;
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
    public void changeBoard(Membership m, boolean shouldPromote)
            throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        GetHoaModel model = new GetHoaModel();
        model.setHoaId(m.getHoaId());
        model.setMemberId(m.getMemberId());
        stopMembership(model);
        JoinHoaModel jmodel = new JoinHoaModel();
        jmodel.setAddress(m.getAddress());
        jmodel.setMemberId(m.getMemberId());
        jmodel.setHoaId(m.getHoaId());
        saveMembership(jmodel, shouldPromote);
    }
}