package nl.tudelft.sem.template.authmember.integration.db;

import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    public void validateUsernameNull() {
        Assertions.assertFalse(memberService.validateUsername(null));
    }

    @Test
    public void validateUsernameEmpty() {
        Assertions.assertFalse(memberService.validateUsername(""));
    }

    @Test
    public void validateUsernameBlank() {
        Assertions.assertFalse(memberService.validateUsername("   "));
    }

    @Test
    public void validateUsernameHappy() {
        Assertions.assertTrue(memberService.validateUsername(" username "));
    }

    @Test
    public void validatePasswordNull() {
        Assertions.assertFalse(memberService.validatePassword(null));
    }

    @Test
    public void validatePasswordEmpty() {
        Assertions.assertFalse(memberService.validatePassword(""));
    }

    @Test
    public void validatePasswordBlank() {
        Assertions.assertFalse(memberService.validatePassword("    "));
    }

    @Test
    public void validatePasswordHappy() {
        Assertions.assertTrue(memberService.validatePassword("password1234"));
    }

    @Test
    public void registerUserBadUsername() {
        RegistrationModel model = new RegistrationModel("use", "password");
        assertThrows(BadRegistrationModelException.class, () -> memberService.registerUser(model));
    }

    @Test
    public void registerUserBadPassword() {
        RegistrationModel model = new RegistrationModel("username123", "pass");
        assertThrows(BadRegistrationModelException.class, () -> memberService.registerUser(model));
    }

}
