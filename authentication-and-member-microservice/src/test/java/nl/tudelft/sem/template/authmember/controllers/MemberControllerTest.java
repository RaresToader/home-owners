package nl.tudelft.sem.template.authmember.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import nl.tudelft.sem.template.authmember.authentication.AuthManager;
import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.converters.MembershipConverter;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authmember.models.*;
import nl.tudelft.sem.template.authmember.services.HoaService;
import nl.tudelft.sem.template.authmember.utils.HoaUtils;
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
public class MemberControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient MemberRepository memberRepository;

    private transient MemberService memberService = Mockito.mock(MemberService.class);

    private transient MembershipService membershipService = Mockito.mock(MembershipService.class);

    @Autowired
    private transient HoaService hoaService;

    private final transient String m_id = "john_doe";
    private final transient String b_id = "john_not_doe";
    private final transient String secret = "password123";
    private final transient String API = "/member/register";
    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private transient LocalDateTime start = LocalDateTime.now();
    private transient LocalDateTime end = start.plusHours(12);
    private transient Membership m1 = new Membership(m_id, 1L, address, start, null, true);
    private transient Membership m2 = new Membership(m_id, 2L, address, start, null, false);

    @Autowired
    private transient MemberController memberController;

    private transient AuthManager mockAuthenticationManager = Mockito.mock(AuthManager.class);

    private transient HoaUtils hoaUtils = Mockito.mock(HoaUtils.class);

    @BeforeEach
    void setup() throws IllegalAccessException {
        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn("john_doe");
        Mockito.when(this.mockAuthenticationManager.validateMember(b_id)).thenThrow(new IllegalAccessException());
        Mockito.when(this.memberService.getMember(m_id)).thenReturn(new Member(m_id, new HashedPassword(secret)));
        Mockito.when(this.memberService.getMember(b_id)).thenThrow(new IllegalArgumentException());
        List<Membership> list = new ArrayList<>();
        list.add(m1);
        list.add(m2);
        Mockito.when(this.membershipService.getActiveMemberships(m_id)).thenReturn(list);
        Mockito.when(this.membershipService.getActiveMemberships(b_id)).thenReturn(new ArrayList<>());
        Mockito.when(this.membershipService.getAll()).thenReturn(list);
        Mockito.when(this.membershipService.getMembership(m1.getMembershipId())).thenReturn(m1);
        Mockito.when(this.membershipService.getMembership(69L)).thenThrow(new IllegalArgumentException());
        ;
        // Mockito.when(this.hoaUtils.getHoaById(1)).thenReturn(new HoaResponseModel());
        memberController.setAuthenticationManager(mockAuthenticationManager);
        memberController.setMemberService(memberService);
        memberController.setMembershipService(membershipService);
    }

    //    @BeforeEach
    //    public void setup() throws IllegalAccessException {
    //
    //    }

    void insertMember() {
        Member member = new Member(m_id, new HashedPassword(secret));
        memberRepository.save(member);
    }

    void insertHoa(JoinHoaModel member)
            throws MemberDifferentAddressException,
            MemberAlreadyInHoaException, BadJoinHoaModelException {
    //        hoaService.joinHoa(member);
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
        RegistrationModel request = new RegistrationModel(m_id, secret);
        ResultActions resultActions = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk()); // this asserts that we get a 200 ok response

        //        Mockito.when(this.mockAuthenticationManager.getMemberId()).thenReturn(m_id);
        //
        //        resultActions = mockMvc.perform(get("/member/get/john_doe")
        //                .contentType(MediaType.APPLICATION_JSON));
        //
        //        MvcResult result = resultActions.andExpect(jsonPath("$.memberId").isString()).andReturn();
        //        Member expected = JsonUtil.deserialize(result.getResponse().getContentAsString(), Member.class);
        //        Assertions.assertTrue(memberRepository.findByMemberId(m_id).isPresent());
        //        Member actual = memberRepository.findById(m_id).get();
        //        Assertions.assertEquals(expected, actual);
        //        Assertions.assertEquals(JsonUtil.serialize(expected), JsonUtil.serialize(actual));
    }

    @Test
    void registerShortName() throws Exception {
        RegistrationModel request = new RegistrationModel("john", secret);
        ResultActions resultActions = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void registerShortPassword() throws Exception {
        RegistrationModel request = new RegistrationModel(m_id, "pass");
        ResultActions resultActions = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void registerTwiceSameName() throws Exception {
        RegistrationModel request = new RegistrationModel(m_id, secret);
        ResultActions resultActions = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk()); // this asserts that we get a 200 ok response
        resultActions = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void updatePassword() throws Exception {
        //        insertMember();
        //        RegistrationModel request = new RegistrationModel(m_id, "password123_new");
        //        ResultActions resultActions = mockMvc.perform(post("/member/updatePassword")
        //                .contentType(MediaType.APPLICATION_JSON)
        //                .content(JsonUtil.serialize(request)));
        //
        //        resultActions.andExpect(status().isOk()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void updatePasswordShort() throws Exception {
        insertMember();
        RegistrationModel request = new RegistrationModel(m_id, "pass");
        ResultActions resultActions = mockMvc.perform(post("/member/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest()); // this asserts that we get a 400 BadRequest response
    }

    @Test
    void updatePasswordNoRegistration() throws Exception {
        RegistrationModel request = new RegistrationModel(m_id, secret);
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
        request.setMemberId(m_id);
        request.setHoaId(1L);
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
        request.setMemberId(m_id);
        request.setHoaId(1L);
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
    void getActiveMemberships() throws Exception {
    }

    @Test
    void getMembershipById() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getMembershipById/" + m1.getMembershipId())
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        MvcResult result = resultActions.andExpect(jsonPath("$.membershipId").isNumber()).andReturn();
        String expected = result.getResponse().getContentAsString();
        assertTrue(expected.contains("\"membershipId\":" + m1.getMembershipId()));
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
    }

    @Test
    void validateExistence() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMemberships/" + m_id)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void validateExistenceNoMember() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMemberships/" + "random_id")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void validateExistenceUnauthorized() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/member/getActiveMemberships/" + b_id)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void authenticate() throws Exception {
        RegistrationModel request = new RegistrationModel(m_id, secret);
        ResultActions resultActions = mockMvc.perform(post("/member/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isUnauthorized());
    }
}
