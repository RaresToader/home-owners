package nl.tudelft.sem.template.authmember.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.password.PasswordHashingService;
import nl.tudelft.sem.template.authmember.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthenticationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient MemberRepository memberRepository;
    @Autowired
    private transient PasswordHashingService passwordHashingService;

    @Test
    public void authenticateGoodCase() throws Exception {
        // STEP 1: Register user
        final Member member = new Member("Stefan", passwordHashingService.hash("coati69"));
        final RegistrationModel model = new RegistrationModel();
        model.setMemberId("Stefan");
        model.setPassword("coati69");
        // Act
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));
        // Assert
        resultActions.andExpect(status().isOk());

        // STEP 2: Authenticate
        ResultActions resultActions2 = mockMvc.perform(post("/member/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions2.andExpect(status().isOk());
        MvcResult result = resultActions2.andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("token");
        String token = body.split(":")[1].replace("\"", "").replace("}", "");
        System.out.println(token);

        // STEP 4: Use NO token to test get method
        // Act
        ResultActions resultActions3 = mockMvc.perform(get("/member/get/" + model.getMemberId()));

        // Assert
        resultActions3.andExpect(status().is4xxClientError());

        // STEP 5: Use  token to test get method
        // Act
        // AuthenticationResponseModel model1 = new AuthenticationResponseModel();
        //  model1.setToken("Bearer " + token);
        // ResultActions resultActions4 = mockMvc.perform(get("/member/get/"+model.getMemberId())
        //   .header("Authentication", JsonUtil.serialize(model1)));
        // Assert
        //   resultActions4.andExpect(status().isOk());
    }
}
