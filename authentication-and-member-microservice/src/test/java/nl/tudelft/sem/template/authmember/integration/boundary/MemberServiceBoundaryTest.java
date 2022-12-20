package nl.tudelft.sem.template.authmember.integration.boundary;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class MemberServiceBoundaryTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    public void validateUsernameOffPointLeft() {
        Assertions.assertFalse(memberService.validateUsername(" test1  "));
    }

    @Test
    public void validateUsernameOffPointRight() {
        String string = "a".repeat(21);
        Assertions.assertFalse(memberService.validateUsername(string));
    }

    @Test
    public void validateUsernameOnPointLeft() {
        Assertions.assertTrue(memberService.validateUsername(" test12  "));
    }

    @Test
    public void validateUsernameOnPointRight() {
        String string = "a".repeat(20);
        Assertions.assertTrue(memberService.validateUsername(string));
    }

    @Test
    public void validatePasswordOffPointLeft() {
        Assertions.assertFalse(memberService.validatePassword("a".repeat(9)));
    }

    @Test
    public void validatePasswordOffPointRight() {
        Assertions.assertFalse(memberService.validatePassword("a".repeat(21)));
    }


    @Test
    public void validatePasswordOnPointLeft() {
        Assertions.assertTrue(memberService.validatePassword("a".repeat(10)));
    }

    @Test
    public void validatePasswordOnPointRight() {
        Assertions.assertTrue(memberService.validatePassword("a".repeat(20)));
    }


}
