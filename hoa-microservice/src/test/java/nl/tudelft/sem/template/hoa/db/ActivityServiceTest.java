package nl.tudelft.sem.template.hoa.db;


import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.BadActivityException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestSuite(testType = INTEGRATION)
class ActivityServiceTest {

    @Autowired
    private transient ActivityRepo actualRepo;

    @Mock
    private transient ActivityRepo activityRepo;

    private transient HoaService hoaService;

    private transient ActivityService activityService;

    private static MockedStatic<MembershipUtils> membershipUtils;

    private static final String test = "Test";

    private static final String spaces = "   ";

    private final transient Activity activity = new Activity(1L, "activity 1",
            "description 1",
            LocalDateTime.of(2025, 12, 12, 5, 0, 0),
            LocalTime.of(2, 0, 0));

    @BeforeAll
    static void registerMocks() {
        membershipUtils = mockStatic(MembershipUtils.class);
        when(MembershipUtils.getMembershipById(1L))
                .thenReturn(new MembershipResponseModel(1L, "test user",
                        1L, "country", "city", false, LocalDateTime.now(), LocalDateTime.now()));
        when(MembershipUtils.getMembershipById(2L))
                .thenReturn(new MembershipResponseModel(2L, "test user",
                        3L, "country", "city", false, LocalDateTime.now(), LocalDateTime.now()));

    }

    @AfterAll
    static void deregisterMocks() {
        membershipUtils.close();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityService = new ActivityService(activityRepo);
    }

    @Test
    void getActivityByIdTest() throws ActivityDoesntExistException {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        assertEquals(activity, activityService.getActivityById(1));
    }

