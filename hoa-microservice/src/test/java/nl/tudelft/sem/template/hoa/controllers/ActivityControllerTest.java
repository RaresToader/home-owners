package nl.tudelft.sem.template.hoa.controllers;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.db.ActivityRepo;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.JsonUtil;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestSuite(testType = INTEGRATION)
class ActivityControllerTest {

    private final transient String activityCreate = "/activity/create/";

    private final transient String testActivity = "This is a test activity";
    @Autowired
    private transient MockMvc mockMvc;
    @Autowired
    private transient ActivityRepo activityRepo;
    @Autowired
    private transient HoaRepo hoaRepo;

    private static MockedStatic<MembershipUtils> membershipUtils;

    @BeforeAll
    static void registerMocks() {
        membershipUtils = mockStatic(MembershipUtils.class);
        when(MembershipUtils.getMembershipById(1L))
                .thenReturn(new MembershipResponseModel(1L, "test user", 1L,
                    "country", "city", false, LocalDateTime.now(), null));
        when(MembershipUtils.getMembershipById(2L))
                .thenReturn(new MembershipResponseModel(2L, "test user 2", 2L,
                        "country 2", "city 2", false,
                        LocalDateTime.now(), null));
    }

    @AfterAll
    static void deregisterMocks() {
        membershipUtils.close();
    }

    @BeforeEach
    void setUp() {
        Hoa hoa = Hoa.createHoa("Germany", "Berlin", "Coolest");
        hoaRepo.save(hoa);
    }

    @Test
    public void createActivityTest() throws Exception {

        LocalDateTime activityTime = LocalDateTime.of(2030, 12, 15, 5, 30, 15);
        LocalTime activityDuration = LocalTime.of(2, 30, 30);

        // Set up the ActivityRequestModel to be provided in the request body
        ActivityRequestModel requestModel = new ActivityRequestModel("Test Activity",
                testActivity, 1L, activityTime, activityDuration);

        // Set up the expected Activity object that the ActivityService should be called with
        Activity expectedActivity = new Activity(1L, "Test Activity",
                testActivity, activityTime, activityDuration);

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(post(activityCreate + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk()); // Assert that the response has a 200 OK status

        Activity responseActivity = activityRepo.findById(2L).orElseThrow();

        // Assert that the response body contains the expected activity object
        assertEquals(expectedActivity, responseActivity);
    }

    @Test
    public void createActivityTest_BadCaseNotFound() throws Exception {

        LocalDateTime activityTime = LocalDateTime.of(2030, 12, 15, 5, 30, 15);
        LocalTime activityDuration = LocalTime.of(2, 30, 30);

        // Set up the ActivityRequestModel to be provided in the request body
        ActivityRequestModel requestModel = new ActivityRequestModel("Test Activity", testActivity,
                1L, activityTime, activityDuration);

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(post(activityCreate + 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void createActivityTest_BadCaseBadRequest() throws Exception {

        LocalDateTime activityTime = LocalDateTime.of(2030, 12, 15, 5, 30, 15);
        LocalTime activityDuration = LocalTime.of(2, 30, 30);

        // Set up the ActivityRequestModel to be provided in the request body
        ActivityRequestModel requestModel = new ActivityRequestModel("", testActivity,
                1L, activityTime, activityDuration);

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(post(activityCreate + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isBadRequest());

        // Set up the ActivityRequestModel to be provided in the request body
        ActivityRequestModel requestModel1 = new ActivityRequestModel("", testActivity,
                3L, activityTime, activityDuration);

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions1 = mockMvc.perform(post(activityCreate + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel1)));

        resultActions1.andExpect(status().isBadRequest());
    }



    @Test
    void joinActivityTest() throws Exception {

        insertActivityInDatabase();

        long membershipId = 1L;
        long activityId = 2L;

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(put("/activity/join/" + membershipId + "/" + activityId));

        resultActions.andExpect(status().isOk()); // Assert that the response has a 200 OK status

        Activity updatedActivity = activityRepo.findById(activityId).orElseThrow();

        assertTrue(updatedActivity.getParticipants().contains(membershipId));
    }

    @Test
    void joinActivityTest_BadCase() throws Exception {

        insertActivityInDatabase();

        long membershipId = 2L;
        long activityId = 2L;

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(put("/activity/join/" + membershipId + "/" + activityId));

        resultActions.andExpect(status().isBadRequest());
    }

    private void joinActivity(long membershipId, long activityId) {
        activityRepo.findById(activityId).orElseThrow().joinActivity(membershipId);
    }

    @Test
    void leaveActivityTest() throws Exception {

        insertActivityInDatabase();

        long membershipId = 1L;
        long activityId = 2L;

        joinActivity(membershipId, activityId);

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(delete("/activity/leave/" + membershipId + "/" + activityId));

        resultActions.andExpect(status().isOk()); // Assert that the response has a 200 OK status

        Activity updatedActivity = activityRepo.findById(activityId).orElseThrow();

        assertFalse(updatedActivity.getParticipants().contains(membershipId));
    }

    @Test
    void leaveActivityTest_BadCase() throws Exception {

        insertActivityInDatabase();

        long membershipId = 2L;
        long activityId = 2L;

        joinActivity(membershipId, activityId);

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(delete("/activity/leave/" + membershipId + "/" + activityId));

        resultActions.andExpect(status().isBadRequest()); // Assert that the response has a 200 OK status
    }

    @Test
    void getPublicBoardTest() throws Exception {

        insertActivityInDatabase();

        long membershipId = 1L;
        long hoaId = 1L;

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(get("/activity/publicBoard/" + hoaId + "/" + membershipId));

        resultActions.andExpect(status().isOk()); // Assert that the response has a 200 OK status

        List<Activity> resultActivities = Arrays.asList(JsonUtil.deserialize(resultActions.andReturn()
                .getResponse().getContentAsString(), Activity[].class));
        List<Activity> actualActivities = activityRepo.findByHoaId(hoaId).orElseThrow();

        assertEquals(resultActivities, actualActivities);
    }

    @Test
    void getPublicBoardTest_BadCaseBadRequest() throws Exception {

        insertActivityInDatabase();

        long membershipId = 2L;
        long hoaId = 1L;

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(get("/activity/publicBoard/" + hoaId + "/" + membershipId));

        resultActions.andExpect(status().isBadRequest());
    }

    public boolean insertActivityInDatabase() {
        LocalDateTime activityTime = LocalDateTime.of(2025, 9, 26, 20, 30, 0);
        LocalTime activityDuration = LocalTime.of(1, 30, 0);
        Activity activity = new Activity(1L, "BBQ", "We are having a BBQ", activityTime, activityDuration);
        try {
            activityRepo.save(activity);
        } catch (IllegalAccessError e) {
            return false;
        }
        return true;
    }

}