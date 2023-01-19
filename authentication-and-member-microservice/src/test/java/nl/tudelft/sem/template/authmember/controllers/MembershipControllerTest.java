package nl.tudelft.sem.template.authmember.controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.services.HoaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MembershipControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient MemberRepository memberRepository;

    @Autowired
    private transient MembershipController membershipController;

    private transient AuthManager mockAuthenticationManager = Mockito.mock(AuthManager.class);

    private transient MemberService memberService = Mockito.mock(MemberService.class);

    private transient MembershipService membershipService = Mockito.mock(MembershipService.class);

    private transient HoaService hoaService = Mockito.mock(HoaService.class);

    private final transient String tok = "token123";
    private final transient String memberId = "john_doe";
    private final transient String badId = "john_not_doe";
    private final transient String randomId = "randomemberId";
    private final transient String secret = "password123";
    private final transient String newText = "_new";
    private final transient String authType = "Bearer ";
    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private transient LocalDateTime start = LocalDateTime.now();
    private transient Membership m1 = new Membership(memberId, 1L, address, start, null, true);
    private transient Membership m2 = new Membership(memberId, 2L, address, start, null, false);

    private final transient String membershipCheck = "\"membershipId\":";
    private final transient String apiMemberships = "/member/getMembershipsForHOA";

    @BeforeEach
    void setup() throws IllegalAccessException, MemberDifferentAddressException,
            MemberAlreadyInHoaException, BadJoinHoaModelException,
            BadRegistrationModelException, MemberAlreadyExistsException {
        // AuthenticationManager mocking
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(tok);
        Mockito.when(this.mockAuthenticationManager.validateMember(badId)).thenThrow(new IllegalAccessException());

        // memberService mocking
        Mockito.when(this.memberService.getMember(memberId)).thenReturn(new Member(memberId, new HashedPassword(secret)));
        Mockito.when(this.memberService.getMember(randomId)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.memberService.updatePassword(new RegistrationModel(memberId, secret + newText)))
                .thenReturn(new Member(memberId, new HashedPassword(secret + newText)));
        Mockito.when(this.memberService.updatePassword(new RegistrationModel(randomId + 1, secret)))
                .thenThrow(new IllegalArgumentException());
        Mockito.when(this.memberService.updatePassword(new RegistrationModel(randomId + 2, secret)))
                .thenThrow(new BadRegistrationModelException("Bad boi."));
        Mockito.when(this.memberService.registerUser(new RegistrationModel(memberId, secret)))
                .thenReturn(new Member(memberId, new HashedPassword(secret)));
        Mockito.when(this.memberService.registerUser(new RegistrationModel(randomId + 1, secret)))
                .thenThrow(new MemberAlreadyExistsException(new RegistrationModel(randomId + 1, secret)));
        Mockito.when(this.memberService.registerUser(new RegistrationModel(randomId + 2, secret)))
                .thenThrow(new BadRegistrationModelException("Bad username or password!"));

        // membershipService mocking
        List<Membership> list = new ArrayList<>();
        list.add(m1);
        list.add(m2);
        Mockito.when(this.membershipService.getActiveMemberships(memberId)).thenReturn(list);
        Mockito.when(this.membershipService.getActiveMemberships(randomId)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.membershipService.getMembershipsForMember(memberId)).thenReturn(list);
        Mockito.when(this.membershipService.getMembershipsForMember(randomId)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.membershipService.getAll()).thenReturn(list);
        Mockito.when(this.membershipService.getMembership(m1.getMembershipId())).thenReturn(m1);
        Mockito.when(this.membershipService.getMembership(69L)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.membershipService.getActiveMembershipsByHoaId(1L)).thenReturn(List.of(m1));

        // HOAService mocking
        List<Membership> list2 = new ArrayList<>();
        list2.add(m1);
        Mockito.when(this.hoaService.getMembershipsForHoa(memberId, 1L)).thenReturn(list2);
        Mockito.when(this.hoaService.getMembershipsForHoa(randomId, 1L)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.hoaService.getCurrentMembership(memberId, 1L)).thenReturn(m1);
        Mockito.when(this.hoaService.getCurrentMembership(randomId, 1L)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.hoaService.leaveHoa(new GetHoaModel(memberId, 1L))).thenReturn(m1);
        Mockito.when(this.hoaService.leaveHoa(new GetHoaModel(randomId, 1L))).thenThrow(new IllegalArgumentException());
        Mockito.when(this.hoaService.joinHoa(new JoinHoaModel(memberId, 1L, address), authType + tok)).thenReturn(memberId);
        Mockito.when(this.hoaService.joinHoa(new JoinHoaModel(badId + 1, 1L, address), authType + tok))
                .thenThrow(new MemberAlreadyInHoaException(new JoinHoaModel(badId + 1, 1L, address)));
        Mockito.when(this.hoaService.joinHoa(new JoinHoaModel(badId + 2, 1L, address), authType + tok))
                .thenThrow(new IllegalArgumentException());
        Mockito.when(this.hoaService.joinHoa(new JoinHoaModel(badId + 3, 1L, address), authType + tok))
                .thenThrow(new MemberDifferentAddressException(new JoinHoaModel(badId + 3, 1L, address)));
        Mockito.when(this.hoaService.joinHoa(new JoinHoaModel(badId + 4, 1L, address), authType + tok))
                .thenThrow(new BadJoinHoaModelException("Bad model."));

        membershipController.setAuthenticationManager(mockAuthenticationManager);
        membershipController.setMemberService(memberService);
        membershipController.setMembershipService(membershipService);
        membershipController.setHoaService(hoaService);
    }

    @AfterEach
    void flushRepo() {
        memberRepository.deleteAll();
    }

    @Test
    void getActiveMembership() throws Exception {
        GetHoaModel request = new GetHoaModel(memberId, 1L);
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMembership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains(membershipCheck + m1.getMembershipId()));
    }

    @Test
    void getActiveMembershipUnauthorized() throws Exception {
        GetHoaModel request = new GetHoaModel(badId, 1L);
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMembership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void getActiveMembershipNoMember() throws Exception {
        GetHoaModel request = new GetHoaModel(randomId, 1L);
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMembership")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getMembershipsForHoa() throws Exception {
        GetHoaModel request = new GetHoaModel(memberId, 1L);
        ResultActions resultActions = mockMvc.perform(get(apiMemberships)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains(membershipCheck + m1.getMembershipId()));
    }

    @Test
    void getMembershipsForHoaNoMember() throws Exception {
        GetHoaModel request = new GetHoaModel(randomId, 1L);
        ResultActions resultActions = mockMvc.perform(get(apiMemberships)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getMembershipsForHoaUnauthorized() throws Exception {
        GetHoaModel request = new GetHoaModel(badId, 1L);
        ResultActions resultActions = mockMvc.perform(get(apiMemberships)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void getMembershipsForNoMember() throws Exception {
        GetHoaModel request = new GetHoaModel(randomId, 1L);
        ResultActions resultActions = mockMvc.perform(get(apiMemberships)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getMemberships() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getMemberships/" + memberId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains(membershipCheck + m1.getMembershipId()));
    }

    @Test
    void getMembershipsIllegal() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getMemberships/" + randomId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getMembershipsUnauthorized() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getMemberships/" + badId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void getMembershipById() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getMembershipById/" + m1.getMembershipId())
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andExpect(jsonPath("$.membershipId").isNumber()).andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains(membershipCheck + m1.getMembershipId()));
    }

    @Test
    void getMembershipByIdIllegal() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getMembershipById/" + 69L)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getAllMemberships() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getAllMemberships")
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        String res = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(res.contains("\"hoaId\":1"));
        assertTrue(res.contains("\"hoaId\":2"));
    }

    @Test
    void getAllMembershipsHoa() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getAllMemberships/" + 1L)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void getActiveMemberships() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMemberships/" + memberId)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        String res = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(res.contains("\"hoaId\":1"));
    }

    @Test
    void getActiveMembershipsNoMember() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMemberships/" + randomId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void getActiveMembershipsUnauthorized() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMemberships/" + badId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void promoteTest() throws Exception {
        List<String> request = List.of(memberId);
        ResultActions resultActions = mockMvc.perform(post("/member/promoteWinners/" + 1L
                + "/Thisisacustomseckeyforpromotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isOk());
        String res = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(res.contains("true"));
        verify(membershipService, times(1)).changeBoard(any(), eq(true));
    }

    @Test
    void promoteFailTest() throws Exception {
        // Invalid if request body is empty
        ResultActions resultActions = mockMvc.perform(post("/member/promoteWinners/" + 1L
                + "/Thisisacustomseckeyforpromotion")
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        // Invalid key
        List<String> request = List.of(memberId);
        resultActions = mockMvc.perform(post("/member/promoteWinners/" + 1L + "/Badkey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isUnauthorized());

        // Invalid hoa
        resultActions = mockMvc.perform(post("/member/promoteWinners/" + 3L
                + "/Thisisacustomseckeyforpromotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void demoteTest() throws Exception {
        String req = "Thisisacustomseckeyforclear";
        ResultActions resultActions = mockMvc.perform(post("/member/resetBoard/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req));
        resultActions.andExpect(status().isOk());
        String res = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(res.contains("true"));
        verify(membershipService, times(1)).changeBoard(any(), eq(false));
    }

    @Test
    void demoteFailTest() throws Exception {
        // Invalid if request body is empty
        ResultActions resultActions = mockMvc.perform(post("/member/resetBoard/" + 1L)
                .contentType(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());

        String wrongReq = "RandomKey";
        // Invalid key
        resultActions = mockMvc.perform(post("/member/resetBoard/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(wrongReq));
        resultActions.andExpect(status().isUnauthorized());

        String req = "Thisisacustomseckeyforclear";
        // Invalid hoa
        resultActions = mockMvc.perform(post("/member/resetBoard/" + 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req));
        resultActions.andExpect(status().isBadRequest());
    }


    @Test
    void validateExistenceNonMember() {
        assertThrows(IllegalArgumentException.class, () -> membershipController
                .validateExistence(new GetHoaModel(randomId, 1L)));
    }

    @Test
    void validateExistenceUnauthorized() {
        assertThrows(IllegalAccessException.class, () -> membershipController
                .validateExistence(new GetHoaModel(badId, 1L)));
    }
}
