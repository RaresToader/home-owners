package nl.tudelft.sem.template.hoa.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.models.TimeModel;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;

/**
 * The controller for the association.
 */
@RestController
@RequestMapping("/hoa")
public class HoaController {

    private final transient HoaService hoaService;
    private final transient RequirementService requirementService;
    private final transient HoaRepo hoaRepo;


    /**
     * Constructor for the HoaController.
     *
     * @param hoaService         the hoa service
     * @param requirementService the requirement service
     */
    @Autowired
    public HoaController(HoaService hoaService, RequirementService requirementService,
                         HoaRepo hoaRepo) {
        this.hoaService = hoaService;
        this.requirementService = requirementService;
        this.hoaRepo = hoaRepo;
    }

    /**
     * Endpoint for creating an association.
     *
     * @param request the possible new Hoa
     * @return 200 OK if the registration is successful
     */
    @PostMapping("/create")
    public ResponseEntity<Hoa> register(@RequestBody HoaRequestModel request) {
        try {
            Hoa newHoa = this.hoaService.registerHoa(request);
            Integer[] nums = Arrays.stream(LocalDateTime.now().plusWeeks(2)
                    .format(DateTimeFormatter.ISO_DATE_TIME)
                    .split("\\D+")).map(Integer::parseInt).toArray(Integer[]::new);
            // start automatic annual board election
            Object e = ElectionUtils.cyclicCreateBoardElection(new BoardElectionRequestModel(newHoa.getId(),
                    1, List.of(), "Annual board election",
                    "This is the auto-generated annual board eletion", new TimeModel(nums)));
            if (e != null) return ResponseEntity.ok(newHoa);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create a board election");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


    /**
     * Endpoint for getting all HOAs in the database.
     *
     * @return all HOAs in the database
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Hoa>> getAll() {
        try {
            return ResponseEntity.ok(hoaService.getAllHoa());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for getting one HOA from the database.
     *
     * @param id the id of the HOA to be retrieved
     * @return the HOA
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<Hoa> getById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(hoaService.getHoaById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets all requirements of an HOA
     *
     * @param hoaId id of HOA to fetch requirements from
     * @return List of requirements of an HOA, if it exists
     */
    @GetMapping("/getRequirements/{hoaId}")
    public ResponseEntity<List<Requirement>> getRequirements(@PathVariable long hoaId) {
        try {
            if (hoaRepo.findById(hoaId).isEmpty())
                throw new HoaDoesntExistException("Hoa with provided id does not exist");
            return ResponseEntity.ok(requirementService.getHoaRequirements(hoaId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Adds a requirement to an HOA
     *
     * @param hoaId  id of HOA to add a requirement to
     * @param prompt String to represent the requirement added
     * @return Added requirement object
     */
    @PostMapping("/addRequirement/{id}")
    public ResponseEntity<Requirement> addRequirement(@PathVariable long hoaId,
                                                      @RequestBody String prompt) {
        try {
            Requirement req = requirementService.addHoaRequirement(hoaId, prompt);
            return ResponseEntity.ok(req);
        } catch (RequirementAlreadyPresent | HoaDoesntExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Removes a requirement from an HOA
     *
     * @param reqId ID of requirement to remove
     * @return Removed requirement, if one with the provided id exists
     */
    @PostMapping("/removeRequirement/{id}")
    public ResponseEntity<Requirement> removeRequirements(@PathVariable long reqId) {
        try {
            Requirement req = requirementService.removeHoaRequirement(reqId);
            return ResponseEntity.ok(req);
        } catch (RequirementDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/report/{memberId}/{reqId}")
    public ResponseEntity<Boolean> reportUser(@PathVariable String memberId, @PathVariable long reqId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            List<MembershipResponseModel> memberships =
                    MembershipUtils.getActiveMembershipsForUser(memberId, token);
            Requirement req = requirementService.getHoaRequirement(reqId);
            if (memberships.stream().noneMatch(m -> m.getHoaId() == req.getHoaId()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access is not allowed");
            hoaService.report(memberId, reqId);
            return ResponseEntity.ok(true);
        } catch (RequirementDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}

