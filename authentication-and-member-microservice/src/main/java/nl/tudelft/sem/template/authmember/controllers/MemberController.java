package nl.tudelft.sem.template.authmember.controllers;

import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authmember.authentication.JwtUserDetailsService;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.authmember.models.AuthenticationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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

    private transient MemberService memberService;

    private final transient AuthenticationManager authenticationManager;
    private final transient JwtTokenGenerator jwtTokenGenerator;
    private final transient JwtUserDetailsService jwtUserDetailsService;

    private transient AuthManager authManager;
    private final transient String unauthorizedMessage = "Access is not allowed";

    /**
     * Instantiates a new MemberController.
     */
    @Autowired
    public MemberController(MemberService memberService, AuthenticationManager authenticationManager,
                            JwtTokenGenerator jwtTokenGenerator, JwtUserDetailsService jwtUserDetailsService,
                            AuthManager authManager) {
        this.memberService = memberService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.authManager = authManager;
    }

    /**
     * Register a new member.
     */
    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody RegistrationModel request) {
        try {
            memberService.registerUser(request);
            return ResponseEntity.ok().build();
        } catch (MemberAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member already exists", e);
        } catch (BadRegistrationModelException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad username or password!", e);
        }
    }


    /**
     * Change member's password.
     */
    @PostMapping("/updatePassword")
    public ResponseEntity<Member> updatePassword(@RequestBody RegistrationModel request) {
        try {
            authManager.validateMember(request.getMemberId());
            Member member = memberService.updatePassword(request);
            return ResponseEntity.ok(member);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member doesn't exist", e);
        } catch (BadRegistrationModelException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad registration model", e);
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
            authManager.validateMember(memberId);
            Member member = memberService.getMember(memberId);
            return ResponseEntity.ok(member);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, unauthorizedMessage, e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member does not exist", e);
        }
    }

    /**
     * Endpoint for authentication.
     *
     * @param request The login model
     * @return JWT token if the login is successful
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseModel> authenticate(@RequestBody AuthenticationRequestModel request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getMemberId(),
                            request.getPassword()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getMemberId());
        final String jwtToken = jwtTokenGenerator.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponseModel(jwtToken));
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
}
