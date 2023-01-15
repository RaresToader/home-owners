package nl.tudelft.sem.template.authmember.integration.db;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validate;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validateCountryCityStreet;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validatePostalCode;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validateStreetNumber;

public class MembershipValidatorTest {

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
    public void validateCountryCityNameNull() {
        Assertions.assertFalse(validateCountryCityStreet(null));
    }

    @Test
    public void validateCountryCityNameEmpty() {
        Assertions.assertFalse(validateCountryCityStreet(""));
    }

    @Test
    public void validateCountryCityNameSize() {
        Assertions.assertFalse(validateCountryCityStreet("te"));
    }

    @Test
    public void validateCountryCityNameBlank() {
        Assertions.assertFalse(validateCountryCityStreet("   "));
    }

    @Test
    public void validateCountryCityNameNotUpperCase() {
        Assertions.assertFalse(validateCountryCityStreet(" a "));
    }

    @Test
    public void validateCountryCityNameNotValidChar() {
        Assertions.assertFalse(validateCountryCityStreet(" Aaa$  "));
    }

    @Test
    public void validateCountryCityNameHappy() {
        Assertions.assertTrue(validateCountryCityStreet(" T e st"));
    }

    @Test
    public void validatePostalCodeNull() {
        Assertions.assertFalse(validatePostalCode(null));
    }

    @Test
    public void validatePostalCodeEmpty() {
        Assertions.assertFalse(validatePostalCode("     "));
    }

    @Test
    public void validatePostalCodeNotEnough() {
        Assertions.assertFalse(validatePostalCode(" tttt "));
    }

    @Test
    public void validatePostalCodeNotDigit() {
        Assertions.assertFalse(validatePostalCode("222aBD"));
    }

    @Test
    public void validatePostalCodeNotLetter() {
        Assertions.assertFalse(validatePostalCode("2222B$"));
    }

    @Test
    public void validatePostalCodeHappy() {
        Assertions.assertTrue(validatePostalCode("2222AB"));
    }

    @Test
    public void validateStreetNumberNull() {
        Assertions.assertFalse(validateStreetNumber(null));
    }

    @Test
    public void validateStreetNumberEmpty() {
        Assertions.assertFalse(validateStreetNumber(""));
    }

    @Test
    public void validateStreetNumberNonDigit() {
        Assertions.assertFalse(validateStreetNumber("77$3"));
    }

    @Test
    public void validateStreetNumberBlank() {
        Assertions.assertFalse(validateStreetNumber("   "));
    }

    @Test
    public void validateStreetNumberNonDigitLast() {
        Assertions.assertFalse(validateStreetNumber("1$"));
    }

    @Test
    public void validateStreetNumberHappyLastLetter() {
        Assertions.assertTrue(validateStreetNumber("77A"));
    }

    @Test
    public void validateStreetNumberLastDigit() {
        Assertions.assertTrue(validateStreetNumber("223"));
    }

    @Test
    public void validateHappy() {
        Assertions.assertTrue(validate(new JoinHoaModel("m1", 1L, new Address("Netherlands",
                "Delft", "Drebelweg", "14", "1111AA"))));
    }
}
