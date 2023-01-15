package nl.tudelft.sem.template.hoa.controllers;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestSuite(testType = INTEGRATION)
public class RequirementControllerTest {
    /*
    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient HoaRepo hoaRepo;

    private transient HoaService hoaService = Mockito.mock(HoaService.class);

    private transient RequirementService requirementService = Mockito.mock(RequirementService.class);

    @Autowired
    private transient HoaController hoaController;


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

     */
}
