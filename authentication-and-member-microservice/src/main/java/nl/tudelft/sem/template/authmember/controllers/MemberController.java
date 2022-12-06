package nl.tudelft.sem.template.authmember.controllers;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHOAException;
import nl.tudelft.sem.template.authmember.models.HOAModel;
import nl.tudelft.sem.template.authmember.models.JoinHOAModel;
import nl.tudelft.sem.template.authmember.models.GetHOAModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.services.HOAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final transient MemberService memberService;
    private final transient HOAService hoaService;
    private final transient MembershipService membershipService;

    /**
     * Instantiates a new MemberController.
     *
     */
    @Autowired
    public MemberController(MemberService memberService, HOAService hoaService, MembershipService membershipService) {
        this.membershipService = membershipService;
        this.memberService = memberService;
        this.hoaService = hoaService;
    }

    /**
     * Register a new member
     */
    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody RegistrationModel request){
        try {
            Member member = memberService.registerUser(request);
            return ResponseEntity.ok(member);

        } catch (MemberAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member already exists", e);
        }
    }

    /**
     * Change member's password
     */
    @PostMapping("/updatePassword")
    public ResponseEntity<Member> updatePassword(@RequestBody RegistrationModel request){
        try {
            Member member = memberService.updatePassword(request);
            return ResponseEntity.ok(member);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member doesn't exist", e);
        }
    }

    @GetMapping("/get/{memberID}")
    public ResponseEntity<Member> getMember(@PathVariable String memberID){
        try {
            Member member = memberService.getMember(memberID);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member doesn't exist", e);
        }
    }

    /**
     * Adds user to the HOA and creates a new Membership if succeeds.
     * Only succeeds if a user exists and doesn't have an active membership in the HOA.
     */
    @PostMapping("/joinHOA")
    public ResponseEntity<Membership> joinHOA(@RequestBody JoinHOAModel model) {
        try {
            Membership membership = hoaService.joinHOA(model);
            return ResponseEntity.ok(membership);
        } catch (MemberAlreadyInHOAException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member already in HOA", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member doesn't exist", e);
        }
    }

    /**
     * Deactivates a user's membership in the HOA.
     * Only succeeds if a member exists and has an active membership.
     */
    @PostMapping("/leaveHOA")
    public ResponseEntity<Membership> leaveHOA(@RequestBody GetHOAModel model) {
        validateExistence(model);
        try {
            Membership membership = hoaService.leaveHOA(model);
            return ResponseEntity.ok(membership);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member doesn't exist", e);
        }
    }

    /**
     * Gets current (active) membership in HOA.
     */
    @GetMapping("/getActiveMembership")
    public ResponseEntity<Membership> getMembership(@RequestBody GetHOAModel model) {
        validateExistence(model);
        try {
            Membership membership = hoaService.getCurrentMembership(model.getMemberID(), model.getHoaID());
            return ResponseEntity.ok(membership);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member doesn't exist", e);
        }
    }

    /**
     * Returns all memberships for a given HOA (including inactive).
     */
    @GetMapping("/getMembershipsForHOA")
    public ResponseEntity<List<Membership>> getMembershipsForHOA(@RequestBody GetHOAModel model) {
        validateExistence(model);
        try {
            List<Membership> memberships = hoaService.getMembershipsForHOA(model.getMemberID(), model.getHoaID());
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member doesn't exist", e);
        }
    }

    /**
     * Returns all memberships for user(including inactive).
     */
    @GetMapping("/getMemberships/{memberID}")
    public ResponseEntity<List<Membership>> getMemberships(@PathVariable String memberID) {
        try {
            memberService.getMember(memberID); //Validate existence
            List<Membership> memberships = membershipService.getMembershipsForMember(memberID);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member doesn't exist", e);
        }
    }

    /**
     * Returns all active memberships for user.
     */
    @GetMapping("/getActiveMemberships/{memberID}")
    public ResponseEntity<List<Membership>> getActiveMemberships(@PathVariable String memberID) {
        try {
            memberService.getMember(memberID); //Validate existence
            List<Membership> memberships = membershipService.getActiveMemberships(memberID);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member doesn't exist", e);
        }
    }

    /**
     * Checks whether a user and HOA exist
     */
    //TODO: Verify that HOA exists
    public void validateExistence(HOAModel model) {
        try {
            memberService.getMember(model.getMemberID());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member doesn't exist", e);
        }
    }

}
