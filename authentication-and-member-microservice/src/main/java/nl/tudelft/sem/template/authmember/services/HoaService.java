package nl.tudelft.sem.template.authmember.services;

import java.util.List;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.HoaResponseModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.utils.HoaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service to process any HOA-related queries for Member.
 * Communicates with the HOA microservice to provide and retrieve necessary data
 * Requests pass through it to allow for smoother addition of business logic
 */
@Service
public class HoaService {

    private transient MembershipService membershipService;

    @Autowired
    public HoaService(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    /**
     * Joins HOA if all requirements are fulfilled.
     *
     * @throws IllegalArgumentException    thrown if hoa does not exist
     * @throws MemberAlreadyInHoaException thrown if the party requesting is already part of the hoa.
     */
    public String joinHoa(JoinHoaModel model, String token) throws
            MemberAlreadyInHoaException, MemberDifferentAddressException, BadJoinHoaModelException {
        try {
            List<Membership> activeMemberships = this.membershipService.getActiveMemberships(model.getMemberId());
            for (Membership membership : activeMemberships) {
                if (membership.getHoaId() == model.getHoaId()) {
                    throw new MemberAlreadyInHoaException(model);
                }
            }
            HoaResponseModel hoa = HoaUtils.getHoaById(model.getHoaId(), token);
            if (!hoa.getCountry().equals(model.getAddress().getCountry())
                    || !hoa.getCity().equals(model.getAddress().getCity())) {
                throw new MemberDifferentAddressException(model);
            }
            membershipService.saveMembership(model, false);
            return model.getMemberId();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Hoa does not exist!");
        } catch (BadJoinHoaModelException e) {
            throw new BadJoinHoaModelException("Bad join hoa model!");
        }
    }

    /**
     * Invalidates current membership, if there is an active one.
     *
     * @throws IllegalArgumentException if there is no active membership between the user and hoa
     */
    public Membership leaveHoa(GetHoaModel model) throws IllegalArgumentException {
        return membershipService.stopMembership(model);
    }

    /**
     * Get the current membership for a specific hoa.
     *
     * @param memberId the id of the member.
     * @param hoaId    the id of the hoa
     * @return the current membership
     * @throws IllegalArgumentException thrown if there is none
     */
    public Membership getCurrentMembership(String memberId, long hoaId) throws IllegalArgumentException {
        return membershipService.getActiveMembershipByMemberAndHoa(memberId, hoaId);
    }

    /**
     * Get all the memberships for a specific hoa.
     *
     * @param memberId the member id
     * @param hoaId    the hoa id
     * @return all the memberships
     */
    public List<Membership> getMembershipsForHoa(String memberId, long hoaId) {
        return membershipService.getMembershipsByMemberAndHoa(memberId, hoaId);
    }

    /**
     * Setter for MembershipService which enables mocking.
     *
     * @param m the mock of MembershipService
     */
    public void setMembershipService(MembershipService m) {
        this.membershipService = m;
    }
}
