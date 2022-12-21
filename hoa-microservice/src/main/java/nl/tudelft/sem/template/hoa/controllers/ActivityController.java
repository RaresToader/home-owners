package nl.tudelft.sem.template.hoa.controllers;

import java.util.List;

import nl.tudelft.sem.template.hoa.db.ActivityService;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.exception.BadActivityException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class ActivityController {
    private final transient ActivityService activityService;
    private final transient HoaService hoaService;

    /**
     * Constructor to create an activity controller.
     *
     * @param activityService the activity service
     */
    @Autowired
    public ActivityController(ActivityService activityService, HoaService hoaService) {
        this.activityService = activityService;
        this.hoaService = hoaService;
    }

    /**
     * Endpoint for joining an activity, works if the member is in the hoa that hosts the activity.
     *
     * @param membershipId the membership id
     * @param activityId   the id of the activity
     * @return a response entity
     */
    @PutMapping("/activity/join/{membershipId}/{activityId}")
    public ResponseEntity<Activity> joinActivity(@PathVariable long membershipId, @PathVariable long activityId) {
        try {
            Activity activity = this.activityService.joinActivity(membershipId, activityId);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Endpoint for leaving an activity, works if the member is in the hoa that hosts the activity.
     *
     * @param membershipId the membership id
     * @param activityId   the id of the activity
     * @return a response entity
     */
    @DeleteMapping("/activity/leave/{membershipId}/{activityId}")
    public ResponseEntity<Activity> leaveActivity(@PathVariable long membershipId, @PathVariable long activityId) {
        try {
            Activity activity = this.activityService.leaveActivity(membershipId, activityId);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * Endpoint for retrieving the public board for a specific hoa, if the requesting member is in the HOA.
     *
     * @param hoaId        the hoaId
     * @param membershipId the membership id of the member requesting
     * @return a response entity with all the activities within the public board
     */
    @GetMapping("/activity/publicBoard/{hoaId}/{membershipId}")
    public ResponseEntity<List<Activity>> getPublicBoard(@PathVariable long hoaId,
                                                         @PathVariable long membershipId) {
        if (this.activityService.isInThisHoa(membershipId, hoaId)) {
            return ResponseEntity.ok(this.activityService.updateAndRetrieveActivities(hoaId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Endpoint for creating an activity for a specific HOA,
     * works if the user requesting it is in the hoa that will host the activity.
     *
     * @return a response entity with all the new activity
     */
    @PostMapping("/activity/create/{membershipId}")
    public ResponseEntity<Activity> createActivity(@RequestBody ActivityRequestModel activityRequestModel,
                                                   @PathVariable long membershipId) {
        try {
            return ResponseEntity.ok(this.activityService.createActivity(activityRequestModel, hoaService, membershipId));
        } catch (HoaDoesntExistException | BadActivityException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
