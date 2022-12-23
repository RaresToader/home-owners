package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.authentication.AuthManager;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.TimeModel;
import nl.tudelft.sem.template.hoa.models.Address;
import nl.tudelft.sem.template.hoa.models.ProposalRequestModel;
import nl.tudelft.sem.template.hoa.models.VotingModel;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;
import nl.tudelft.sem.template.hoa.utils.JsonUtil;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestSuite(testType = INTEGRATION)
public class ElectionControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Mock
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient ElectionController electionController;

    private static MockedStatic<MembershipUtils> membershipUtilsMockedStatic;
    private static MockedStatic<ElectionUtils> electionUtilsMockedStatic;

    private static final int el1 = 111;
    private static final int el2 = 222;
    private static final int el3 = 333;
    private static final String tok = "token123";
    private static final String memberId = "john_doe";

    private static final String member2Id = "john_doe2";
    private static final String badId = "john_not_doe";
    private static final String randomId = "randomemberId";
    private static Address address = new Address("Netherlands", "Delft", "Drebelweg",
            "14", "1111AA");
    private static LocalDateTime start = LocalDateTime.now();
    private static BoardElectionRequestModel electionRequestModel =
            new BoardElectionRequestModel(1, 1,
            new ArrayList<>(), "el1", "just el1", new TimeModel(10, 10, 10,
            10, 10, 2030));
    private static BoardElectionRequestModel electionRequestModel2 =
            new BoardElectionRequestModel(1, 3,
            new ArrayList<>(List.of(badId)), "el2", "just el2", new TimeModel(10, 10, 10,
            10, 10, 10));
    private static Object election = new Object() {
        private long hoaId = 1L;
        public long getHoaId() {
            return hoaId;
        }
    };

    private static MembershipResponseModel m1 = new MembershipResponseModel(0L, memberId,
            1L, address.getCity(), address.getCountry(), true, start.minusYears(4), null);
    private static MembershipResponseModel m2 = new MembershipResponseModel(1L, memberId,
            2L, address.getCity(), address.getCountry(), false, start, null);
    private static MembershipResponseModel m3 = new MembershipResponseModel(2L, randomId,
            2L, address.getCity(), address.getCountry(), true, start, null);
    private static ProposalRequestModel proposal = new ProposalRequestModel(1, "prop1",
            "des1",  new TimeModel(10, 10, 10, 10, 10, 10));
    private static ProposalRequestModel proposalBad = new ProposalRequestModel(2, "prop2",
            "des2",  new TimeModel(10, 10, 10, 10, 10, 10));

    @BeforeAll
    static void setupStatic() {
        // membershipUtilsMockedStatic
        membershipUtilsMockedStatic = mockStatic(MembershipUtils.class);
        List<MembershipResponseModel> list = new ArrayList<>();
        list.add(m1);
        list.add(m2);
        when(MembershipUtils.getMembershipsForUser(memberId, tok))
                .thenReturn(list);
        when(MembershipUtils.getActiveMembershipsForUser(memberId, tok))
                .thenReturn(list);
        List<MembershipResponseModel> list2 = new ArrayList<>();
        list2.add(m3);
        when(MembershipUtils.getActiveMembershipsForUser(randomId, tok))
                .thenReturn(list2);
        when(MembershipUtils.getMembershipsForUser(randomId, tok))
                .thenReturn(list2);
        when(MembershipUtils.getActiveMembershipsForUser(badId, tok))
                .thenReturn(new ArrayList<>());
        when(MembershipUtils.getMembershipsForUser(badId, tok))
                .thenReturn(new ArrayList<>());

        // electionUtilsMockedStatic
        electionUtilsMockedStatic = mockStatic(ElectionUtils.class);
        when(ElectionUtils.getElectionById(el1))
                .thenReturn(election);
        when(ElectionUtils.getElectionById(el2))
                .thenReturn(new Object() {
                    private long id;
                    public Object getHoaId() {
                        return null;
                    }
                });
        when(ElectionUtils.getElectionById(el3))
                .thenReturn(new Object() {
                    private long id;
                    public Object nothing() {
                        return id;
                    }
                });


        when(ElectionUtils.leaveElection(memberId, m1.getHoaId()))
                .thenReturn(true);
        when(ElectionUtils.leaveElection(randomId, m3.getHoaId()))
                .thenReturn(false);
        when(ElectionUtils.leaveElection(badId, 1L))
                .thenThrow(new IllegalArgumentException());

        when(ElectionUtils.createBoardElection(electionRequestModel))
                .thenReturn(el1);

        when(ElectionUtils.createBoardElection(electionRequestModel2))
                .thenThrow(new IllegalArgumentException());

        when(ElectionUtils.createProposal(proposal))
                .thenReturn(proposal);

        when(ElectionUtils.createProposal(proposalBad))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create proposal"));
    }

    @BeforeEach
    void setup() {
        electionController.setAuthenticationManager(mockAuthenticationManager);
    }

    @AfterAll
    static void deregisterMocks() {
        membershipUtilsMockedStatic.close();
    }

    @Test
    void createProposal() throws Exception {

        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/proposal/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(proposal)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void createProposalBad() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/proposal/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(proposalBad)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void createBoardElection() throws Exception {

        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(electionRequestModel)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void createBoardElectionBad() throws Exception {

        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(electionRequestModel2)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void vote() throws Exception {
        VotingModel request = new VotingModel(el1, member2Id, memberId);
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(member2Id);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void voteNoAllowed() throws Exception {
        VotingModel request = new VotingModel(el1, randomId, "good_choice");
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void voteNoUnauthorized() throws Exception {
        VotingModel request = new VotingModel(el1, memberId, "good_choice");
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void getElectionById() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(get("/voting/getElection/" + el1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("\"hoaId\":1"));
    }

    @Test
    @Disabled
    void getElectionByIdNone() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(get("/voting/getElection/" + el3)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void concludeElection() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + el1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("\"hoaId\":1"));
    }


    @Test
    void concludeElectionBadMethod() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + el2)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinElection() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/joinElection/" + m1.getHoaId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void joinElectionNotBoard() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/joinElection/" + m2.getHoaId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinElectionNotBoardLongEnough() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        ResultActions resultActions = mockMvc.perform(post("/voting/joinElection/" + m3.getHoaId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinElectionNoMemberships() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/joinElection/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void leaveElection() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/leaveElection/" + m1.getHoaId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void leaveElectionFalse() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        ResultActions resultActions = mockMvc.perform(post("/voting/leaveElection/" + m3.getHoaId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void leaveElectionUnauthorized() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/leaveElection/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void validateMemberInHoaNoBoard() {
        assertThrows(ResponseStatusException.class, () -> electionController
                .validateMemberInHOA(m2.getHoaId(), memberId, true, tok));
    }

    @Test
    void validateMemberInHoaEmpty() {
        assertThrows(ResponseStatusException.class, () -> electionController
                .validateMemberInHOA(1L, badId, false, tok));
    }
}
