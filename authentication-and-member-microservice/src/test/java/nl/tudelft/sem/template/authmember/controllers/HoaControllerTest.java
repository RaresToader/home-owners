package nl.tudelft.sem.template.authmember.controllers;

import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.services.HoaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class HoaControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient HoaController hoaController;

    private transient AuthManager mockAuthenticationManager = Mockito.mock(AuthManager.class);

    private transient MemberService memberService = Mockito.mock(MemberService.class);

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

    private final transient String apiJoinHoa = "/member/joinHOA";
    private final transient String membershipCheck = "\"membershipId\":";

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

        hoaController.setAuthenticationManager(mockAuthenticationManager);
        hoaController.setMemberService(memberService);
        hoaController.setHoaService(hoaService);
    }

    @Test
    void joinHoa() throws Exception {
        JoinHoaModel request = new JoinHoaModel(memberId, 1L, address);
        ResultActions resultActions = mockMvc.perform(post(apiJoinHoa)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authType + tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void joinHoaMemberAlreadyThere() throws Exception {
        JoinHoaModel request = new JoinHoaModel(badId + 1, 1L, address);
        ResultActions resultActions = mockMvc.perform(post(apiJoinHoa)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authType + tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinHoaMemberIllegalArgumentException() throws Exception {
        JoinHoaModel request = new JoinHoaModel(badId + 2, 1L, address);
        ResultActions resultActions = mockMvc.perform(post(apiJoinHoa)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authType + tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinHoaMemberDifferentAddressException() throws Exception {
        JoinHoaModel request = new JoinHoaModel(badId + 3, 1L, address);
        ResultActions resultActions = mockMvc.perform(post(apiJoinHoa)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authType + tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinHoaBadJoinHoaModelException() throws Exception {
        JoinHoaModel request = new JoinHoaModel(badId + 4, 1L, address);
        ResultActions resultActions = mockMvc.perform(post(apiJoinHoa)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authType + tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void joinHoaNoHoa() throws Exception {
        JoinHoaModel request = new JoinHoaModel(badId, 1L, address);
        ResultActions resultActions = mockMvc.perform(post(apiJoinHoa)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authType + tok)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void leaveHoa() throws Exception {
        GetHoaModel request = new GetHoaModel(memberId, 1L);
        ResultActions resultActions = mockMvc.perform(post("/member/leaveHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains(membershipCheck + m1.getMembershipId()));
    }

    @Test
    void leaveHoaUnauthorized() throws Exception {
        GetHoaModel request = new GetHoaModel(badId, 1L);
        ResultActions resultActions = mockMvc.perform(post("/member/leaveHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void leaveHoaNoMember() throws Exception {
        GetHoaModel request = new GetHoaModel(randomId, 1L);
        ResultActions resultActions = mockMvc.perform(post("/member/leaveHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void validateExistenceNonMember() {
        assertThrows(IllegalArgumentException.class, () -> hoaController
                .validateExistence(new GetHoaModel(randomId, 1L)));
    }

    @Test
    void validateExistenceUnauthorized() {
        assertThrows(IllegalAccessException.class, () -> hoaController
                .validateExistence(new GetHoaModel(badId, 1L)));
    }
}
