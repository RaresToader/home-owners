package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    /**
     *  Endpoint for creating a proposal
     *
     * @param model the proposal
     * @return The created proposal or bad request
     */
    @PostMapping("/proposal")
    public ResponseEntity<String> createProposal(@RequestBody ProposalRequestModel model) {
        try {
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
    public ResponseEntity<String> createBoardElection(@RequestBody BoardElectionRequestModel model) {
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
    public ResponseEntity<String> getElectionById(@PathVariable("id") int electionId) {
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
}
