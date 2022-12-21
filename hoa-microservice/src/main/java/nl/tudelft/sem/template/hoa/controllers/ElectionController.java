package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.authentication.AuthManager;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotBoardForTooLongValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotInAnyOtherBoardValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.TimeInCurrentHoaValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.Validator;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    private transient AuthManager authManager;

    @Autowired
    public ElectionController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Endpoint for creating a proposal
     *
     * @param model the proposal
     * @return The created proposal or bad request
     */
    @PostMapping("/proposal/{id}")
    public ResponseEntity<Object> createProposal(@PathVariable("id") long hoaId,
                                                 @RequestBody ProposalRequestModel model,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            validateMemberInHOA(hoaId, authManager.getMemberId(), true, token);
            return ResponseEntity.ok(ElectionUtils.createProposal(model));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
        }
    }

    /**
     * Endpoint for creating a board election
     *
     * @param model the board election
     * @return The created board election or bad request
     */
    @PostMapping("/boardElection")
    public ResponseEntity<Object> createBoardElection(@RequestBody BoardElectionRequestModel model) {
        try {
            return ResponseEntity.ok(ElectionUtils.createBoardElection(model));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create board election", e);
        }
    }

    /**
     * Endpoint for voting on an election
     *
     * @param model the vote
     * @return the status of the vote
     */
    @PostMapping("/vote")
    public ResponseEntity<HttpStatus> vote(@RequestBody VotingModel model,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            ResponseEntity<Object> e = getObjectResponseEntity(model.electionId, token);
            if (e != null) return ResponseEntity.ok(ElectionUtils.vote(model));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vote is unsuccessful.");
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Endpoint for getting an election by id
     *
     * @param electionId id for the election to fetch
     * @return Fetched election, if any with given id
     */
    @GetMapping("/getElection/{id}")
    public ResponseEntity<Object> getElectionById(@PathVariable("id") int electionId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            ResponseEntity<Object> e = getObjectResponseEntity(electionId, token);
            if (e != null) return e;
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election fetch was not successful.");
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Concludes an election based on id
     *
     * @param electionId Id of election to be concluded
     * @return Object that contains the winners
     * Boolean if it's a proposal
     * List of winning candidates otherwise
     */
    @PostMapping("/conclude/{id}")
    public ResponseEntity<Object> concludeElection(@PathVariable("id") int electionId,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            ResponseEntity<Object> e1 = getObjectResponseEntity(electionId, token);
            if (e1 != null) return e1;
            return ResponseEntity.ok(ElectionUtils.concludeElection(electionId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot vote", e);
        }
    }

    /**
     * Endpoint for joining an election
     *
     * @param hoaID Id of HOA that one wants to join
     * @param token auth token for verification, passed in header
     * @return Returns a boolean that represents the operation's success
     */
    @PostMapping("joinElection/{hoaID}")
    public ResponseEntity<Boolean> joinElection(@PathVariable long hoaID,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        //Fetch membership data
        try {
            List<MembershipResponseModel> memberships =
                    MembershipUtils.getMembershipsForUser(authManager.getMemberId(), token);
            Validator handler = new TimeInCurrentHoaValidator();
            Validator otherBoardValidator = new NotInAnyOtherBoardValidator();
            Validator notForTooLongValidator = new NotBoardForTooLongValidator();
            otherBoardValidator.setNext(notForTooLongValidator);
            handler.setNext(otherBoardValidator);
            try {
                handler.handle(memberships, hoaID);
                return ResponseEntity.ok(ElectionUtils.joinElection(authManager.getMemberId(), hoaID));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Endpoint for leaving an election
     *
     * @param hoaID Id of HOA that one wants to join
     * @return Returns a boolean that represents the operation's success
     */
    @PostMapping("leaveElection/{hoaID}")
    public ResponseEntity<Boolean> leaveElection(@PathVariable long hoaID,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            validateMemberInHOA(hoaID, authManager.getMemberId(), true, token);
            if (ElectionUtils.leaveElection(authManager.getMemberId(), hoaID))
                return ResponseEntity.ok(true);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member did not participate in the election");
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getMessage());
        }
    }

    /**
     * Validates if a member is in the specified HOA
     *
     * @param hoaID          id of HOA to check
     * @param memberID       id of member to check
     * @param alsoCheckBoard Also check if he is a board member
     * @param token          Authorization token used for validation
     */
    public void validateMemberInHOA(long hoaID, String memberID, boolean alsoCheckBoard, String token) {
        List<MembershipResponseModel> memberships =
                MembershipUtils.getActiveMembershipsForUser(memberID, token);
        if (memberships.stream().noneMatch(m -> m.getHoaId() == hoaID && (!alsoCheckBoard || m.isBoard())))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access is not allowed");
    }

    /**
     * Helper method to fetch an election an extract its hoaId
     *
     * @param electionId Id of election to fetch
     * @param token      Authorization token used for validation
     * @return A response entity containing the fetched Election as an Object, if it exists
     * @throws IllegalAccessException Thrown if fetched object does not have required fields
     */
    private ResponseEntity<Object> getObjectResponseEntity(@PathVariable("id") int electionId,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token)
            throws IllegalAccessException, InvocationTargetException {
        Object e = ElectionUtils.getElectionById(electionId);
        for (Method method : e.getClass().getDeclaredMethods()) {
            if (method.getName().equals("getHoaId")) {
                Object value = method.invoke(e);
                if (value == null) throw new IllegalArgumentException("Election fetch was not successful.");
                validateMemberInHOA((long) value, authManager.getMemberId(), false, token);
                return ResponseEntity.ok(e);
            }
        }
        return null;
    }
}