    @Test
    void getActivityById_notFoundTest() {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ActivityDoesntExistException.class, () -> activityService.getActivityById(2L));
    }

    @Test
    void getActivitiesByHoaIdTest() {
        when(activityRepo.findByHoaId(anyLong())).thenReturn(Optional.of(List.of(activity)));
        assertEquals(List.of(activity), activityService.getActivitiesByHoaId(1));
    }

    @Test
    void joinActivity() throws ActivityDoesntExistException {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        Activity updatedActivity = activityService.joinActivity(1, 1);
        assertEquals(activity, updatedActivity);
        assertEquals(1, activity.getParticipants().size());
        assertTrue(activity.getParticipants().contains(1L));
    }

    @Test
    void joinActivityFalse() {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        Assertions.assertThrows(IllegalArgumentException.class, () -> activityService.joinActivity(2L, 1L));
    }


    @Test
    void leaveActivity() throws ActivityDoesntExistException {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        Activity updatedActivity = activityService.joinActivity(1, 1);
        assertEquals(activity, updatedActivity);
        assertEquals(1, activity.getParticipants().size());
        assertTrue(activity.getParticipants().contains(1L));
        updatedActivity = activityService.leaveActivity(1, 1);
        assertEquals(0, activity.getParticipants().size());
        assertFalse(activity.getParticipants().contains(1L));
        assertEquals(0, activity.getParticipants().size());
        assertFalse(activity.getParticipants().contains(1L));
    }

    @Test
    void leaveActivityFalse() {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        Assertions.assertThrows(IllegalArgumentException.class, () -> activityService.leaveActivity(2L, 1L));
    }

    @Test
    void updateAndRetrieveActivities() {
        Activity expiredActivity = new Activity(1L, "activity 1", "description 1",
                LocalDateTime.of(2018, 12, 12, 5, 0, 0),
                LocalTime.of(2, 0, 0));

        actualRepo.save(activity);
        actualRepo.save(expiredActivity);

        ActivityService actualService = new ActivityService(actualRepo);

        assertEquals(List.of(activity), actualService.updateAndRetrieveActivities(1L));
    }

    @Test
    void createActivity() throws HoaDoesntExistException, BadActivityException {
        HoaService hoaService = mock(HoaService.class);
        when(hoaService.findHoaById(any(Long.class))).thenReturn(true);

        LocalDateTime activityTime = LocalDateTime.of(2025, 12, 12, 5, 0, 0);
        LocalTime activityDuration = LocalTime.of(2, 0, 0);
        ActivityRequestModel requestModel = new ActivityRequestModel("activity 1", "description 1",
                1L, activityTime, activityDuration);

        assertEquals(activity, activityService.createActivity(requestModel, hoaService, 1L));
        verify(activityRepo).save(activity);
    }

    @Test
    void addActivity() {
        activityService.addActivity(activity);
        verify(activityRepo).save(activity);
    }

    @Test
    void deleteActivity() {
        activityService.deleteActivity(activity);
        verify(activityRepo).delete(activity);
    }

    @Test
    void isInThisHoa() {
        assertTrue(activityService.isInThisHoa(1L, 1L));
        assertFalse(activityService.isInThisHoa(1L, 2L));
    }

    @Test
    void addActivityNullName() {
        assertThrows(BadActivityException.class, () ->
                activityService.createActivity(new ActivityRequestModel(null, test,
                        1L, LocalDateTime.now().plusDays(1L),
                        LocalTime.of(2, 10)), hoaService, 1L));
    }

    @Test
    void addActivityNullDescription() {
        assertThrows(BadActivityException.class, () -> activityService.createActivity(new ActivityRequestModel(
                test, null, 1L, LocalDateTime.now().plusDays(1L),
                LocalTime.of(2, 10)), hoaService, 1L));
    }

    @Test
    void addActivityEmptyName() {
        assertThrows(BadActivityException.class, () -> activityService.createActivity(new ActivityRequestModel("",
                test, 1L, LocalDateTime.now().plusDays(1L),
                LocalTime.of(2, 10)), hoaService, 1L));
    }

    @Test
    void addActivityEmptyDescription() {
        assertThrows(BadActivityException.class, () -> activityService.createActivity(new ActivityRequestModel(
                test, "", 1L, LocalDateTime.now().plusDays(1L),
                LocalTime.of(2, 10)), hoaService, 1L));
    }

    @Test
    void addActivityBlankName() {
        assertThrows(BadActivityException.class, () -> activityService.createActivity(new ActivityRequestModel(
                spaces, test, 1L, LocalDateTime.now().plusDays(1L),
                LocalTime.of(2, 10)), hoaService, 1L));
    }

    @Test
    void addActivityBlankDescription() {
        assertThrows(BadActivityException.class, () -> activityService.createActivity(new ActivityRequestModel(
                test, spaces, 1L, LocalDateTime.now().plusDays(1L),
                LocalTime.of(2, 10)), hoaService, 1L));
    }

    @Test
    void rightFormatTitleNull() {
        assertFalse(activityService.rightFormatTitle(null));
    }

    @Test
    void rightFormatTitleEmpty() {
        assertFalse(activityService.rightFormatTitle(""));
    }

    @Test
    void rightFormatTitleBlank() {
        assertFalse(activityService.rightFormatTitle("     "));
    }

    @Test
    void rightFormatTitleHappy() {
        assertTrue(activityService.rightFormatTitle("T"));
        assertTrue(activityService.rightFormatTitle("Test123"));
        assertTrue(activityService.rightFormatTitle("Test123456789test1234"));
    }

    @Test
    void rightFormatTitleBad() {
        assertFalse(activityService.rightFormatTitle("T".repeat(50)));
        assertFalse(activityService.rightFormatTitle("Test123".repeat(40)));
        assertFalse(activityService.rightFormatTitle("Test123456789test1234".repeat(30)));
    }

    @Test
    void rightFormatDescriptionNull() {
        assertFalse(activityService.rightFormatDescription(null));
    }

    @Test
    void rightFormatDescriptionEmpty() {
        assertFalse(activityService.rightFormatDescription(""));
    }

    @Test
    void rightFormatDescriptionBlank() {
        assertFalse(activityService.rightFormatDescription("     "));
    }

    @Test
    void rightFormatDescriptionHappy() {
        assertTrue(activityService.rightFormatDescription("Test123"));
    }

    @Test
    void validateNullName() {
        ActivityRequestModel model = new ActivityRequestModel(null,
                test, 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertFalse(activityService.validateActivity(model, LocalDateTime.now()));
    }

    @Test
    void validateNullDescription() {
        ActivityRequestModel model = new ActivityRequestModel(
                test, null, 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertFalse(activityService.validateActivity(model, LocalDateTime.now()));
    }

    @Test
    void validateEmptyName() {
        ActivityRequestModel model = new ActivityRequestModel("",
                test, 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertFalse(activityService.validateActivity(model, LocalDateTime.now()));
    }

    @Test
    void validateEmptyDescription() {
        ActivityRequestModel model = new ActivityRequestModel(
                test, "", 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertFalse(activityService.validateActivity(model, LocalDateTime.now()));
    }

    @Test
    void validateBlankName() {
        ActivityRequestModel model = new ActivityRequestModel(spaces,
                test, 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertFalse(activityService.validateActivity(model, LocalDateTime.now()));
    }

    @Test
    void validateBlankDescription() {
        ActivityRequestModel model = new ActivityRequestModel(
                test, spaces, 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertFalse(activityService.validateActivity(model, LocalDateTime.now()));
    }

    @Test
    void validateHappyCase() {
        ActivityRequestModel model = new ActivityRequestModel(
                test, test, 1L, LocalDateTime.now().plusDays(1L), LocalTime.of(2, 10));
        assertTrue(activityService.validateActivity(model, LocalDateTime.now()));
    }


    @Test
    public void testNullInput() {
        assertFalse(activityService.rightFormatDescription(null));
    }

    @Test
    public void testBlankInput() {
        assertFalse(activityService.rightFormatDescription(""));
    }

    @Test
    public void testEmptyInput() {
        assertFalse(activityService.rightFormatDescription(" "));
    }

    @Test
    public void testInputWithinLimit() {
        assertTrue(activityService.rightFormatDescription("a"));
        assertTrue(activityService.rightFormatDescription("abcdefghijklmnopqrstuvwxyz"));
        assertTrue(activityService.rightFormatDescription(
                "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"));
    }

    @Test
    public void testInputExceedingLimit() {
        assertFalse(activityService.rightFormatDescription("a".repeat(300)));
    }


}
