package nl.tudelft.sem.template.hoa.controllers;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.h2.value.ValueJson.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.hoa.db.ActivityRepo;
import nl.tudelft.sem.template.hoa.db.ActivityService;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.domain.Activity;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.models.ActivityRequestModel;
import nl.tudelft.sem.template.hoa.utils.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.LocalTime;


@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HoaRepo hoaRepo;
    @Autowired
    private ActivityRepo activityRepo;

    private Activity activity;


    @BeforeEach
    void setUp(){
        Hoa hoa = Hoa.createHoa("country","city","name");
        hoaRepo.save(hoa);
        assertEquals(hoaRepo.findById(1L).orElseThrow(),hoa);

        activity = new Activity(1L, "Test Activity", "This is a test activity",LocalDateTime.of(2030,12,15,5,0),LocalTime.of(2,0));
        activityRepo.save(activity);
        assertEquals(activityRepo.findById(2L).orElseThrow(),activity);
    }

    @AfterEach
    void delete(){
        hoaRepo.deleteAll();
    }

    @Test
    public void createActivityTest() throws Exception {
        // Set up the ActivityRequestModel to be provided in the request body
        ActivityRequestModel requestModel = new ActivityRequestModel("Test Activity", "This is a test activity",1L, LocalDateTime.of(2030,12,15,5,0), LocalTime.of(2,0));

        // Set up the expected Activity object that the ActivityService should be called with
        Activity expectedActivity = new Activity(1L, "Test Activity", "This is a test activity",LocalDateTime.of(2030,12,15,5,0),LocalTime.of(2,0));

        // Perform a POST request to the /activity/create endpoint with the requestModel in the request body
        ResultActions resultActions = mockMvc.perform(post("/activity/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk()); // Assert that the response has a 200 OK status

        // Retrieve the response body as an Activity object
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