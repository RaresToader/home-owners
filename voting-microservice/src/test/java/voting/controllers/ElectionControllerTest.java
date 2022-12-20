package voting.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import voting.annotations.TestSuite;
import voting.db.repos.ElectionRepository;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;
import voting.models.TimeModel;
import voting.models.VotingModel;
import voting.util.JsonUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static voting.annotations.TestSuite.TestType.INTEGRATION;
import static voting.annotations.TestSuite.TestType.SYSTEM;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@TestSuite(testType =   {INTEGRATION, SYSTEM})
class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ElectionRepository electionRepo;

    private static TimeModel validTimeModel;

    private static final String VALID_NAME = "aaaa";

    private static final String VALID_DESC = "bbbb";

    @BeforeAll
    static void setup() {
        validTimeModel = new TimeModel(10, 10, 10, 10, 10, 2022);
    }

    @AfterEach
    void flushDatabase() {
        try (Connection CONN = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "s")) {
            Statement stmt = CONN.createStatement();
            stmt.executeUpdate("DELETE FROM ELECTIONS");
            stmt.executeUpdate("ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 1");
            stmt.close();
        }  catch (SQLException e) {
            System.out.println("SQT error");
        }
    }

    @Test
    void createProposalSuccessTest() throws Exception {
        ProposalModel reqModel = new ProposalModel();
        reqModel.name = "Test Proposal";
        reqModel.hoaId = 1;
        reqModel.description = "This is a test proposal";
        reqModel.scheduledFor = validTimeModel;

        Election expected = new Proposal(reqModel.name, reqModel.description, reqModel.hoaId,
                reqModel.scheduledFor.createDate());
        expected.setElectionId(1);

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 200 OK status
        response.andExpect(status().isOk());

        Election returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Proposal.class);
        Election res = electionRepo.findByElectionId(1).orElse(null);
        assertEquals(expected, res, "Check that db entry is equivalent to expected proposal");
        assertEquals(expected, returned, "Check that response is equivalent to expected proposal");
    }

    @Test
    void createProposalFailTest() throws Exception {
        ProposalModel reqModel = new ProposalModel();
        reqModel.name = "Test Proposal";
        reqModel.hoaId = -1;
        reqModel.description = "This is a test proposal";
        reqModel.scheduledFor = validTimeModel;

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
    }

    @Test
    void createBoardElectionSuccessTest() throws Exception {
        BoardElectionModel reqModel = new BoardElectionModel();
        reqModel.name = "Test Board Election";
        reqModel.hoaId = 1;
        reqModel.description = "This is a test board election";
        reqModel.scheduledFor = validTimeModel;
        reqModel.amountOfWinners = 1;
        reqModel.candidates = List.of("1", "2", "3");

        Election expected = new BoardElection(reqModel.name, reqModel.description, reqModel.hoaId,
                reqModel.scheduledFor.createDate(), reqModel.amountOfWinners, reqModel.candidates);
        expected.setElectionId(1);

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 200 OK status
        response.andExpect(status().isOk());

        Election returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), BoardElection.class);
        Election res = electionRepo.findById(1).orElse(null);
        assertEquals(expected, res, "Check that db entry is equivalent to expected board election");
        assertEquals(expected, returned, "Check that response is equivalent to expected board election");
    }

    @Test
    void createBoardElectionFailTest() throws Exception {
        BoardElectionModel reqModel = new BoardElectionModel();
        reqModel.name = "Test Board Election";
        reqModel.hoaId = -1;
        reqModel.description = "This is a test board election";
        reqModel.scheduledFor = validTimeModel;
        reqModel.amountOfWinners = 1;
        reqModel.candidates = List.of("1", "2", "3");

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
    }

    @Test
    void voteSuccessTest() throws Exception {
        Election p = new Proposal(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate());
        electionRepo.save(p);
        VotingModel reqModel = new VotingModel(1, "2", "false");

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 200 OK status
        response.andExpect(status().isOk());

        Proposal fetchedP = (Proposal) electionRepo.findByElectionId(1).orElse(null);
        assertNotNull(fetchedP, "Make sure entry is persisted");
        assertTrue(fetchedP.getVotes().entrySet()
                .stream().anyMatch(e -> e.getKey().equals("2") && !e.getValue()), "Make sure vote is persisted");
    }

    @Test
    void voteFailTest() throws Exception {
        Election p = new BoardElection(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate(),
                1, List.of());
        electionRepo.save(p);
        VotingModel reqModel = new VotingModel(1, "chad", "aaaa");

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());

        BoardElection fetchedP = (BoardElection) electionRepo.findByElectionId(1).orElse(null);
        assertNotNull(fetchedP, "Make sure entry is persisted");
        assertEquals(0, fetchedP.getVotes().entrySet().size(), "Make sure votes are not changed");
    }

    @Test
    void getElectionByIdSuccessTest() throws Exception {
        Election p = new Proposal(VALID_NAME, VALID_DESC,  1, validTimeModel.createDate());
        p = electionRepo.save(p);

        // Perform a GET request
        ResultActions response = mockMvc.perform(get("/voting/getElection/" + 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert that the response has a 200 OK status
        response.andExpect(status().isOk());

        Proposal returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Proposal.class);

        assertEquals(returned, p, "Check that returned proposal is equivalent to expected");
        assertEquals(returned, electionRepo.findByElectionId(1).orElse(null),
                "Check that returned proposal is persisted");
    }

    @Test
    void getElectionByIdFailTest() throws Exception {
        // Perform a GET request
        ResultActions response = mockMvc.perform(get("/voting/getElection/" + 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert that the response has a 400 BadRequest status
        // Fail to fetch a non-existent election
        response.andExpect(status().isBadRequest());
    }

    @Test
    void concludeElectionSuccessTest() throws Exception {
        Election p = new Proposal(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate());
        p.setStatus("ongoing");
        p.vote("chad", true);
        p.vote("chad2", true);
        electionRepo.save(p);

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/conclude/" + 1)
                .contentType(MediaType.APPLICATION_JSON));

        Boolean returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Boolean.class);
        assertTrue(returned, "Majority vote is positive/true");

        VotingModel reqModel = new VotingModel(1, "chad2", "false");
        // Perform a POST request
        response = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));
        // Make sure one cannot vote for concluded election
        response.andExpect(status().isBadRequest());
    }

    @Test
    void concludeElectionFailTest() throws Exception {
        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/conclude/1")
                .contentType(MediaType.APPLICATION_JSON));
        // Cannot conclude a non-existent election
        response.andExpect(status().isBadRequest());
    }
}
