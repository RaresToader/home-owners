package nl.tudelft.sem.template.hoa.controllers;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.authentication.AuthManager;
import nl.tudelft.sem.template.hoa.db.HoaRepo;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.models.BoardElectionRequestModel;
import nl.tudelft.sem.template.hoa.models.RemoveVoteModel;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.INTEGRATION;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Mock
    private transient HoaRepo hoaRepo;
    @Autowired
    private transient ElectionController electionController;

    private static MockedStatic<MembershipUtils> membershipUtilsMockedStatic;
    private static MockedStatic<ElectionUtils> electionUtilsMockedStatic;

    private static final int el1 = 111;
    private static final int el2 = 222;
    private static final int el3 = 333;
    private static final int el4 = 444;
    private static final int pel = 555;
    private static final int pel2 = 666;
    private static final String tok = "token123";
    private static final String memberId = "john_doe";

    private static final String member2Id = "john_doe2";
    private static final String badId = "john_not_doe";
    private static final String randomId = "randomemberId";

    private static Hoa hoaTwo;
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

    static LocalDateTime currTime = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);

    private static Object election = new LinkedHashMap<String, Object>() {{
                put("hoaId", 1);
                put("status", "finished");
        }};
    private static Object election2 = new LinkedHashMap<String, Object>() {{
                put("hoaId", 2);
                put("status", "ongoing");
                put("scheduledFor", currTime.toString());
        }};
    private static Object election3 = new LinkedHashMap<String, Object>() {{
                put("hoaId", 1);
                put("status", "ongoing");
        }};
    private static Object proposal = new LinkedHashMap<String, Object>() {{
                put("hoaId", 1);
                put("status", "ongoing");
                put("winningChoice", false);
        }};

    private static Object proposal2 = new LinkedHashMap<String, Object>() {{
                put("hoaId", 2);
                put("status", "ongoing");
                put("winningChoice", true);
                put("description", "SomeRandomNotif");
        }};
    private static MembershipResponseModel m1 = new MembershipResponseModel(0L, memberId,
            1L, address.getCity(), address.getCountry(), true, start.minusYears(4), null);
    private static MembershipResponseModel m2 = new MembershipResponseModel(1L, memberId,
            2L, address.getCity(), address.getCountry(), false, start, null);
    private static MembershipResponseModel m3 = new MembershipResponseModel(2L, randomId,
            2L, address.getCity(), address.getCountry(), true, start, null);
    private static ProposalRequestModel proposalm = new ProposalRequestModel(1, "prop1",
            "des1",  new TimeModel(10, 10, 10, 10, 10, 10));
    private static ProposalRequestModel proposalBadm = new ProposalRequestModel(2, "prop2",
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
        when(MembershipUtils.getActiveMembershipsOfHoa(2L, tok))
                .thenReturn(List.of(m2, m3));

        // electionUtilsMockedStatic
        electionUtilsMockedStatic = mockStatic(ElectionUtils.class);
        when(ElectionUtils.getElectionById(el1))
                .thenReturn(election);
        when(ElectionUtils.getElectionById(el2))
                .thenReturn(election2);
        when(ElectionUtils.getElectionById(el3))
                .thenReturn(null);
        when(ElectionUtils.getElectionById(el4))
                .thenReturn(election3);
        when(ElectionUtils.getElectionById(pel))
                .thenReturn(proposal);
        when(ElectionUtils.getElectionById(pel2))
                .thenReturn(proposal2);

        when(ElectionUtils.vote(any()))
                .thenReturn(HttpStatus.ACCEPTED);
        when(ElectionUtils.removeVote(any()))
                .thenReturn(HttpStatus.ACCEPTED);

        when(ElectionUtils.leaveElection(memberId, m1.getHoaId()))
                .thenReturn(true);
        when(ElectionUtils.leaveElection(randomId, m3.getHoaId()))
                .thenReturn(false);
        when(ElectionUtils.leaveElection(badId, 1L))
                .thenThrow(new IllegalArgumentException());

        when(ElectionUtils.createBoardElection(electionRequestModel))
                .thenReturn(election);

        when(ElectionUtils.createProposal(proposalm))
                .thenReturn(proposal);

        when(ElectionUtils.concludeElection(el2))
                .thenReturn(List.of("SomeRandomWinner"));
        when(ElectionUtils.concludeElection(pel))
                .thenReturn(false);
        when(ElectionUtils.concludeElection(pel2))
                .thenReturn(true);
    }

    @BeforeEach
    void setup() {
        hoaTwo = Hoa.createHoa("Germany", "Hamburg", "RandomAssoc");
        electionController.setHoaRepo(hoaRepo);
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
                .content(JsonUtil.serialize(proposalm)));

        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(result.contains("\"hoaId\":1"));
    }

    @Test
    void createProposalBad() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/proposal/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(proposalBadm)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void createProposalBaVerification() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(proposalm)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void createProposalBadNotInHoa() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        ResultActions resultActions = mockMvc.perform(post("/voting/proposal")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(proposalm)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void createBoardElection() throws Exception {

        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(electionRequestModel)));

        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(result.contains("\"hoaId\":1"));
    }

    @Test
    void createBoardElectionBadModel() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(electionRequestModel2)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void createBoardElectionBadVerification() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/boardElection")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(electionRequestModel)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void vote() throws Exception {
        VotingModel request = new VotingModel(el1, member2Id, memberId);
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(member2Id);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
        String res = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(res.contains("ACCEPTED"));
    }

    @Test
    void voteNoAllowedBadVote() throws Exception {
        VotingModel request = new VotingModel(el1, randomId, "good_choice");
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void voteNotAllowed() throws Exception {
        VotingModel request = new VotingModel(el3, randomId, "good_choice");
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void voteNotAllowedNotBoard() throws Exception {
        VotingModel request = new VotingModel(pel2, memberId, "true");
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void voteNoAllowedBadVerification() throws Exception {
        VotingModel request = new VotingModel(el2, memberId, "good_choice");
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void removeVote() throws Exception {
        RemoveVoteModel request = new RemoveVoteModel(el1, member2Id);
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(member2Id);
        ResultActions resultActions = mockMvc.perform(post("/voting/removeVote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
        String res = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(res.contains("ACCEPTED"));
    }

    @Test
    void removeVoteBadModel() throws Exception {
        RemoveVoteModel request = new RemoveVoteModel(el1, randomId);
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/removeVote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void removeVoteNoAllowed() throws Exception {
        RemoveVoteModel request = new RemoveVoteModel(el3, randomId);
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void removeVoteBadVerification() throws Exception {
        RemoveVoteModel request = new RemoveVoteModel(el2, memberId);
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(badId);
        ResultActions resultActions = mockMvc.perform(post("/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
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
    void getElectionByIdNone() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(get("/voting/getElection/" + el3)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getElectionByIdUnauthorized() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        ResultActions resultActions = mockMvc.perform(get("/voting/getElection/" + el4)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));
        resultActions.andExpect(status().isUnauthorized());

        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        resultActions = mockMvc.perform(get("/voting/getElection/" + pel2)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void concludeElection() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + el2)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("\"SomeRandomWinner\""));
        membershipUtilsMockedStatic.verify(
                () -> MembershipUtils.resetBoard(eq(2L))
        );
        membershipUtilsMockedStatic.verify(
                () -> MembershipUtils.promoteWinners(any(), eq(2L))
        );
        electionUtilsMockedStatic.verify(
                () -> ElectionUtils.createBoardElection(argThat(e -> e.hoaId == 2L
                        && e.scheduledFor.year == currTime.getYear() + 1))
        );
    }

    @Test
    void concludeProposal() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        when(hoaRepo.findById(2L)).thenReturn(Optional.of(hoaTwo));
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + pel)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        verify(hoaRepo, times(0)).save(hoaTwo);
        assertEquals("{}", hoaTwo.getNotifications().toString());
        assertTrue(expected.contains("false"));
    }

    @Test
    void concludeProposalApproved() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        when(hoaRepo.findById(2L)).thenReturn(Optional.of(hoaTwo));
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + pel2)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isOk());
        verify(hoaRepo, times(1)).save(hoaTwo);
        assertTrue(hoaTwo.getNotifications().toString().contains(randomId + "=[SomeRandomNotif]"));
        assertTrue(hoaTwo.getNotifications().toString().contains(memberId + "=[SomeRandomNotif]"));
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("true"));
    }

    @Test
    void concludeElectionBadMethod() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(memberId);
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + el1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tok));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void concludeProposalBadMethod() throws Exception {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(randomId);
        when(hoaRepo.findById(2L)).thenReturn(Optional.empty());
        ResultActions resultActions = mockMvc.perform(post("/voting/conclude/" + pel2)
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

        resultActions.andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("argGen")
    void validateMemberInHoa(boolean shouldThrow, long hoaId, String memberId, boolean alsoCheckBoard) {
        if (shouldThrow)
            assertThrows(IllegalAccessException.class, () -> electionController
                    .validateMemberInHOA(hoaId, memberId, alsoCheckBoard, tok));
        else
            assertThatNoException().isThrownBy(() -> electionController
                    .validateMemberInHOA(hoaId, memberId, alsoCheckBoard, tok));
    }

    /**
     * Arg Gen for parameterized validation tests
     * @return Stream of Arguments to be used
     */
    public static Stream<Arguments> argGen() {
        return Stream.of(
                Arguments.of(true, 1L, badId, false),
                Arguments.of(true, 2L, badId, false),
                Arguments.of(true, 10L, badId, true),
                Arguments.of(true, 1L, badId, true),
                Arguments.of(true, 2L, badId, true),
                Arguments.of(true, 10L, badId, true),
                Arguments.of(false, 1L, memberId, false),
                Arguments.of(false, 1L, memberId, true),
                Arguments.of(false, 2L, memberId, false),
                Arguments.of(true, 2L, memberId, true),
                Arguments.of(true, 1L, randomId, false),
                Arguments.of(false, 2L, randomId, false),
                Arguments.of(true, 1L, randomId, true),
                Arguments.of(false, 2L, randomId, true)
        );
    }
}
