package nl.tudelft.sem.template.hoa.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.models.HoaRequestModel;
import nl.tudelft.sem.template.hoa.utils.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

    Hoa insertHoa() {
        Hoa hoa = Hoa.createHoa("Country", "City", "Test");
        return hoaRepo.save(hoa);
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
        Assertions.assertTrue(hoaRepo.findById(1L).isPresent());
        Hoa actual = hoaRepo.findById(1L).get();
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
        Assertions.assertTrue(hoaRepo.findById(1L).isEmpty());
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
        Assertions.assertTrue(hoaRepo.findById(1L).isPresent());
        expected.add(hoaRepo.findById(1L).get());
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
        Assertions.assertTrue(hoaRepo.findById(1L).isPresent());
        expected.add(hoaRepo.findById(1L).get());
        Assertions.assertTrue(hoaRepo.findById(2L).isPresent());
        expected.add(hoaRepo.findById(2L).get());
        Assertions.assertEquals(expected.size(), 2);
        Assertions.assertEquals(actual.size(), 2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getByIdHappy() throws Exception {
        insertHoa();
        ResultActions resultActions = mockMvc.perform(get("/hoa/getById/" + 1L)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").isNumber());
        resultActions.andExpect(jsonPath("$.country").isString());
        resultActions.andExpect(jsonPath("$.city").isString());
        resultActions.andExpect(jsonPath("$.name").isString());
        Hoa actual = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), Hoa.class);
        Assertions.assertTrue(hoaRepo.findById(1L).isPresent());
        Hoa expected = hoaRepo.findById(1L).get();
        Assertions.assertEquals(actual, expected);
        Assertions.assertEquals(actual.getCountry(), expected.getCountry());
        Assertions.assertEquals(actual.getCity(), expected.getCity());
        Assertions.assertEquals(actual.getName(), expected.getName());
    }

    @Test
    void getByIdBad() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/hoa/getById/" + 1L)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
        Assertions.assertTrue(hoaRepo.findById(1L).isEmpty());
    }

}