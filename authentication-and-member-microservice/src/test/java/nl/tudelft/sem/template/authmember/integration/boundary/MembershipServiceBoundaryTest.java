package nl.tudelft.sem.template.authmember.integration.boundary;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validateCountryCityStreet;

public class MembershipServiceBoundaryTest {

    private transient MembershipService membershipService;
    @Mock
    private transient MembershipRepository membershipRepository;

    @Mock
    private transient MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        membershipService = new MembershipService(membershipRepository, memberRepository);
    }

    @Test
    public void offPointLeft() {
        Assertions.assertFalse(validateCountryCityStreet("Tes"));
    }

    @Test
    public void offPointRight() {
        Assertions.assertFalse(validateCountryCityStreet("T".repeat(51)));
    }

    @Test
    public void onPointLeft() {
        Assertions.assertTrue(validateCountryCityStreet("Test"));
    }

    @Test
    public void onPointRight() {
        Assertions.assertTrue(validateCountryCityStreet("T".repeat(50)));
    }
}
