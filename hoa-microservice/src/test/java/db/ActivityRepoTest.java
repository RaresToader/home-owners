package db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.db.ActivityRepo;
import nl.tudelft.sem.template.hoa.domain.Activity;
import org.junit.jupiter.api.Test;

class ActivityRepoTest {

    @Test
    void testFindById() {
        long activityId = 1;
        Activity added = new Activity(1L, "Test activity",
                "This is a test activity", LocalDateTime.now(), LocalTime.now());
        ActivityRepo activityRepo = mock(ActivityRepo.class);
        when(activityRepo.findById(activityId)).thenReturn(Optional.of(added));
        Optional<Activity> result = activityRepo.findById(activityId);
        assertTrue(result.isPresent());
        assertEquals(added, result.get());
    }


    @Test
    void testFindByHoaId() {
        long hoaId = 2;
        List<Activity> activities = new ArrayList<>();
        Activity activity1 = new Activity(2L, "Test activity", "This is a test activity",
                LocalDateTime.now(), LocalTime.now());
        activities.add(activity1);
        Activity activity2 = new Activity(2L, "Test activity",
                "This is a test activity 2", LocalDateTime.now(), LocalTime.now());
        activities.add(activity2);
        ActivityRepo activityRepo = mock(ActivityRepo.class);
        when(activityRepo.findByHoaId(hoaId)).thenReturn(Optional.of(activities));
        Optional<List<Activity>> result = activityRepo.findByHoaId(hoaId);
        assertTrue(result.isPresent());
        assertEquals(activities, result.get());
    }
}

