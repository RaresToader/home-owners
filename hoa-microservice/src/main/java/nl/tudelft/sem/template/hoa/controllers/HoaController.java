package nl.tudelft.sem.template.hoa.controllers;

import java.util.List;

import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The controller for the association.
 */
@RestController
public class HoaController {

    private transient HoaService hoaService;
    private transient RequirementService requirementService;
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
    @PostMapping("/hoa/create")
    public ResponseEntity<Hoa> register(@RequestBody HoaRequestModel request) {
        try {
            Hoa newHoa = this.hoaService.registerHoa(request);
            return ResponseEntity.ok(newHoa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Endpoint for getting all HOAs in the database.
     *
     * @return all HOAs in the database
     */
    @GetMapping("/hoa/getAll")
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
    @GetMapping("/hoa/getById/{id}")
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
     * @param id id of HOA to fetch requirements from
     * @return List of requirements of an HOA, if it exists
     */
    @GetMapping("/hoa/getRequirements/{id}")
    public ResponseEntity<List<Requirement>> getRequirements(@PathVariable long id) {
        try {
            if (hoaRepo.findById(id).isEmpty())
                throw new HoaDoesntExistException("Hoa with provided id does not exist");
            return ResponseEntity.ok(requirementService.getHoaRequirements(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Adds a requirement to an HOA
     *
     * @param id  id of HOA to add a requirement to
     * @param prompt String to represent the requirement added
     * @return Added requirement object
     */
    @PostMapping("/hoa/addRequirement/{id}")
    public ResponseEntity<Requirement> addRequirement(@PathVariable long id,
                                                      @RequestBody Object prompt) {
        try {
            Requirement req = requirementService.addHoaRequirement(id, String.valueOf(prompt));
            return ResponseEntity.ok(req);
        } catch (RequirementAlreadyPresent | HoaDoesntExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Removes a requirement from an HOA
     *
     * @param id ID of requirement to remove
     * @return Removed requirement, if one with the provided id exists
     */
    @PostMapping("/hoa/removeRequirement/{id}")
    public ResponseEntity<Requirement> removeRequirements(@PathVariable long id) {
        try {
            Requirement req = requirementService.removeHoaRequirement(id);
            return ResponseEntity.ok(req);
        } catch (RequirementDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /** Setter method used when HoaService needs to be mocked
     * @param h - HoaService to be mocked
     */
    public void setHoaService(HoaService h) {
        this.hoaService = h;
    }

    /** Setter method used when RequirementService needs to be mocked
     * @param h - RequirementService to be mocked
     */
    public void setRequirementService(RequirementService h) {
        this.requirementService = h;
    }
}

