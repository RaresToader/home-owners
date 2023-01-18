package nl.tudelft.sem.template.authmember.controllers;

import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient MemberRepository memberRepository;

    @Autowired
    private transient MemberController memberController;

    private transient AuthManager mockAuthenticationManager = Mockito.mock(AuthManager.class);

    private transient MemberService memberService = Mockito.mock(MemberService.class);

    private final transient String tok = "token123";
    private final transient String memberId = "john_doe";
    private final transient String badId = "john_not_doe";
    private final transient String randomId = "randomemberId";
    private final transient String secret = "password123";
    private final transient String newText = "_new";
    private final transient String authType = "Bearer ";
    private final transient String apiRegister = "/member/register";
    private final transient String apiUpdate = "/member/updatePassword";

    @BeforeEach
    void setup() throws IllegalAccessException, BadRegistrationModelException, MemberAlreadyExistsException {
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

        memberController.setAuthenticationManager(mockAuthenticationManager);
        memberController.setMemberService(memberService);
    }

    @AfterEach
    void flushRepo() {
        memberRepository.deleteAll();
    }

    @Test
    void register() throws Exception {
        RegistrationModel request = new RegistrationModel(memberId, secret);
        ResultActions resultActions = mockMvc.perform(post(apiRegister)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void registerMemberAlreadyExistsException() throws Exception {
        RegistrationModel request = new RegistrationModel(randomId + 1, secret);
        ResultActions resultActions = mockMvc.perform(post(apiRegister)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void registerBadRegistrationModelException() throws Exception {
        RegistrationModel request = new RegistrationModel(randomId + 2, secret);
        ResultActions resultActions = mockMvc.perform(post(apiRegister)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());

    }

    @Test
    void updatePassword() throws Exception {
        RegistrationModel request = new RegistrationModel(memberId, (secret + newText));
        ResultActions resultActions = mockMvc.perform(post(apiUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("\"memberId\":\"" + memberId));
        assertTrue(expected.contains("\"password\":\"" + secret + newText));
    }

    @Test
    void updatePasswordNoRegistration() throws Exception {
        RegistrationModel request = new RegistrationModel(randomId + 1, secret);
        ResultActions resultActions = mockMvc.perform(post(apiUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void updatePasswordBadRegistration() throws Exception {
        RegistrationModel request = new RegistrationModel(randomId + 2, secret);
        ResultActions resultActions = mockMvc.perform(post(apiUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void updatePasswordUnathorized() throws Exception {
        RegistrationModel request = new RegistrationModel(badId, secret);
        ResultActions resultActions = mockMvc.perform(post(apiUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    public void getMember() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/get/" + memberId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("\"memberId\":\"" + memberId));
    }

    @Test
    public void getMemberEmpty() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/get/" + randomId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void getMemberUnathorized() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/get/" + randomId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void authenticate() throws Exception {
        RegistrationModel request = new RegistrationModel(memberId, secret);
        ResultActions resultActions = mockMvc.perform(post("/member/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }
}
