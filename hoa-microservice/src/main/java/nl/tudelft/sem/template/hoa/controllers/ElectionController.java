package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.domain.electionchecks.NotBoardForTooLongValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotInAnyOtherBoardValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.TimeInCurrentHoaValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.Validator;
import nl.tudelft.sem.template.hoa.exception.MemberNotInBoardException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
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

import java.util.List;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    /**
     *  Endpoint for creating a proposal
     *
     * @param model the proposal
     * @return The created proposal or bad request
     */
    @PostMapping("/proposal/{id}")
    public ResponseEntity<Object> createProposal(@PathVariable("id") String memberId,
                    @RequestBody ProposalRequestModel model, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            List<MembershipResponseModel> memberships = MembershipUtils.getActiveMembershipsForUser(memberId, token);
            boolean ans = memberships.stream().noneMatch(m -> m.getHoaId() == model.getHoaId() && m.isBoard());
            if (ans) throw new MemberNotInBoardException("Member should be in the board to create a proposal");
            return ResponseEntity.ok(ElectionUtils.createProposal(model));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create proposal", e);
        }
    }

    /**
     *  Endpoint for creating a board election
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
     *  Endpoint for voting on an election
     *
     * @param model the vote
     * @return the status of the vote
     */
    @PostMapping("/vote")
    public ResponseEntity<HttpStatus> vote(@RequestBody VotingModel model) {
        try {
            return ResponseEntity.ok(ElectionUtils.vote(model));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot vote", e);
        }
    }

    /**
     * Endpoint for getting an election by id
     *
     * @param electionId id for the election to fetch
     * @return Fetched election, if any with given id
     */
    @GetMapping("/getElection/{id}")
    public ResponseEntity<Object> getElectionById(@PathVariable("id") int electionId) {
        try {
            return ResponseEntity.ok(ElectionUtils.getElectionById(electionId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot vote", e);
        }
    }

    /**
     * Concludes an election based on id
     *
     * @param id Id of election to be concluded
     * @return Object that contains the winners
     * Boolean if it's a proposal
     * List of winning candidates otherwise
     */
    @PostMapping("/conclude/{id}")
    public ResponseEntity<Object> concludeElection(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(ElectionUtils.concludeElection(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot vote", e);
        }
    }

    /**
     * Endpoint for becoming a participant in board election as a user
     */
    @PostMapping("joinElection/{memberID}/{hoaID}")
    public ResponseEntity<Boolean> joinElection(@PathVariable String memberID, @PathVariable long hoaID,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        //Fetch membership data
        try {
            List<MembershipResponseModel> memberships = MembershipUtils.getMembershipsForUser(memberID, token);
            Validator handler = new TimeInCurrentHoaValidator();
            Validator otherBoardValidator = new NotInAnyOtherBoardValidator();
            Validator notForTooLongValidator = new NotBoardForTooLongValidator();
            otherBoardValidator.setNext(notForTooLongValidator);
            handler.setNext(otherBoardValidator);
            try {
                handler.handle(memberships, hoaID);
                return ResponseEntity.ok(ElectionUtils.joinElection(memberID, hoaID));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Endpoint for leaving a board election as a user
     */
    @PostMapping("leaveElection/{memberID}/{hoaID}")
    public ResponseEntity<Boolean> leaveElection(@PathVariable String memberID, @PathVariable long hoaID) {
        boolean ret = ElectionUtils.leaveElection(memberID, hoaID);
        if (ret) {
            return ResponseEntity.ok(true);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member did not participate in the election");
    }
}
