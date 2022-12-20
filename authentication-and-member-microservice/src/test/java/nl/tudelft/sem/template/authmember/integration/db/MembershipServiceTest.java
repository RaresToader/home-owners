package nl.tudelft.sem.template.authmember.integration.db;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class MembershipServiceTest {

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
    public void validateCountryCityNameNull() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet(null));
    }

    @Test
    public void validateCountryCityNameEmpty() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet(""));
    }

    @Test
    public void validateCountryCityNameSize() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet("te"));
    }

    @Test
    public void validateCountryCityNameBlank() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet("   "));
    }

    @Test
    public void validateCountryCityNameNotUpperCase() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet(" a "));
    }

    @Test
    public void validateCountryCityNameNotValidChar() {
        Assertions.assertFalse(membershipService.validateCountryCityStreet(" Aaa$  "));
    }

    @Test
    public void validateCountryCityNameHappy() {
        Assertions.assertTrue(membershipService.validateCountryCityStreet(" T e st"));
    }

    @Test
    public void validatePostalCodeNull() {
        Assertions.assertFalse(membershipService.validatePostalCode(null));
    }

    @Test
    public void validatePostalCodeEmpty() {
        Assertions.assertFalse(membershipService.validatePostalCode("     "));
    }

    @Test
    public void validatePostalCodeNotEnough() {
        Assertions.assertFalse(membershipService.validatePostalCode(" tttt "));
    }

    @Test
    public void validatePostalCodeNotDigit() {
        Assertions.assertFalse(membershipService.validatePostalCode("222aBD"));
    }

    @Test
    public void validatePostalCodeNotLetter() {
        Assertions.assertFalse(membershipService.validatePostalCode("2222B$"));
    }

    @Test
    public void validatePostalCodeHappy() {
        Assertions.assertTrue(membershipService.validatePostalCode("2222AB"));
    }

    @Test
    public void validateStreetNumberNull() {
        Assertions.assertFalse(membershipService.validateStreetNumber(null));
    }

    @Test
    public void validateStreetNumberEmpty() {
        Assertions.assertFalse(membershipService.validateStreetNumber(""));
    }

    @Test
    public void validateStreetNumberNonDigit() {
        Assertions.assertFalse(membershipService.validateStreetNumber("77$3"));
    }

    @Test
    public void validateStreetNumberBlank() {
        Assertions.assertFalse(membershipService.validateStreetNumber("   "));
    }

    @Test
    public void validateStreetNumberNonDigitLast() {
        Assertions.assertFalse(membershipService.validateStreetNumber("1$"));
    }

    @Test
    public void validateStreetNumberHappyLastLetter() {
        Assertions.assertTrue(membershipService.validateStreetNumber("77A"));
    }

    @Test
    public void validateStreetNumberLastDigit() {
        Assertions.assertTrue(membershipService.validateStreetNumber("223"));
    }


}
