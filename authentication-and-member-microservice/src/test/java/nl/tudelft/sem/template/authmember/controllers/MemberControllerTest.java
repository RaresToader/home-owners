package nl.tudelft.sem.template.authmember.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;

import nl.tudelft.sem.template.authmember.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authmember.models.HoaResponseModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.services.HoaService;
import nl.tudelft.sem.template.authmember.utils.HoaUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private HoaService hoaService;



    @Autowired
    private transient MemberController memberController;

    private transient AuthManager mockAuthenticationManager = Mockito.mock(AuthManager.class);

    private transient HoaUtils hoaUtils = Mockito.mock(HoaUtils.class);

    @BeforeEach
    void setup() {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn("ExampleUser");
//        Mockito.when(this.hoaUtils.getHoaById(1)).thenReturn(new HoaResponseModel());
        memberController.setAuthenticationManager(mockAuthenticationManager);
    }

//    @BeforeEach
//    public void setup() throws IllegalAccessException {
//
//    }

    void insertMember() {
        Member member = new Member("john_doe", new HashedPassword("password123"));
        memberRepository.save(member);
    }

    void insertHoa(JoinHoaModel member) throws MemberDifferentAddressException, MemberAlreadyInHoaException, BadJoinHoaModelException {
        hoaService.joinHoa(member);
    }

    @AfterEach
    void flushRepo() {
        memberRepository.deleteAll();
    }


    @Test
    public void getAllEmpty() throws Exception {
//        ResultActions resultActions = mockMvc.perform(get("/member/get/1")
//                .contentType(MediaType.APPLICATION_JSON));
//        resultActions.andExpect(status().isOk());
    }
    @Test
    void register() throws Exception {
        RegistrationModel request = new RegistrationModel("john_doe", "password123");
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk()); // this asserts that we get a 200 ok response

//        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn("john_doe");
//
//        resultActions = mockMvc.perform(get("/member/get/john_doe")
//                .contentType(MediaType.APPLICATION_JSON));
//
//        MvcResult result = resultActions.andExpect(jsonPath("$.memberId").isString()).andReturn();
//        Member expected = JsonUtil.deserialize(result.getResponse().getContentAsString(), Member.class);
//        Assertions.assertTrue(memberRepository.findByMemberId("john_doe").isPresent());
//        Member actual = memberRepository.findById("john_doe").get();
//        Assertions.assertEquals(expected, actual);
//        Assertions.assertEquals(JsonUtil.serialize(expected), JsonUtil.serialize(actual));
    }

    @Test
    void registerShortName() throws Exception {
        RegistrationModel request = new RegistrationModel("john", "password123");
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void registerShortPassword() throws Exception {
        RegistrationModel request = new RegistrationModel("john_doe", "pass");
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void registerTwiceSameName() throws Exception {
        RegistrationModel request = new RegistrationModel("john_doe", "password123");
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk()); // this asserts that we get a 200 ok response
        resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void updatePassword() throws Exception {
//        insertMember();
//        RegistrationModel request = new RegistrationModel("john_doe", "password123_new");
//        ResultActions resultActions = mockMvc.perform(post("/member/updatePassword")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JsonUtil.serialize(request)));
//
//        resultActions.andExpect(status().isOk()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void updatePasswordShort() throws Exception {
        insertMember();
        RegistrationModel request = new RegistrationModel("john_doe", "pass");
        ResultActions resultActions = mockMvc.perform(post("/member/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void updatePasswordNoRegistration() throws Exception {
        RegistrationModel request = new RegistrationModel("john_doe", "password123");
        ResultActions resultActions = mockMvc.perform(post("/member/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    public void getMember() throws Exception {
//        insertMember();
//        ResultActions resultActions = mockMvc.perform(get("/member/get/john_doe")
//                .contentType(MediaType.APPLICATION_JSON));
//
//        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    public void getMemberEmpty() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/get/john_doe")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void joinHoa() throws Exception {
        insertMember();
        JoinHoaModel request = new JoinHoaModel();
        request.setMemberId("john_doe");
        request.setHoaId(1l);
        request.setAddress(new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA"));
        ResultActions resultActions = mockMvc.perform(post("/member/joinHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void joinHoaNoHoa() throws Exception {
        insertMember();
        JoinHoaModel request = new JoinHoaModel();
        request.setMemberId("john_doe");
        request.setHoaId(1l);
        request.setAddress(new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA"));
        ResultActions resultActions = mockMvc.perform(post("/member/joinHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void leaveHoa() {
    }

    @Test
    void getMembership() {
    }

    @Test
    void getMembershipsForHoa() {
    }

    @Test
    void getMemberships() {
    }

    @Test
    void getActiveMemberships() {
    }

    @Test
    void validateExistence() {
    }

    @Test
    void authenticate() throws Exception {
        RegistrationModel request = new RegistrationModel("john_doe", "password123");
        ResultActions resultActions = mockMvc.perform(post("/member/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized()); // this asserts that we get a 200 ok response
    }

}
