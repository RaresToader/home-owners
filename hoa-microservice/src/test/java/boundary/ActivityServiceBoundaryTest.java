package boundary;

import java.time.LocalDateTime;
import java.time.LocalTime;
import nl.tudelft.sem.template.hoa.db.ActivityRepo;
import nl.tudelft.sem.template.hoa.db.ActivityService;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ActivityServiceBoundaryTest {

    @Mock
    private ActivityRepo activityRepo;

    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityService = new ActivityService(activityRepo);
    }

    @Test
    public void rightFormatOnPoint() {
        String string = "a".repeat(100);
        Assertions.assertTrue(activityService.rightFormat(string));
    }

    @Test
    public void rightFormatOffPoint() {
        String string = "a".repeat(101);
        Assertions.assertFalse(activityService.rightFormat(string));
    }

    @Test
    public void validateActivityOffPoint() {
        LocalDateTime time = LocalDateTime.now();
        ActivityRequestModel model = new ActivityRequestModel("Test", "Test", 1L, time, LocalTime.of(2, 10));
        Assertions.assertFalse(activityService.validateActivity(model, time));
    }

    @Test
    public void validateActivityOnPoint() {
        LocalDateTime time = LocalDateTime.now();
        ActivityRequestModel model = new ActivityRequestModel("Test", "Test", 1L, time, LocalTime.of(2, 10));
        Assertions.assertTrue(activityService.validateActivity(model, time.minusNanos(1L)));
    }


}
