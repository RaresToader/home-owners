package nl.tudelft.sem.template.authmember.integration.boundary;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class MembershipServiceBoundaryTest {

    private MembershipService membershipService;
    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        membershipService = new MembershipService(membershipRepository, memberRepository);
    }

    @Test
    public void offPointLeft() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet("Tes"));
    }

    @Test
    public void offPointRight() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet("T".repeat(51)));
    }

    @Test
    public void onPointLeft() {
        Assertions.assertTrue(membershipService.validateCountryCityStreet("Test"));
    }

    @Test
    public void onPointRight() {
        Assertions.assertTrue(membershipService.validateCountryCityStreet("T".repeat(50)));
    }
}
