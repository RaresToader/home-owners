package nl.tudelft.sem.template.authmember.services;

import java.util.List;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to process any HOA-related queries for Member.
 * Communicates with the HOA microservice to provide and retrieve necessary data
 * Requests pass through it to allow for smoother addition of business logic
 */
@Service
public class HoaService {

    private final transient MembershipService membershipService;

    @Autowired
    public HoaService(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    /**
     * Joins HOA if all requirements are fulfilled.
     *
     * @throws IllegalArgumentException thrown if hoa does not exist
     */
    //TODO: Call the HOA microservice to persist the data
    //TODO: Add unique exceptions for HOA doesn't exist and user already in HOA
    public void joinHoa(JoinHoaModel model) throws MemberAlreadyInHoaException {
        //TODO: API Logic to connect with HOA here

        membershipService.saveMembership(model);
    }

    /**
     * Invalidates current membership, if there is an active one.
     *
     * @throws IllegalArgumentException if there is no active membership between the user and hoa
     */
    public Membership leaveHoa(GetHoaModel model) throws IllegalArgumentException {
        return membershipService.stopMembership(model);
    }

    public Membership getCurrentMembership(String memberId, long hoaId) throws IllegalArgumentException {
        return membershipService.getActiveMembershipByMemberAndHoa(memberId, hoaId);
    }

    public List<Membership> getMembershipsForHoa(String memberId, long hoaId) {
        return membershipService.getMembershipsByMemberAndHoa(memberId, hoaId);
    }
}
