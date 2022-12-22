package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.authentication.AuthManager;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotBoardForTooLongValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotInAnyOtherBoardValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.TimeInCurrentHoaValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.Validator;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.models.RemoveVoteModel;
import nl.tudelft.sem.template.hoa.models.TimeModel;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;

@RestController
@RequestMapping("/voting")
public class ElectionController {

    private final transient AuthManager authManager;
    private final transient HoaRepo hoaRepo;

    @Autowired
    public ElectionController(AuthManager authManager,
                              HoaRepo hoaRepo) {
        this.authManager = authManager;
        this.hoaRepo = hoaRepo;
    }

    /**
     * Endpoint for creating a proposal
     *
     * @param model the proposal
     * @return The created proposal or bad request
     */
    @PostMapping("/proposal")
    public ResponseEntity<Object> createProposal(@RequestBody ProposalRequestModel model,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            validateMemberInHOA(model.hoaId, authManager.getMemberId(), true, token);
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
            if (!model.memberId.equals(authManager.getMemberId()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access is not allowed");
            fetchElectionAsEntity(model.electionId, true, token);
            return ResponseEntity.ok(ElectionUtils.vote(model));
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getMessage(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  Endpoint for removing a vote on an election
     *
     * @param model the vote to be removed
     * @return the status of the removal of the vote
     */
    @PostMapping("/removeVote")
    public ResponseEntity<HttpStatus> removeVote(@RequestBody RemoveVoteModel model) {
        try {
            return ResponseEntity.ok(ElectionUtils.removeVote(model));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove vote", e);
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
            return fetchElectionAsEntity(electionId, false, token);
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
            ResponseEntity<Object> e = fetchElectionAsEntity(electionId, true, token);
            Object result = ElectionUtils.concludeElection(electionId);
            if (e.getClass().getName().equals("BoardElection")) {
                LocalDateTime scheduledFor = (LocalDateTime) e.getClass().getDeclaredField("scheduledFor").get(e);
                long hoaId = (Long) e.getClass().getDeclaredField("hoaId").get(e);
                // start automatic annual board election
                Integer[] nums = Arrays.stream(scheduledFor.plusYears(1)
                        .format(DateTimeFormatter.ISO_DATE_TIME)
                        .split("\\D+")).map(Integer::parseInt).toArray(Integer[]::new);
                ElectionUtils.cyclicCreateBoardElection(new BoardElectionRequestModel(hoaId, 1,
                        List.of(), "Annual board election",
                        "This is the auto-generated annual board election", new TimeModel(nums)));
                // clear board
                MembershipUtils.resetBoard(hoaId);
                // promote winners and demote rest of board
                MembershipUtils.promoteWinners(result, hoaId);
            } else if (e.getClass().getName().equals("Proposal") && (Boolean) result) {
                long hoaId = (Long) e.getClass().getDeclaredField("hoaId").get(e);
                Optional<Hoa> hoa = hoaRepo.findById(hoaId);
                if (hoa.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Hoa with given id does not exists?");
                // for each member in the HOA, add an entry in notification mapper
                for (String memberId : MembershipUtils.getActiveMembershipsOfHoa(hoaId, token)
                        .stream().map(MembershipResponseModel::getMemberId).collect(Collectors.toList())) {
                    // PMD... cmon, really?
                    String rule = (String) e.getClass().getDeclaredField("description").get(e);
                    hoa.get().notify(memberId, rule);
                }
                // explicit save due to lack of hoaService
                hoaRepo.save(hoa.get());
            }
            return ResponseEntity.ok(result);
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
            handler.handle(memberships, hoaID);
            return ResponseEntity.ok(ElectionUtils.joinElection(authManager.getMemberId(), hoaID));
        } catch (Exception e) {
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
            validateMemberInHOA(hoaID, authManager.getMemberId(), false, token);
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
     */
    private ResponseEntity<Object> fetchElectionAsEntity(@PathVariable("id") int electionId, boolean boardCheck,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String token)
            throws IllegalAccessException, InvocationTargetException {
        Object e = ElectionUtils.getElectionById(electionId);
        try {
            if (!e.getClass().getDeclaredMethod("getStatus").invoke(e).equals("finished")) {
                Object val = e.getClass().getDeclaredMethod("getHoaId").invoke(e);
                validateMemberInHOA((long) val, authManager.getMemberId(),
                        !boardCheck || e.getClass().getName().equals("Proposal"), token);
            }
            return ResponseEntity.ok(e);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
