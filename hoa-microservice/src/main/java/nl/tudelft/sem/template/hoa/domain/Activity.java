package nl.tudelft.sem.template.hoa.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

/**
 * DDD entity corresponding to an activity on the public board.
 */
@Entity
@Table(name = "Activities")
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "activityName", nullable = false)
    private String activityName;

    @Column(name = "activityDescription", nullable = false)
    private String activityDescription;

    @Column(name = "hoaId", nullable = false)
    private Long hoaId;

    @Column(name = "activityTime", nullable = false)
    private LocalDateTime activityTime;

    @Column(name = "activityDuration", nullable = false)
    private LocalTime activityDuration;

    @Column(name = "participants", nullable = false)
    @Convert(converter = ParticipantsConverter.class)
    private List<Long> participants;

    /**
     * Constructor for activity.
     *
     * @param activityName        activity name
     * @param activityDescription description
     * @param activityTime        start time of the activity
     * @param activityDuration    activity duration
     */
    public Activity(Long hoaId, String activityName, String activityDescription,
                    LocalDateTime activityTime, LocalTime activityDuration) {
        this.hoaId = hoaId;
        this.activityName = activityName;
        this.activityDescription = activityDescription;
        this.activityTime = activityTime;
        this.activityDuration = activityDuration;
        this.participants = new ArrayList<>();
    }

    /**
     * Retrieve the name of the activity.
     *
     * @return the activity name
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * Retrieve the description of the activity.
     *
     * @return the activity description
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * Retrieve the time of the activity.
     *
     * @return the time
     */
    public LocalDateTime getActivityTime() {
        return activityTime;
    }

    /**
     * Retrieve the duration of the activity.
     *
     * @return the duration
     */
    public LocalTime getActivityDuration() {
        return activityDuration;
    }

    /**
     * Method to join an activity.
     *
     * @param memberId the memberId of the member that joins.
     */
    public void joinActivity(long memberId) {
        participants.add(memberId);
    }

    /**
     * The id of the activity.
     *
     * @return the id.
     */
    public long getId() {
        return id;
    }

    /**
     * The id of the Hoa this activity belongs to.
     *
     * @return the hoaId
     */
    public Long getHoaId() {
        return hoaId;
    }

    /**
     * Retrieves the list of participants.
     *
     * @return the list of participants.
     */
    public List<Long> getParticipants() {
        return participants;
    }

    /**
     * Method for a participant to leave an activity.
     *
     * @param memberId the memberId of the member that leaves.
     */
    public void leaveActivity(long memberId) {
        participants.remove(memberId);
    }

    /**
     * Method that checks if an activity has expired.
     *
     * @return whether the activity has expired or not
     */
    public boolean isExpired() {
        return activityTime.isBefore(LocalDateTime.now());
    }

    /**
     * Equals method for activity class.
     *
     * @param o an object
     * @return true if this is equal to o, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Activity)) {
            return false;
        }
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    /**
     * Returns a hash of this activity.
     *
     * @return an integer, corresponding to the has.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, activityName, activityDescription, hoaId, activityTime, activityDuration, participants);
    }
}
