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
import voting.models.ElectionModel;
import voting.models.ProposalModel;
import voting.models.RemoveVoteModel;
import voting.models.TimeModel;
import voting.models.VotingModel;
import voting.util.JsonUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static voting.annotations.TestSuite.TestType.INTEGRATION;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@TestSuite(testType = {INTEGRATION})
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
        validTimeModel = new TimeModel(1, 1, 1, 1, 1,
                LocalDateTime.now().getYear());
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
        ProposalModel reqModel = new ProposalModel("Test Proposal", "This is a test proposal",
                1, validTimeModel);

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
        ProposalModel reqModel = new ProposalModel("Test Proposal", "This is a test proposal",
                -1, validTimeModel);

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
    }

    @Test
    void createProposalScheduledSuccessTest() throws Exception {
        ProposalModel reqModel = new ProposalModel("Test Proposal", "This is a test proposal",
                1, validTimeModel);

        Election expected = new Proposal(reqModel.name, reqModel.description, reqModel.hoaId,
                reqModel.scheduledFor.createDate().plusDays(42));
        expected.setElectionId(1);

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/specifiedProposal/42")
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
    void createProposalScheduledFailTest() throws Exception {
        ProposalModel reqModel = new ProposalModel("Test Proposal", "This is a test proposal",
                -1, validTimeModel);
        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/specifiedProposal/42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));
        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());

        reqModel = new ProposalModel("Test Proposal", "This is a test proposal", 1,
                validTimeModel);
        // Perform a POST request
        response = mockMvc.perform(post("/voting/specifiedProposal/a")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));
        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
    }


    @Test
    void createBoardElectionSuccessTest() throws Exception {
        BoardElectionModel reqModel = new BoardElectionModel(new ElectionModel("Test Board Election",
                "This is a test board election", 1,
                new TimeModel(validTimeModel.seconds, validTimeModel.minutes, validTimeModel.hours,
                validTimeModel.day, validTimeModel.month, validTimeModel.year + 1)),
                1, List.of("1", "2", "3"));

        Election expected = new BoardElection(reqModel.name, reqModel.description, reqModel.hoaId,
                reqModel.scheduledFor.createDate().plusYears(1), reqModel.amountOfWinners, reqModel.candidates);
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
        BoardElectionModel reqModel = new BoardElectionModel(new ElectionModel("Test Board Election",
                "This is a test board election", -1, validTimeModel), 1,
                List.of("1", "2", "3"));

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
    }

    @Test
    void voteSuccessTest() throws Exception {
        Election p = new Proposal(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate().minusDays(1));
        electionRepo.save(p);
        VotingModel reqModel = new VotingModel(1, "2", "false");

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has a 200 OK status
        response.andExpect(status().isOk());
        assertEquals(1, Integer.parseInt(response.andReturn().getResponse().getContentAsString()));

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

        reqModel = new VotingModel(2, "chad", "aaaa");
        // Perform a POST request
        response = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));
        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());

        BoardElection fetchedP = (BoardElection) electionRepo.findByElectionId(1).orElse(null);
        assertNotNull(fetchedP, "Make sure entry is persisted");
        assertEquals(0, fetchedP.getVotes().entrySet().size(), "Make sure votes are not changed");
    }

    @Test
    void removeVoteSuccessTest() throws Exception {
        TimeModel newTimeModel = new TimeModel(1, 1, 1, 1, 1, 1);
        Election p = new BoardElection(VALID_NAME, VALID_DESC, 1, newTimeModel.createDate(),
            1, List.of("1"));
        p.setStatus("ongoing");
        p.vote("2", "1");
        electionRepo.save(p);
        RemoveVoteModel reqModel = new RemoveVoteModel(1, "2");

        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/removeVote")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(reqModel)));

        // Assert that the response has     a 200 OK status
        response.andExpect(status().isOk());
        assertEquals(0, Integer.parseInt(response.andReturn().getResponse().getContentAsString()));

        BoardElection fetchedP = (BoardElection) electionRepo.findByElectionId(1).orElse(null);
        assertNotNull(fetchedP, "Make sure entry is persisted");
        assertTrue(fetchedP.getVotes().isEmpty(), "Make sure vote is persisted");
    }

    @Test
    void removeVoteFailTest() throws Exception {
        // Cannot remove non-existing vote
        Election p = new Proposal(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate());
        p.setStatus("ongoing");
        electionRepo.save(p);
        RemoveVoteModel reqModel = new RemoveVoteModel(1, "1");
        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/removeVote")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.serialize(reqModel)));
        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
        Proposal fetchedP = (Proposal) electionRepo.findByElectionId(1).orElse(null);
        assertNotNull(fetchedP, "Make sure entry is persisted");
        assertTrue(fetchedP.getVotes().isEmpty(), "Make sure vote is persisted");

        // Election has finished
        p = new Proposal(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate());
        p.setStatus("finished");
        electionRepo.save(p);
        reqModel = new RemoveVoteModel(1, "1");
        // Perform a POST request
        response = mockMvc.perform(post("/voting/removeVote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(reqModel)));
        // Assert that the response has a 400 BadRequest status
        response.andExpect(status().isBadRequest());
        fetchedP = (Proposal) electionRepo.findByElectionId(1).orElse(null);
        assertNotNull(fetchedP, "Make sure entry is persisted");
        assertTrue(fetchedP.getVotes().isEmpty(), "Make sure vote is persisted");
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

    @Test
    void joinElectionSuccessTest() throws Exception {
        BoardElection be = new BoardElection(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate(),
                1, List.of());
        electionRepo.save(be);
        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/joinElection/testCandidate/" + 1)
                .contentType(MediaType.APPLICATION_JSON));
        // Check result
        response.andExpect(status().isOk());
        Boolean returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Boolean.class);
        assertTrue(returned, "Candidate join was successful");

        // Second POST request
        response = mockMvc.perform(post("/voting/joinElection/testCandidate/" + 1)
                .contentType(MediaType.APPLICATION_JSON));
        // Check result for idempotence (second join)
        response.andExpect(status().isOk());
        returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Boolean.class);
        assertFalse(returned, "Idempotence");
    }

    @Test
    void joinElectionFailTest() throws Exception {
        BoardElection be = new BoardElection(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate(),
                1, List.of());
        electionRepo.save(be);
        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/joinElection/testCandidate/" + 2)
                .contentType(MediaType.APPLICATION_JSON));
        // Check result
        response.andExpect(status().isBadRequest());
    }

    @Test
    void leaveElectionSuccessTest() throws Exception {
        BoardElection be = new BoardElection(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate(),
                1, List.of("testCandidate"));
        electionRepo.save(be);
        // Perform POST request
        ResultActions response = mockMvc.perform(post("/voting/leaveElection/testCandidate/" + 1)
                .contentType(MediaType.APPLICATION_JSON));
        // Check result
        response.andExpect(status().isOk());
        Boolean returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Boolean.class);
        assertTrue(returned, "First leave is successful");

        // Second POST request
        response = mockMvc.perform(post("/voting/leaveElection/testCandidate/" + 1)
                .contentType(MediaType.APPLICATION_JSON));
        // Check result for idempotence (second join)
        response.andExpect(status().isOk());
        returned = JsonUtil.deserialize(response.andReturn()
                .getResponse().getContentAsString(), Boolean.class);
        assertFalse(returned, "Idempotence");
    }

    @Test
    void leaveElectionFailTest() throws Exception {
        BoardElection be = new BoardElection(VALID_NAME, VALID_DESC, 1, validTimeModel.createDate(),
                1, List.of());
        electionRepo.save(be);
        // Perform a POST request
        ResultActions response = mockMvc.perform(post("/voting/leaveElection/testCandidate/" + 2)
                .contentType(MediaType.APPLICATION_JSON));
        // Check result
        response.andExpect(status().isBadRequest());
    }
}
