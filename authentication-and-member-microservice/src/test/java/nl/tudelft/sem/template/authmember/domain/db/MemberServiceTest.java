package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyExistsException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.domain.password.PasswordHashingService;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    private transient MemberRepository memberRepository;
    private transient PasswordHashingService passwordHashingService;
    private transient MemberService memberService;
    private transient String id = "member1";
    private transient Member member = new Member(id, new HashedPassword("pass1"));

    @BeforeEach
    void setup(){
        memberRepository = Mockito.mock(MemberRepository.class);
        passwordHashingService = Mockito.mock(PasswordHashingService.class);
        memberService = new MemberService(memberRepository, passwordHashingService);
        Mockito.when(this.memberRepository.existsByMemberId(id)).thenReturn(true);
        Mockito.when(this.memberRepository.findByMemberId(id)).thenReturn(java.util.Optional.ofNullable(member));
        Mockito.when(this.memberRepository.existsByMemberId("member2")).thenReturn(false);
        Mockito.when(this.memberRepository.existsByMemberId("member3")).thenReturn(true);
        Mockito.when(this.memberRepository.findByMemberId("member3")).thenReturn(java.util.Optional.ofNullable(null));
    }

    @Test
    void registerUserShortName() {
        assertThrows(BadRegistrationModelException.class, () -> {
            memberService.registerUser(new RegistrationModel("short", "ok_password_123"));
        });
    }

    @Test
    void registerUserShortPassword() {
        assertThrows(BadRegistrationModelException.class, () -> {
            memberService.registerUser(new RegistrationModel("ok_member_123", "short"));
        });
    }

    @Test
    void registerUserExists() {
        assertThrows(MemberAlreadyExistsException.class, () -> {
            memberService.registerUser(new RegistrationModel(id, "ok_password_123"));
        });
    }

    @Test
    void registerUser() throws MemberAlreadyExistsException, BadRegistrationModelException {
        assertEquals("member2", memberService.registerUser(new RegistrationModel("member2", "ok_password_123")).getMemberId());
    }

    @Test
    void validateUsernameNull() {
        assertFalse(memberService.validateUsername(null));
    }

    @Test
    void validateUsernameEmpty() {
        assertFalse(memberService.validateUsername(""));
    }

    @Test
    void validateUsernameBlank() {
        assertFalse(memberService.validateUsername("   "));
    }

    @Test
    void validateUsernameShort() {
        assertFalse(memberService.validateUsername("JoeMa"));
    }

    @Test
    void validateUsernameLong() {
        assertFalse(memberService.validateUsername("JoeMaJoeMaJoeMaJoeMa1"));
    }

    @Test
    void validateUsernameCorrectLower() {
        assertTrue(memberService.validateUsername("JoeMam"));
    }

    @Test
    void validateUsernameCorrectUpper() {
        assertTrue(memberService.validateUsername("JoeMaJoeMaJoeMaJoeMa"));
    }

    @Test
    void validatePasswordNull() {
        assertFalse(memberService.validatePassword(null));
    }

    @Test
    void validatePasswordEmpty() {
        assertFalse(memberService.validatePassword(""));
    }

    @Test
    void validatePasswordBlank() {
        assertFalse(memberService.validatePassword("   "));
    }

    @Test
    void validatePasswordShort() {
        assertFalse(memberService.validatePassword("JoeMaJoeM"));
    }

    @Test
    void validatePasswordLong() {
        assertFalse(memberService.validatePassword("JoeMaJoeMaJoeMaJoeMaJoeMaJoeMaMa3"));
    }

    @Test
    void validatePasswordCorrectLower() {
        assertTrue(memberService.validatePassword("JoeMaJoeMa"));
    }

    @Test
    void validatePasswordCorrectUpper() {
        assertTrue(memberService.validatePassword("JoeMaJoeMaJoeMaJoeMaJoeMaJoeMaMa"));
    }

    @Test
    void updatePasswordShort() {
        assertThrows(BadRegistrationModelException.class, () -> {
            memberService.updatePassword(new RegistrationModel("joe", "mama"));
        });
    }

    @Test
    void updatePasswordDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.updatePassword(new RegistrationModel("joe_not_present", "joe_not_present"));
        });
    }

    @Test
    void updatePassword() throws BadRegistrationModelException {
        assertEquals(member, memberService.updatePassword(new RegistrationModel(id, "joe_present_123")));
    }

    @Test
    void getMember() {
        assertEquals(member, memberService.getMember(id));
    }

    @Test
    void getMemberNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.getMember("member2");
        });
    }

    @Test
    void getMemberNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.getMember("member3");
        });
    }
}