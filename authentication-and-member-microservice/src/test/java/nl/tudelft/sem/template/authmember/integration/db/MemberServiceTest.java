package nl.tudelft.sem.template.authmember.integration.db;

import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.password.PasswordHashingService;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class MemberServiceTest {

    @Mock
    private transient MemberRepository memberRepository;

    @Mock
    private transient PasswordHashingService passwordHashingService;

    private transient MemberService memberService;

    @BeforeEach
    public void setUp() {
        memberService = new MemberService(memberRepository, passwordHashingService);
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
        assertThrows(BadRegistrationModelException.class,
                () -> memberService.registerUser(new RegistrationModel("use", "password")));
    }

    @Test
    public void registerUserBadPassword() {
        assertThrows(BadRegistrationModelException.class,
                () -> memberService.registerUser(new RegistrationModel("username123", "pass")));
    }

    @Test
    public void updateBadPassword() {
        assertThrows(BadRegistrationModelException.class,
                () -> memberService.updatePassword(new RegistrationModel("username123", "pass")));
    }

}
