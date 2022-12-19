package nl.tudelft.sem.template.hoa.controllers;

import java.util.List;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller for the association.
 */
@RestController
public class HoaController {

    private final transient HoaService hoaService;
    private final transient RequirementService requirementService;


    /**
     * Constructor for the HoaController.
     *
     * @param hoaService         the hoa service
     * @param requirementService the requirement service
     */
    @Autowired
    public HoaController(HoaService hoaService, RequirementService requirementService) {
        this.hoaService = hoaService;
        this.requirementService = requirementService;
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
}

