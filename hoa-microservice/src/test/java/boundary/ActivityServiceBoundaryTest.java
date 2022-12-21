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
    private transient ActivityRepo activityRepo;

    private transient ActivityService activityService;

    private final transient String test = "Test";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityService = new ActivityService(activityRepo);
    }

    @Test
    public void rightFormatTitleOnPoint() {
        String string = "a".repeat(40);
        Assertions.assertTrue(activityService.rightFormatTitle(string));
    }

    @Test
    public void rightFormatTittleOffPoint() {
        String string = "a".repeat(41);
        Assertions.assertFalse(activityService.rightFormatTitle(string));
    }

    @Test
    public void rightFormatDescriptionOnPoint() {
        String string = "a".repeat(200);
        Assertions.assertTrue(activityService.rightFormatDescription(string));
    }

    @Test
    public void rightFormatDescriptionOffPoint() {
        String string = "a".repeat(201);
        Assertions.assertFalse(activityService.rightFormatDescription(string));
    }


    @Test
    public void validateActivityOffPoint() {
        LocalDateTime time = LocalDateTime.now();
        ActivityRequestModel model = new ActivityRequestModel(test, test, 1L, time,
                LocalTime.of(2, 10));
        Assertions.assertFalse(activityService.validateActivity(model, time));
    }

    @Test
    public void validateActivityOnPoint() {
        LocalDateTime time = LocalDateTime.now();
        ActivityRequestModel model = new ActivityRequestModel(test, test, 1L, time,
                LocalTime.of(2, 10));
        Assertions.assertTrue(activityService.validateActivity(model, time.minusNanos(1L)));
    }


}
