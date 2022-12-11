package voting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.domain.exceptions.BoardElectionAlreadyCreated;
import voting.domain.exceptions.ElectionDoesNotExist;
import voting.domain.exceptions.ProposalAlreadyCreated;
import voting.models.VotingModel;
import voting.services.ElectionService;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    private final ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Creates a new Proposal
     *
     * @param model ProposalModel needed to instantiate a proposal
     * @return ResponseEntity containing new Proposal if creation was successful,
     * Bad request otherwise
     */
    @PostMapping("/proposal")
    public ResponseEntity<Proposal> createProposal(@RequestBody ProposalModel model) {
        try {
            Proposal proposal = electionService.createProposal(model);
            return ResponseEntity.ok(proposal);
        } catch (ProposalAlreadyCreated e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create proposal", e);
        }
    }

    /**
     * Creates a new BoardElection
     *
     * @param model BoardElectionModel need to instantiate a board election
     * @return ResponseEntity containing new Proposal if creation was successful,
     * Bad request otherwise
     */
    @PostMapping("/boardElection")
    public ResponseEntity<BoardElection> createBoardElection(@RequestBody BoardElectionModel model) {
        try {
            BoardElection boardElection = electionService.createBoardElection(model);
            return ResponseEntity.ok(boardElection);
        } catch (BoardElectionAlreadyCreated e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create board election", e);
        }
    }

    /**
     * Handles voting requests by members of an HOA
     *
     * @param model VotingModel containing the required fields
     * @return Response entity to note whether the voting was successful
     */
    @PostMapping("/vote")
    public ResponseEntity<HttpStatus> vote(@RequestBody VotingModel model) {
        try {
            electionService.vote(model.electionId, model.membershipId, model.choice);
            return ResponseEntity.ok().build();
        } catch (ElectionDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election with provided id was not found", e);
        }
    }

    /**
     * Getter for an election by id
     *
     * @param electionId id for the election to fetch
     * @return Fetched election, if any with given id
     */
    @GetMapping("/getElection/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable("id") int electionId) {
        try {
            Election e = electionService.getElection(electionId);
            return ResponseEntity.ok(e);
        } catch (ElectionDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election with provided id was not found", e);
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
    @GetMapping("/conclude/{id}")
    public ResponseEntity<Object> concludeElection(@PathVariable("id") int id) {
        try {
            Object result = electionService.conclude(id);
            return ResponseEntity.ok(result);
        } catch (ElectionDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election with provided id was not found", e);
        }
    }
}
