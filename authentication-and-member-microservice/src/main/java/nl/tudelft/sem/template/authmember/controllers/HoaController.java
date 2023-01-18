package nl.tudelft.sem.template.authmember.controllers;

import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.HoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.services.HoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/member")
public class HoaController {

    private transient MemberService memberService;
    private transient HoaService hoaService;

    private transient AuthManager authManager;
    private final transient String unauthorizedMessage = "Access is not allowed";

    /**
     * Instantiates a new MemberController.
     */
    @Autowired
    public HoaController(HoaService hoaService, AuthManager authManager) {
        this.hoaService = hoaService;
        this.authManager = authManager;
    }

    /**
     * Adds user to the HOA and creates a new Membership if succeeds.
     * Only succeeds if a user exists and doesn't have an active membership in the HOA.
     */
    @PostMapping("/joinHOA")
    public ResponseEntity<Membership> joinHoa(@RequestBody JoinHoaModel model,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            authManager.validateMember(model.getMemberId());
            hoaService.joinHoa(model, token);
            return ResponseEntity.ok().build();
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (MemberAlreadyInHoaException | IllegalArgumentException | MemberDifferentAddressException
                 | BadJoinHoaModelException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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
            authManager.validateMember(model.getMemberId());
            Membership membership = hoaService.leaveHoa(model);
            return ResponseEntity.ok(membership);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hoa or member are not found.", e);
        }
    }

    /**
     * Checks whether a user and HOA exist.
     */
    public void validateExistence(HoaModel model) {
        try {
            authManager.validateMember(model.getMemberId());
            memberService.getMember(model.getMemberId());
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOA or member are not stored", e);
        }
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


    /** Setter method used when HoaService needs to be mocked
     * @param h - HoaService to be mocked
     */
    public void setHoaService(HoaService h) {
        this.hoaService = h;
    }
}
