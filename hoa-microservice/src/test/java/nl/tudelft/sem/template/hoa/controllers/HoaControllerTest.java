package nl.tudelft.sem.template.hoa.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.db.HoaService;
import nl.tudelft.sem.template.hoa.db.RequirementService;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import nl.tudelft.sem.template.hoa.exception.HoaDoesntExistException;
import nl.tudelft.sem.template.hoa.exception.RequirementAlreadyPresent;
import nl.tudelft.sem.template.hoa.exception.RequirementDoesNotExist;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
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
public class HoaControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient HoaRepo hoaRepo;

    private transient HoaService hoaService = Mockito.mock(HoaService.class);

    private transient RequirementService requirementService = Mockito.mock(RequirementService.class);

    @Autowired
    private transient HoaController hoaController;

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
    public void registerHoaTestHappy() throws Exception {
        HoaRequestModel request = new HoaRequestModel("Country", "City", "Name");
        ResultActions resultActions = mockMvc.perform(post("/hoa/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isOk()); // this asserts that we get a 200 ok response
        resultActions.andExpect(jsonPath("$.country").isString());
        resultActions.andExpect(jsonPath("$.city").isString());
        resultActions.andExpect(jsonPath("$.name").isString());
        MvcResult result = resultActions.andExpect(jsonPath("$.id").isNumber()).andReturn();
        Hoa expected = JsonUtil.deserialize(result.getResponse().getContentAsString(), Hoa.class);
        Assertions.assertTrue(hoaRepo.findById(l1).isPresent());
        Hoa actual = hoaRepo.findById(l1).get();
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(JsonUtil.serialize(expected), JsonUtil.serialize(actual));
    }

    @Test
    public void registerHoaTestBad() throws Exception {
        HoaRequestModel request = new HoaRequestModel("Cou$", "C13ty", "  ");
        ResultActions resultActions = mockMvc.perform(post("/hoa/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 200 ok response
        Assertions.assertTrue(hoaRepo.findById(l1).isEmpty());
    }

    @Test
    public void getAllEmpty() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/hoa/getAll")
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        List<Hoa> actual = Arrays.asList(JsonUtil.deserialize(result.getResponse().getContentAsString(), Hoa[].class));
        List<Hoa> expected = new ArrayList<>();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void getAllOne() throws Exception {
        insertHoa();
        ResultActions resultActions = mockMvc.perform(get("/hoa/getAll")
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        final List<Hoa> actual = Arrays.asList(JsonUtil.deserialize(result.getResponse().getContentAsString(), Hoa[].class));
        List<Hoa> expected = new ArrayList<>();
        Assertions.assertTrue(hoaRepo.findById(l1).isPresent());
        expected.add(hoaRepo.findById(l1).get());
        Assertions.assertEquals(expected.size(), 1);
        Assertions.assertEquals(actual.size(), 1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getAllMultiple() throws Exception {
        insertHoa();
        Hoa anotherHoa = Hoa.createHoa("Other", "Other", "Other");
        hoaRepo.save(anotherHoa);
        ResultActions resultActions = mockMvc.perform(get("/hoa/getAll")
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        final List<Hoa> actual = Arrays.asList(JsonUtil.deserialize(result.getResponse().getContentAsString(), Hoa[].class));
        List<Hoa> expected = new ArrayList<>();
        Assertions.assertTrue(hoaRepo.findById(l1).isPresent());
        expected.add(hoaRepo.findById(l1).get());
        Assertions.assertTrue(hoaRepo.findById(l2).isPresent());
        expected.add(hoaRepo.findById(l2).get());
        Assertions.assertEquals(expected.size(), 2);
        Assertions.assertEquals(actual.size(), 2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getAllBad() throws Exception {
        Mockito.when(this.hoaService.getAllHoa()).thenThrow(new IllegalArgumentException());
        hoaController.setHoaService(this.hoaService);
        ResultActions resultActions = mockMvc.perform(get("/hoa/getAll")
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getByIdHappy() throws Exception {
        insertHoa();
        ResultActions resultActions = mockMvc.perform(get("/hoa/getById/" + l1)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").isNumber());
        resultActions.andExpect(jsonPath("$.country").isString());
        resultActions.andExpect(jsonPath("$.city").isString());
        resultActions.andExpect(jsonPath("$.name").isString());
        Hoa actual = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), Hoa.class);
        Assertions.assertTrue(hoaRepo.findById(l1).isPresent());
        Hoa expected = hoaRepo.findById(l1).get();
        Assertions.assertEquals(actual, expected);
        Assertions.assertEquals(actual.getCountry(), expected.getCountry());
        Assertions.assertEquals(actual.getCity(), expected.getCity());
        Assertions.assertEquals(actual.getName(), expected.getName());
    }

    @Test
    void getByIdBad() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/hoa/getById/" + l1)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Assertions.assertTrue(hoaRepo.findById(l1).isEmpty());
    }

    @Test
    void getRequirementsBad() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/hoa/getRequirements/" + l1)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getRequirements() throws Exception {
        List<Requirement> expected = new ArrayList<>();
        expected.add(new Requirement(l1, request1, l1));
        Mockito.when(this.requirementService.getHoaRequirements(l1)).thenReturn(expected);
        hoaController.setRequirementService(requirementService);
        insertHoa();
        ResultActions resultActions = mockMvc.perform(get("/hoa/getRequirements/" + l1)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        final List<Requirement> actual = Arrays.asList(JsonUtil.deserialize(
                result.getResponse().getContentAsString(), Requirement[].class));
        assertEquals(expected, actual);
    }

    @Test
    void addRequirement() throws Exception {
        Mockito.when(this.requirementService.addHoaRequirement(l1, request1)).thenReturn(new Requirement(l1, request1, l1));
        hoaController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/hoa/addRequirement/" + l1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void addRequirementRequirementAlreadyPresent() throws Exception {
        Mockito.when(this.requirementService.addHoaRequirement(l1, request1))
                .thenThrow(new RequirementAlreadyPresent(request1 + " already present"));
        hoaController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/hoa/addRequirement/" + l1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void addRequirementHoaDoesntExistException() throws Exception {
        Mockito.when(this.requirementService.addHoaRequirement(l2, request1))
                .thenThrow(new HoaDoesntExistException(l2 + " does not exist"));
        hoaController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/hoa/addRequirement/" + l2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void removeRequirements() throws Exception {
        final Requirement expected = new Requirement(l1, request1, l1);
        Mockito.when(this.requirementService.removeHoaRequirement(l1)).thenReturn(expected);
        hoaController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/hoa/removeRequirement/" + l1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1)));
        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        final Requirement actual = JsonUtil.deserialize(result.getResponse().getContentAsString(), Requirement.class);
        assertEquals(expected, actual);
    }

    @Test
    void removeRequirementsRequirementDoesNotExist() throws Exception {
        Mockito.when(this.requirementService.removeHoaRequirement(l2)).thenThrow(
                new RequirementDoesNotExist(l2 + " does not exist"));
        hoaController.setRequirementService(requirementService);

        ResultActions resultActions = mockMvc.perform(post("/hoa/removeRequirement/" + l2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2)));
        resultActions.andExpect(status().isBadRequest());
    }
}