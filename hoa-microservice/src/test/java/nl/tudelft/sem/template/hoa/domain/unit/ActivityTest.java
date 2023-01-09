package nl.tudelft.sem.template.hoa.domain.unit;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.domain.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@TestSuite(testType = UNIT)
public class ActivityTest {

    transient Long hoaId;
    transient String activityName;
    transient String activityDescription;
    transient LocalDateTime activityTime;
    transient LocalTime activityDuration;
    transient Activity activity;

    @BeforeEach
    void setUp() {
        hoaId = 1L;
        activityName = "BBQ";
        activityDescription = "Annual neighborhood BBQ";
        activityTime = LocalDateTime.now().plusYears(1);
        activityDuration = LocalTime.of(2, 0);
        activity = new Activity(hoaId, activityName, activityDescription, activityTime, activityDuration);
    }

    @Test
    public void activityConstructorTest() {
        assertNotNull(activity);
        assertEquals(hoaId, activity.getHoaId());
        assertEquals(activityName, activity.getActivityName());
        assertEquals(activityDescription, activity.getActivityDescription());
        assertEquals(activityTime, activity.getActivityTime());
        assertEquals(activityDuration, activity.getActivityDuration());
        assertTrue(activity.getParticipants().isEmpty());
    }

    @Test
    public void joinActivityTest() {
        long memberId = 12345L;
        activity.joinActivity(memberId);

        assertEquals(1, activity.getParticipants().size());
        assertTrue(activity.getParticipants().contains(memberId));

        long memberId1 = 678L;
        activity.joinActivity(memberId1);

        assertEquals(2, activity.getParticipants().size());
        assertTrue(activity.getParticipants().contains(memberId1));
    }

    @Test
    public void leaveActivityTest() {
        long memberId = 12345L;
        activity.joinActivity(memberId);
        assertEquals(1, activity.getParticipants().size());
        assertTrue(activity.getParticipants().contains(memberId));

        activity.leaveActivity(memberId);
        assertEquals(0, activity.getParticipants().size());
        assertFalse(activity.getParticipants().contains(memberId));

        //Member id which is not participating in the activity
        long memberId1 = 678L;
        activity.leaveActivity(memberId1);
        assertEquals(0, activity.getParticipants().size());
        assertFalse(activity.getParticipants().contains(memberId1));
    }

    @Test
    public void isExpiredTest() {
        LocalDateTime activityTimeExpired = LocalDateTime.of(2022, 6, 25, 12, 0);
        Activity activityExpired = new Activity(hoaId, activityName, activityDescription,
                activityTimeExpired, activityDuration);

        assertFalse(activity.isExpired());
        assertTrue(activityExpired.isExpired());
    }

    @Test
    public void equalsTest() {
        assertNotEquals(activity, null);
        assertEquals(activity, activity);
    }
}
