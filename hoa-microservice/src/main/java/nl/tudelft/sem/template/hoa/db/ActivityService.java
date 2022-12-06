package nl.tudelft.sem.template.hoa.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import org.springframework.stereotype.Service;

/**
 * A DDD service for activity-related queries.
 */
@Service
public class ActivityService {

    private final transient ActivityRepo activityRepo;

    /**
     * Constructor for the activity service.
     *
     * @param activityRepo the activity repository
     */
    public ActivityService(ActivityRepo activityRepo) {
        this.activityRepo = activityRepo;
    }

    /**
     * Query to retrieve an activity by its id.
     *
     * @param activityId the id of the queried activity
     * @return the activity, if found
     * @throws ActivityDoesntExistException if no activity with the specified id exists
     */
    public Activity getActivityById(long activityId) throws ActivityDoesntExistException {
        Optional<Activity> activity = activityRepo.findById(activityId);
        if (activity.isEmpty()) {
            throw new ActivityDoesntExistException("Activity with id " + activityId + " doesn't exist.");
        }
        return activity.get();
    }

    /**
     * Query to get all activities of a hoa id.
     *
     * @param hoaId the id of the hoa
     * @return the list of activities corresponding to the public board
     */
    public List<Activity> getActivitiesByHoaId(long hoaId) {
        Optional<List<Activity>> list = activityRepo.findByHoaId(hoaId);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        return list.get();
    }

    /**
     * Query to delete an activity.
     *
     * @param activity the activity to be deleted.
     */
    public void deleteActivity(Activity activity) {
        activityRepo.delete(activity);
    }

    /**
     * Query to add an activity.
     *
     * @param activity the activity to be added.
     */
    public void addActivity(Activity activity) {
        activityRepo.save(activity);
    }
}
