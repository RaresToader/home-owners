package nl.tudelft.sem.template.hoa.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
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
        return list.orElseGet(ArrayList::new);
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
     * Method that adds a user to an activity.
     *
     * @param membershipId the membership id of the user
     * @param activityId   the activity id
     * @return the activity where the user is added
     * @throws ActivityDoesntExistException is thrown if the activity does not exist
     */
    public Activity joinActivity(long membershipId, long activityId) throws ActivityDoesntExistException {
        Activity activity = this.getActivityById(activityId);
        activity.joinActivity(membershipId);
        this.addActivity(activity);
        return activity;
    }

    /**
     * Method that enables a member to leave an activity.
     *
     * @param membershipId the membership id
     * @param activityId   the id of the activity
     * @return the activity left
     * @throws ActivityDoesntExistException thrown if the activity does not exist
     */
    public Activity leaveActivity(long membershipId, long activityId) throws ActivityDoesntExistException {
        Activity activity = this.getActivityById(activityId);
        activity.leaveActivity(membershipId);
        this.addActivity(activity);
        return activity;
    }

    /**
     * Updates and retrieves ongoing activities.
     *
     * @param hoaId the hoaId
     * @return the list of activities
     */
    public List<Activity> updateAndRetrieveActivities(long hoaId) {
        List<Activity> activities = this.getActivitiesByHoaId(hoaId);
        for (Activity activity : activities) {
            if (activity.isExpired()) {
                this.deleteActivity(activity);
            }
        }
        activities = this.getActivitiesByHoaId(hoaId);
        return activities;
    }

    /**
     * Method to create an activity.
     *
     * @param activityRequestModel the activity request model
     * @param hoaService           the hoaService
     * @return the activity created
     * @throws HoaDoesntExistException thrown if Hoa does not exist
     */
    public Activity createActivity(ActivityRequestModel activityRequestModel, HoaService hoaService)
            throws HoaDoesntExistException {
        if (hoaService.findHoaById(activityRequestModel.getHoaId())) {
            Activity activity = new Activity(activityRequestModel.getHoaId(), activityRequestModel.getActivityName(),
                    activityRequestModel.getActivityDescription(), activityRequestModel.getActivityTime(),
                    activityRequestModel.getActivityDuration());
            this.addActivity(activity);
            return activity;
        }
        throw new HoaDoesntExistException("Hoa with id " + activityRequestModel.getHoaId() + " doesn't exist");
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
