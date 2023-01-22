package nl.tudelft.sem.template.authmember.controllers;

import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.converters.MembershipConverter;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.HoaModel;
import nl.tudelft.sem.template.authmember.models.MembershipResponseModel;
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

import java.util.List;


@RestController
@RequestMapping("/member")
public class MembershipController {

    private transient MemberService memberService;
    private transient HoaService hoaService;
    private transient MembershipService membershipService;

    private transient AuthManager authManager;
    private final transient String unauthorizedMessage = "Access is not allowed";

    private static final String SECRET_CLEAR_BOARD_KEY = "Thisisacustomseckeyforclear";

    private static final String SECRET_PROMOTION_KEY = "Thisisacustomseckeyforpromotion";

    /**
     * Instantiates a new MemberController.
     */
    @Autowired
    public MembershipController(MemberService memberService, HoaService hoaService,
                                MembershipService membershipService, AuthManager authManager) {
        this.membershipService = membershipService;
        this.memberService = memberService;
        this.hoaService = hoaService;
        this.authManager = authManager;
    }

    /**
     * Gets current (active) membership in HOA.
     */
    @GetMapping("/getActiveMembership")
    public ResponseEntity<Membership> getMembership(@RequestBody GetHoaModel model) {
        try {
            validateExistence(model);
            Membership membership = hoaService.getCurrentMembership(model.getMemberId(), model.getHoaId());
            return ResponseEntity.ok(membership);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member are not stored", e);
        }
    }

    /**
     * Endpoint to retrieve a membership by id.
     *
     * @param membershipId the membership id.
     * @return the membership with the id provided
     */
    @GetMapping("/getMembershipById/{membershipId}")
    public ResponseEntity<MembershipResponseModel> getMembershipById(@PathVariable long membershipId) {
        try {
            Membership membership = membershipService.getMembership(membershipId);
            MembershipResponseModel model = MembershipConverter.convert(membership);
            return ResponseEntity.ok(model);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Returns all memberships for a given HOA (including inactive).
     */
    @GetMapping("/getMembershipsForHOA")
    public ResponseEntity<List<Membership>> getMembershipsForHoa(@RequestBody GetHoaModel model) {
        try {
            validateExistence(model);
            List<Membership> memberships = hoaService.getMembershipsForHoa(model.getMemberId(), model.getHoaId());
            return ResponseEntity.ok(memberships);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member do not exist", e);
        }
    }

    /**
     * Returns all memberships for user (including inactive).
     */
    @GetMapping("/getMemberships/{memberId}")
    public ResponseEntity<List<MembershipResponseModel>> getMemberships(@PathVariable String memberId) {
        try {
            authManager.validateMember(memberId);
            memberService.getMember(memberId); //Validate existence
            List<Membership> memberships = membershipService.getMembershipsForMember(memberId);
            return ResponseEntity.ok(MembershipConverter.convertMany(memberships));
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member has not been found", e);
        }
    }

    /**
     * Rest endpoint to get all memberships.
     *
     * @return all memberships
     */
    @GetMapping("/getAllMemberships")
    public ResponseEntity<List<Membership>> getAllMemberships() {
        return ResponseEntity.ok(this.membershipService.getAll());
    }

    /**
     * Rest endpoint to get all active memberships of an HOA
     *
     * @param hoaId id of HOA
     * @return List of all members
     */
    @GetMapping("/getAllMemberships/{hoaId}")
    public ResponseEntity<List<MembershipResponseModel>> getAllMemberships(@PathVariable Long hoaId) {
        return ResponseEntity.ok(MembershipConverter.convertMany(
                    this.membershipService.getActiveMembershipsByHoaId(hoaId)));
    }

    /**
     * >>>>>>> Draft board election
     * Returns all active memberships for user.
     */
    @GetMapping("/getActiveMemberships/{memberId}")
    public ResponseEntity<List<MembershipResponseModel>> getActiveMemberships(@PathVariable String memberId) {
        try {
            authManager.validateMember(memberId);
            memberService.getMember(memberId); //Validate existence
            List<MembershipResponseModel> memberships =
                    MembershipConverter.convertMany(membershipService.getActiveMemberships(memberId));
            return ResponseEntity.ok(memberships);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not exist", e);
        }
    }

    /**
     * Checks whether a user and HOA exist.
     */
    public void validateExistence(HoaModel model) throws IllegalAccessException, IllegalArgumentException {
        try {
            authManager.validateMember(model.getMemberId());
            memberService.getMember(model.getMemberId());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Endpoint for demoting board members into regular members (effective clearing the board of the HOA)
     *
     * @param hoaId id of Hoa to consider
     * @param key   Semi-secure validation key to ensure that only other ms could have called this endpoint, not a user
     * @return Boolean to represent the success of the operation
     */
    @PostMapping("/resetBoard/{hoaId}")
    public ResponseEntity<Boolean> demote(@PathVariable long hoaId, @RequestBody String key) {
        if (!SECRET_CLEAR_BOARD_KEY.equals(key)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Unauthorized to clear board");
        ResponseEntity<List<MembershipResponseModel>> memberships = this.getAllMemberships(hoaId);
        if (memberships.getBody().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Given hoa either does not exist, or no members are in it");
        memberships.getBody().stream().filter(MembershipResponseModel::isBoardMember).forEach(m ->
                membershipService.changeBoard(m, false));
        return ResponseEntity.ok(true);
    }

    /**
     * Endpoint for promoting a regular member into a board member
     * Always called after clearing a board, hence no check if the member is already a board member
     *
     * @param hoaId id of Hoa to consider
     * @param mem   list of all members (by memberId) that should be promoted
     * @param key   Semi-secure validation key to ensure that only other ms could have called this endpoint, not a user
     * @return Boolean to represent the success of the operation
     */
    @PostMapping("/promoteWinners/{hoaId}/{key}")
    public ResponseEntity<Boolean> promote(@PathVariable long hoaId, @RequestBody List<String> mem,
                                           @PathVariable String key) {
        if (!SECRET_PROMOTION_KEY.equals(key)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Unauthorized to clear board");
        ResponseEntity<List<MembershipResponseModel>> memberships = this.getAllMemberships(hoaId);
        if (memberships.getBody().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Given hoa either does not exist, or no members are in it");
        memberships.getBody().stream().filter(m -> mem.contains(m.getMemberId())).forEach(m ->
                membershipService.changeBoard(m, true));
        return ResponseEntity.ok(true);
    }

    /** Setter method used when AuthManager needs to be mocked
     * @param a - AuthManager to be mocked
     */
    public void setAuthenticationManager(AuthManager a) {
        this.authManager = a;
    }

    /** Setter method used when MemberService needs to be mocked
     * @param m - MemberService to be mocked
     */
    public void setMemberService(MemberService m) {
        this.memberService = m;
    }

    /** Setter method used when MembershipService needs to be mocked
     * @param m - MembershipService to be mocked
     */
    public void setMembershipService(MembershipService m) {
        this.membershipService = m;
    }

    /** Setter method used when HoaService needs to be mocked
     * @param h - HoaService to be mocked
     */
    public void setHoaService(HoaService h) {
        this.hoaService = h;
    }
}
