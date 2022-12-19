package nl.tudelft.sem.template.hoa.db;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.BadActivityException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
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
     * Method that adds a user to an activity, if that user is in the hoa that hosts the activity.
     *
     * @param membershipId the membership id of the user
     * @param activityId   the activity id
     * @return the activity where the user is added
     * @throws ActivityDoesntExistException is thrown if the activity does not exist
     */
    public Activity joinActivity(long membershipId, long activityId) throws ActivityDoesntExistException {
        Activity activity = this.getActivityById(activityId);
        MembershipResponseModel model = MembershipUtils.getMembershipById(membershipId);
        if (activity.getHoaId() != model.getHoaId()) {
            throw new IllegalArgumentException("Member is not eligible to join this!");
        } else {
            activity.joinActivity(membershipId);
            this.addActivity(activity);
            return activity;
        }

    }

    /**
     * Method that enables a member to leave an activity, if that user is in the hoa that hosts the activity.
     *
     * @param membershipId the membership id
     * @param activityId   the id of the activity
     * @return the activity left
     * @throws ActivityDoesntExistException thrown if the activity does not exist
     */
    public Activity leaveActivity(long membershipId, long activityId) throws ActivityDoesntExistException {
        Activity activity = this.getActivityById(activityId);
        MembershipResponseModel model = MembershipUtils.getMembershipById(membershipId);
        if (activity.getHoaId() != model.getHoaId()) {
            throw new IllegalArgumentException("Member is not eligible to join this!");
        } else {
            activity.leaveActivity(membershipId);
            this.addActivity(activity);
            return activity;
        }

    }

    /**
     * Return true if the membershipId exists and its hoaId is the hoaId provided.
     *
     * @param membershipId the membership id
     * @param hoaId        the hoaId
     * @return true if the membership is for the hoaId provided
     */
    public boolean isInThisHoa(long membershipId, long hoaId) {
        try {
            MembershipResponseModel membership = MembershipUtils.getMembershipById(membershipId);
            return membership.getHoaId() == hoaId;
        } catch (Exception e) {
            return false;
        }
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
    public Activity createActivity(ActivityRequestModel activityRequestModel, HoaService hoaService, long membershipId)
            throws HoaDoesntExistException, BadActivityException {
        if (!validateActivity(activityRequestModel, LocalDateTime.now())) {
            throw new BadActivityException("Bad format for name or description. "
                    + "Activity start time needs to be in the future!");
        }
        if (hoaService.findHoaById(activityRequestModel.getHoaId())) {
            Activity activity = new Activity(activityRequestModel.getHoaId(), activityRequestModel.getActivityName(),
                    activityRequestModel.getActivityDescription(), activityRequestModel.getActivityTime(),
                    activityRequestModel.getActivityDuration());
            MembershipResponseModel model = MembershipUtils.getMembershipById(membershipId);
            if (activity.getHoaId() != model.getHoaId()) {
                throw new IllegalArgumentException("Not in the HOA!");
            }
            this.addActivity(activity);
            return activity;
        }
        throw new HoaDoesntExistException("Hoa with id " + activityRequestModel.getHoaId() + " doesn't exist");
    }

    /**
     * Helper method that validates an activity request model.
     * The activity can be added iff the name and description have the right format.
     * The activity can be added iff the starting time is in the future.
     * Thus, it should be impossible to add an activity that starts in the past.
     *
     * @param model the request model
     * @param now the time
     * @return true if the activity can be added, false otherwise
     */
    public boolean validateActivity(ActivityRequestModel model, LocalDateTime now) {
        String name = model.getActivityName();
        String description = model.getActivityDescription();
        if (!rightFormat(name) || !rightFormat(description)) {
            return false;
        }
        LocalDateTime startTime = model.getActivityTime();
        return now.isBefore(startTime);
    }

    /**
     * Helper method to be used to determine if the name and description of an activity is valid.
     * The name can have at most 100 characters.
     *
     * @param name the string
     * @return true, if the name has the right format, false otherwise;
     */
    public boolean rightFormat(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            return false;
        }
        return name.length() <= 100;
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
