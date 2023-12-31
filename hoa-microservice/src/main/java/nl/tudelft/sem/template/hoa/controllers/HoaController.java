package nl.tudelft.sem.template.hoa.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
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

    private transient HoaService hoaService;


    /**
     * Constructor for the HoaController.
     *
     * @param hoaService the hoa service
     */
    @Autowired
    public HoaController(HoaService hoaService) {
        this.hoaService = hoaService;
    }

    /**
     * Endpoint for creating an association.
     *
     * @param request             the possibly new Hoa
     * @param scheduledAfterXDays Optional int for automatic board election creation that is scheduled for X days
     * @return 200 OK if the registration is successful
     */
    @PostMapping(value = {"/create", "/create/{scheduledAfterXDays}"})
    public ResponseEntity<Hoa> register(@RequestBody HoaRequestModel request,
                                        @PathVariable Optional<Integer> scheduledAfterXDays) {
        try {
            Hoa newHoa = this.hoaService.registerHoa(request);
            if (scheduledAfterXDays.isEmpty() || scheduledAfterXDays.get() < 0) return ResponseEntity.ok(newHoa);
            Integer[] nums = Arrays.stream(LocalDateTime.now().plusDays(scheduledAfterXDays.get())
                    .format(DateTimeFormatter.ISO_DATE_TIME)
                    .split("\\D+")).map(Integer::parseInt).toArray(Integer[]::new);
            // start automatic annual board election
            Object e = ElectionUtils.createBoardElection(new BoardElectionRequestModel(newHoa.getId(),
                    2, List.of(), "Annual board election",
                    "This is the auto-generated annual board election",
                    TimeModel.createModelFromArr(nums)));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch HOA", e);
        }
    }

    /**
     * Endpoint for notifying users of accepted proposals (rule changes)
     *
     * @param memberId id of member to notify
     * @param hoaId    id of hoa that sends the notification
     * @param token    Authorization token used for validation
     * @return List of unread/new notifications pertaining to the member in the given hoa, if new exist
     */
    @GetMapping("/getNotifications/{memberId}/{hoaId}")
    public ResponseEntity<List<String>> getNotifications(@PathVariable String memberId,
                                                         @PathVariable long hoaId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            List<MembershipResponseModel> memberships = MembershipUtils.getActiveMembershipsForUser(memberId, token);
            if (memberships.stream().noneMatch(m -> m.getHoaId() == hoaId))
                throw new IllegalAccessException("Access is not allowed");
            List<String> res = hoaService.clearNotifications(hoaId, memberId);
            return ResponseEntity.ok(res);
        } catch (HoaDoesntExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        }
    }

    /**
     * Setter method used when HoaService needs to be mocked
     *
     * @param h - HoaService to be mocked
     */
    public void setHoaService(HoaService h) {
        this.hoaService = h;
    }

}

