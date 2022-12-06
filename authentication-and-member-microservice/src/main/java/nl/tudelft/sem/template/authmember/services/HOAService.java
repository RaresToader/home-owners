package nl.tudelft.sem.template.authmember.services;


import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHOAException;
import nl.tudelft.sem.template.authmember.models.JoinHOAModel;
import nl.tudelft.sem.template.authmember.models.GetHOAModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to process any HOA-related queries for Member
 * Communicates with the HOA microservice to provide and retrieve necessary data
 * Requests pass through it to allow for smoother addition of business logic
 */
@Service
public class HOAService {

    private final transient MembershipService membershipService;

    @Autowired
    public HOAService(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    /**
     * Joins HOA if all requirements are fulfilled
     * @throws IllegalArgumentException
     */
    //TODO: Call the HOA microservice to persist the data
    //TODO: Add unique exceptions for HOA doesn't exist and user already in HOA
    public Membership joinHOA(JoinHOAModel model) throws MemberAlreadyInHOAException {
        //TODO: API Logic to connect with HOA here

        return membershipService.saveMembership(model);
    }

    /**
     * Invalidates current membership, if there is an active one
     * @throws IllegalArgumentException if there is no active membership between the user and hoa
     */
    public Membership leaveHOA(GetHOAModel model) throws IllegalArgumentException {
        return membershipService.stopMembership(model);
    }

    public Membership getCurrentMembership(String memberID, int hoaID) throws IllegalArgumentException{
        return membershipService.getActiveMembershipByMemberAndHOA(memberID, hoaID);
    }

    public List<Membership> getMembershipsForHOA(String memberID, int hoaID) {
        return membershipService.getMembershipsByMemberAndHOA(memberID, hoaID);
    }
}
