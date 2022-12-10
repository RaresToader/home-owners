package nl.tudelft.sem.template.hoa.controllers;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import nl.tudelft.sem.template.hoa.db.ActivityRepo;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.JsonUtil;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;
import java.time.LocalTime;



@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ActivityRepo activityRepo;
    @Autowired
    private HoaRepo hoaRepo;
    private Activity activity;

    @BeforeEach
    void setUp(){
        Hoa hoa = Hoa.createHoa("Germany", "Berlin","Coolest");
        hoaRepo.save(hoa);
        LocalDateTime activityTime = LocalDateTime.of(2025,9,26,20,30,0);
        LocalTime activityDuration = LocalTime.of(1,30,0);
        activity = new Activity(1L, "BBQ", "We are having a BBQ",activityTime, activityDuration);
        activityRepo.save(activity);

    }

    @AfterEach
    void delete(){
        hoaRepo.deleteAll();
        activityRepo.deleteAll();
    }

    @Test
    public void createActivityTest() throws Exception {

        LocalDateTime activityTime = LocalDateTime.of(2030,12,15,5,30,15);
        LocalTime activityDuration = LocalTime.of(2,30,30);

        // Set up the ActivityRequestModel to be provided in the request body
        ActivityRequestModel requestModel = new ActivityRequestModel("Test Activity", "This is a test activity",1L, activityTime, activityDuration);

        // Set up the expected Activity object that the ActivityService should be called with
        Activity expectedActivity = new Activity(1L, "Test Activity", "This is a test activity",activityTime, activityDuration);

        mockStatic(MembershipUtils.class);
        when(MembershipUtils.getMembershipById(1L)).thenReturn(new MembershipResponseModel(1L,"test user",1L,false));

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(post("/activity/create/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk()); // Assert that the response has a 200 OK status

        Activity responseActivity = activityRepo.findById(3L).orElseThrow();

        // Assert that the response body contains the expected activity object
        assertTrue(expectedActivity.equals(responseActivity));
    }


    @Test
    void joinActivityTest() {

    }

    @Test
    void leaveActivity() {
    }

    @Test
    void getPublicBoard() {
    }

}