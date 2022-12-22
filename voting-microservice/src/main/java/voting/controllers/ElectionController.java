package voting.controllers;

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
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.exceptions.BoardElectionAlreadyCreated;
import voting.exceptions.CannotProceedVote;
import voting.exceptions.ElectionCannotBeCreated;
import voting.exceptions.ElectionDoesNotExist;
import voting.exceptions.ProposalAlreadyCreated;
import voting.exceptions.ThereIsNoVote;
import voting.models.RemoveVoteModel;
import voting.models.VotingModel;
import voting.services.ElectionService;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    private final transient ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Creates a new Proposal with default time of 2 weeks to start the voting
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
        } catch (ProposalAlreadyCreated | ElectionCannotBeCreated e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Creates a new Proposal with a specified time to start the voting
     *
     * @param model ProposalModel needed to instantiate a proposal
     * @return ResponseEntity containing new Proposal if creation was successful,
     * Bad request otherwise
     */
    @PostMapping("/specifiedProposal")
    public ResponseEntity<Proposal> createProposal(@RequestBody ProposalModel model, @RequestBody TemporalAmount startAfter) {
        try {
            Proposal proposal = electionService.createProposal(model, startAfter);
            return ResponseEntity.ok(proposal);
        } catch (ProposalAlreadyCreated | ElectionCannotBeCreated e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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
        } catch (BoardElectionAlreadyCreated | ElectionCannotBeCreated e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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
            electionService.vote(model, LocalDateTime.now());
            return ResponseEntity.ok().build();
        } catch (ElectionDoesNotExist | CannotProceedVote e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Handles removing vote requests by members of an HOA
     *
     * @param model RemoveVoteModel containing the required fields
     * @return Response entity to note whether the removing of the vote was successful
     */
    @PostMapping("/removeVote")
    public ResponseEntity<HttpStatus> removeVote(@RequestBody RemoveVoteModel model) {
        try {
            electionService.removeVote(model, LocalDateTime.now());
            return ResponseEntity.ok().build();
        } catch (ElectionDoesNotExist | ThereIsNoVote | CannotProceedVote e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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
    public ResponseEntity<Object> concludeElection(@PathVariable("id") int electionId) {
        try {
            Object result = electionService.conclude(electionId);
            return ResponseEntity.ok(result);
        } catch (ElectionDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Adds a participant to board election
     * Doesn't conduct background checks, relies on an eligible member being provided
     * @param memberId - member that joins the election
     * @param hoaId - id of hoa for which the election is held
     * @return true if member is added as participant, false if there is no election ongoing
     */
    @PostMapping("/joinElection/{memberId}/{hoaId}")
    public ResponseEntity<Boolean> joinElection(@PathVariable String memberId, @PathVariable long hoaId) {
        try {
            boolean result = electionService.addParticipantToBoardElection(memberId, hoaId);
            return ResponseEntity.ok(result);
        } catch (ElectionDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Election for given HOA not found", e);
        }
    }

    /**
     * Removes a participant to board election
     * @param memberId - member that leaves the election
     * @param hoaId - id of hoa for which the election is held
     * @return true if member is removes as participant,
     * false if there is no election ongoing or the member didn't participate
     */
    @PostMapping("/leaveElection/{memberId}/{hoaId}")
    public ResponseEntity<Boolean> leaveElection(@PathVariable String memberId, @PathVariable long hoaId) {
        try {
            boolean result = electionService.removeParticipantFromBoardElection(memberId, hoaId);
            return ResponseEntity.ok(result);
        } catch (ElectionDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Election for given HOA not found or participant not partaking", e);
        }
    }
}
