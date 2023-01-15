package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * The controller related to requirements.
 */
@RestController
@RequestMapping("/requirement")
public class RequirementController {
    private transient RequirementService requirementService;
    private final transient HoaRepo hoaRepo;

    private final transient HoaService hoaService;


    /**
     * Constructor for the requirement controller
     *
     * @param requirementService the requirement service
     * @param hoaRepo the hoaRepo
     * @param hoaService the hoaService
     */
    public RequirementController(RequirementService requirementService, HoaRepo hoaRepo, HoaService hoaService) {
        this.requirementService = requirementService;
        this.hoaRepo = hoaRepo;
        this.hoaService = hoaService;
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
    @PostMapping("/addRequirement/{hoaId}")
    public ResponseEntity<Requirement> addRequirement(@PathVariable long hoaId,
                                                      @RequestBody Object prompt) {
        try {
            Requirement req = requirementService.addHoaRequirement(hoaId, String.valueOf(prompt));
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
    @PostMapping("/removeRequirement/{reqId}")
    public ResponseEntity<Requirement> removeRequirements(@PathVariable long reqId) {
        try {
            Requirement req = requirementService.removeHoaRequirement(reqId);
            return ResponseEntity.ok(req);
        } catch (RequirementDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /** Setter method used when RequirementService needs to be mocked
     * @param h - RequirementService to be mocked
     */
    public void setRequirementService(RequirementService h) {
        this.requirementService = h;
    }

    /**
     * Endpoint for reporting a member of an HOA for violating a rule/requirement
     *
     * @param memberId id of member to report
     * @param reqId    id of requirement that was broken
     * @param token    Authorization token used for validation
     * @return ResponseEntity that contains the operation's success
     */
    @PostMapping("/report/{memberId}/{reqId}")
    public ResponseEntity<Boolean> reportUser(@PathVariable String memberId, @PathVariable long reqId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        List<MembershipResponseModel> memberships =
                MembershipUtils.getActiveMembershipsForUser(memberId, token);
        if (memberships.stream().noneMatch(m -> {
            try {
                //PMD, this is silly :)
                return m.getHoaId() == requirementService.getHoaRequirement(reqId).getHoaId();
            } catch (RequirementDoesNotExist e) {
                throw new RuntimeException(e);
            }
        }))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access is not allowed");
        hoaService.report(memberId, reqId);
        return ResponseEntity.ok(true);
    }
}
