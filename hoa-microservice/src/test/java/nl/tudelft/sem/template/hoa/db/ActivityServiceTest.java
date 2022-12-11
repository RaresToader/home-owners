package nl.tudelft.sem.template.hoa.db;

import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.exception.ActivityDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ActivityServiceTest {

    @Autowired
    private ActivityRepo actualRepo;

    @Mock
    private ActivityRepo activityRepo;

    private ActivityService activityService;

    private final Activity activity = new Activity(1L, "activity 1", "description 1",
            LocalDateTime.of(2025,12,12,5,0,0),
            LocalTime.of(2,0,0));

    @BeforeAll
    static void registerMocks(){
        mockStatic(MembershipUtils.class);
        when(MembershipUtils.getMembershipById(1L)).thenReturn(new MembershipResponseModel(1L,"test user",1L,false));
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        activityService = new ActivityService(activityRepo);
    }

    @Test
    void getActivityByIdTest() throws ActivityDoesntExistException {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        assertEquals(activity, activityService.getActivityById(1));
    }

    @Test
    void getActivityById_notFoundTest() throws ActivityDoesntExistException {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ActivityDoesntExistException.class,()->{
            activityService.getActivityById(2L);
        });
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
        assertTrue(activity.getParticipants().size()==1);
        assertTrue(activity.getParticipants().contains(1L));
    }

    @Test
    void leaveActivity() throws ActivityDoesntExistException {
        when(activityRepo.findById(anyLong())).thenReturn(Optional.of(activity));
        Activity updatedActivity = activityService.joinActivity(1, 1);
        assertEquals(activity, updatedActivity);
        assertTrue(activity.getParticipants().size()==1);
        assertTrue(activity.getParticipants().contains(1L));

        updatedActivity = activityService.leaveActivity(1, 1);
        assertTrue(activity.getParticipants().size()==0);
        assertTrue(!activity.getParticipants().contains(1L));
    }

    @Test
    void updateAndRetrieveActivities() {
        Activity expiredActivity = new Activity(1L, "activity 1", "description 1",
                LocalDateTime.of(2018,12,12,5,0,0),
                LocalTime.of(2,0,0));

        actualRepo.save(activity);
        actualRepo.save(expiredActivity);

        ActivityService actualService = new ActivityService(actualRepo);

        assertEquals(List.of(activity), actualService.updateAndRetrieveActivities(1L));
    }

    @Test
    void createActivity() throws HoaDoesntExistException {
        HoaService hoaService = mock(HoaService.class);
        when(hoaService.findHoaById(any(Long.class))).thenReturn(true);

        LocalDateTime activityTime = LocalDateTime.of(2025,12,12,5,0,0);
        LocalTime activityDuration = LocalTime.of(2,0,0);
        ActivityRequestModel requestModel = new ActivityRequestModel("activity 1", "description 1",1L, activityTime, activityDuration);

        assertEquals(activity,activityService.createActivity(requestModel,hoaService,1L));
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
        assertTrue(activityService.isInThisHoa(1L,1L));
        assertFalse(activityService.isInThisHoa(1L, 2L));
    }
}