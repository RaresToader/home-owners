package nl.tudelft.sem.template.authmember.controllers;

import java.util.List;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.HoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.services.HoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/member")
public class MemberController {

    private final transient MemberService memberService;
    private final transient HoaService hoaService;
    private final transient MembershipService membershipService;

    /**
     * Instantiates a new MemberController.
     */
    @Autowired
    public MemberController(MemberService memberService, HoaService hoaService, MembershipService membershipService) {
        this.membershipService = membershipService;
        this.memberService = memberService;
        this.hoaService = hoaService;
    }

    /**
     * Register a new member.
     */
    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody RegistrationModel request) {
        try {
            Member member = memberService.registerUser(request);
            return ResponseEntity.ok(member);

        } catch (MemberAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member already exists", e);
        }
    }

    /**
     * Change member's password.
     */
    @PostMapping("/updatePassword")
    public ResponseEntity<Member> updatePassword(@RequestBody RegistrationModel request) {
        try {
            Member member = memberService.updatePassword(request);
            return ResponseEntity.ok(member);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member doesn't exist", e);
        }
    }

    /**
     * Get a member by its id.
     *
     * @param memberId the member id
     * @return the member
     */
    @GetMapping("/get/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable String memberId) {
        try {
            Member member = memberService.getMember(memberId);
            return ResponseEntity.ok(member);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not exist", e);
        }
    }

    /**
     * Adds user to the HOA and creates a new Membership if succeeds.
     * Only succeeds if a user exists and doesn't have an active membership in the HOA.
     */
    @PostMapping("/joinHOA")
    public ResponseEntity<Membership> joinHoa(@RequestBody JoinHoaModel model) {
        try {
            Membership membership = hoaService.joinHoa(model);
            return ResponseEntity.ok(membership);
        } catch (MemberAlreadyInHoaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member already in Hoa", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hoa or member do not exist", e);
        }
    }

    /**
     * Deactivates a user's membership in the HOA.
     * Only succeeds if a member exists and has an active membership.
     */
    @PostMapping("/leaveHOA")
    public ResponseEntity<Membership> leaveHoa(@RequestBody GetHoaModel model) {
        validateExistence(model);
        try {
            Membership membership = hoaService.leaveHoa(model);
            return ResponseEntity.ok(membership);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hoa or member are not found.", e);
        }
    }

    /**
     * Gets current (active) membership in HOA.
     */
    @GetMapping("/getActiveMembership")
    public ResponseEntity<Membership> getMembership(@RequestBody GetHoaModel model) {
        validateExistence(model);
        try {
            Membership membership = hoaService.getCurrentMembership(model.getMemberId(), model.getHoaId());
            return ResponseEntity.ok(membership);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member are not stored", e);
        }
    }

    /**
     * Returns all memberships for a given HOA (including inactive).
     */
    @GetMapping("/getMembershipsForHOA")
    public ResponseEntity<List<Membership>> getMembershipsForHoa(@RequestBody GetHoaModel model) {
        validateExistence(model);
        try {
            List<Membership> memberships = hoaService.getMembershipsForHoa(model.getMemberId(), model.getHoaId());
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member do not exist", e);
        }
    }

    /**
     * Returns all memberships for user(including inactive).
     */
    @GetMapping("/getMemberships/{memberId}")
    public ResponseEntity<List<Membership>> getMemberships(@PathVariable String memberId) {
        try {
            memberService.getMember(memberId); //Validate existence
            List<Membership> memberships = membershipService.getMembershipsForMember(memberId);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member has not been found", e);
        }
    }

    /**
     * Returns all active memberships for user.
     */
    @GetMapping("/getActiveMemberships/{memberId}")
    public ResponseEntity<List<Membership>> getActiveMemberships(@PathVariable String memberId) {
        try {
            memberService.getMember(memberId); //Validate existence
            List<Membership> memberships = membershipService.getActiveMemberships(memberId);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not exist", e);
        }
    }

    /**
     * Checks whether a user and HOA exist.
     */
    //TODO: Verify that HOA exists
    public void validateExistence(HoaModel model) {
        try {
            memberService.getMember(model.getMemberId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member are not stored", e);
        }
    }

}
