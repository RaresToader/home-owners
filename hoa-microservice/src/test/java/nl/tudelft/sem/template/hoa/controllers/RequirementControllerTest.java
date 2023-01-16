package nl.tudelft.sem.template.hoa.controllers;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import nl.tudelft.sem.template.hoa.utils.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestSuite(testType = INTEGRATION)
public class RequirementControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient HoaRepo hoaRepo;

    private transient HoaService hoaService = Mockito.mock(HoaService.class);

    private transient RequirementService requirementService = Mockito.mock(RequirementService.class);

    @Autowired
    private transient RequirementController requirementController;

    private transient String request1 = "req1";
    private transient String request2 = "req2";

    private transient Long l1 = 1L;
    private transient Long l2 = 2L;

    void insertHoa() {
        Hoa hoa = Hoa.createHoa("Country", "City", "Test");
        hoaRepo.save(hoa);
    }

    @AfterEach
    void flushRepo() {
        hoaRepo.deleteAll();
    }

    @Test
    void getRequirementsBad() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/requirement/getRequirements/" + l1)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getRequirements() throws Exception {
        List<Requirement> expected = new ArrayList<>();
        expected.add(new Requirement(l1, request1, l1));
        Mockito.when(this.requirementService.getHoaRequirements(l1)).thenReturn(expected);
        requirementController.setRequirementService(requirementService);
        insertHoa();
        ResultActions resultActions = mockMvc.perform(get("/requirement/getRequirements/" + l1)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        final List<Requirement> actual = Arrays.asList(JsonUtil.deserialize(
                result.getResponse().getContentAsString(), Requirement[].class));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addRequirement() throws Exception {
        Mockito.when(this.requirementService.addHoaRequirement(l1, request1)).thenReturn(new Requirement(l1, request1, l1));
        requirementController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/requirement/addRequirement/" + l1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void addRequirementRequirementAlreadyPresent() throws Exception {
        Mockito.when(this.requirementService.addHoaRequirement(l1, request1))
                .thenThrow(new RequirementAlreadyPresent(request1 + " already present"));
        requirementController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/requirement/addRequirement/" + l1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void addRequirementHoaDoesntExistException() throws Exception {
        Mockito.when(this.requirementService.addHoaRequirement(l2, request1))
                .thenThrow(new HoaDoesntExistException(l2 + " does not exist"));
        requirementController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/requirement/addRequirement/" + l2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void removeRequirements() throws Exception {
        final Requirement expected = new Requirement(l1, request1, l1);
        Mockito.when(this.requirementService.removeHoaRequirement(l1)).thenReturn(expected);
        requirementController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/requirement/removeRequirement/" + l1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        final Requirement actual = JsonUtil.deserialize(result.getResponse().getContentAsString(), Requirement.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void removeRequirementsRequirementDoesNotExist() throws Exception {
        Mockito.when(this.requirementService.removeHoaRequirement(l2)).thenThrow(
                new RequirementDoesNotExist(l2 + " does not exist"));
        requirementController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/requirement/removeRequirement/" + l2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2)));
        resultActions.andExpect(status().isBadRequest());
    }


}
