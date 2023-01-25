package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.MembershipResponseModel;
import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validateStreetNumber;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validateCountryCityStreet;
import static nl.tudelft.sem.template.authmember.domain.db.MembershipValidator.validatePostalCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MembershipServiceTest {

    private transient MembershipRepository membershipRepository;
    private transient MemberRepository memberRepository;
    private transient MembershipService membershipService;

    private transient String id = "member1";
    private transient String newId = "member111";
    private transient String country = "Netherlands";
    private transient String city = "Delft";
    private transient String street = "Drebelweg";
    private transient String zip = "1111AA";
    private transient String m2 = "member2";
    private transient Address address = new Address(country, city, street, "14", zip);
    private transient LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC);

    private transient Membership membership = new Membership(id, 1, address, start, null, false);
    private transient Membership membership2 = new Membership(id, 2, address, start, null, true);
    private transient Membership membership3 = new Membership(id, 2, address, start, 
            TimeUtils.absoluteDifference(start, start.plusHours(12)), true);

    @BeforeEach
    void setup() {
        memberRepository = Mockito.mock(MemberRepository.class);
        membershipRepository = Mockito.mock(MembershipRepository.class);
        membershipService = new MembershipService(membershipRepository, memberRepository);

        Mockito.when(this.membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull(id, 1L))
                .thenReturn(java.util.Optional.ofNullable(membership));
        Mockito.when(this.membershipRepository.findAllByDurationIsNull())
            .thenReturn(List.of(membership, membership2));
        Mockito.when(this.membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull(m2, 1L))
                .thenReturn(java.util.Optional.ofNullable(null));
        Mockito.when(this.membershipRepository.findByMembershipId(0L)).thenReturn(java.util.Optional.ofNullable(membership));
        Mockito.when(this.membershipRepository.findAll()).thenReturn(new ArrayList<>(List.of(membership)));
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        list.add(membership2);
        Mockito.when(this.membershipRepository.findAllByMemberIdAndDurationIsNull(id)).thenReturn(list);
        List<Membership> list2 = new ArrayList<>();
        list2.add(membership);
        Mockito.when(this.membershipRepository.findAllByMemberIdAndHoaId(id, 1L)).thenReturn(list2);
        List<Membership> list3 = new ArrayList<>();
        list3.add(membership);
        list3.add(membership3);
        Mockito.when(this.membershipRepository.findAllByMemberId(id)).thenReturn(list2);
        Mockito.when(this.memberRepository.findByMemberId(m2)).thenReturn(java.util.Optional.ofNullable(null));
        Mockito.when(this.memberRepository.findByMemberId(id)).thenReturn(java.util.Optional.ofNullable(new Member()));
        Mockito.when(this.memberRepository.findByMemberId(newId)).thenReturn(java.util.Optional.ofNullable(new Member()));
    }

    @AfterEach
    void flushRepo() {
        membershipRepository.deleteAll();
    }

    @Test
    void saveMembership() throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        assertTrue(membershipService.saveMembership(new JoinHoaModel(newId, 1L, address), false));
    }

    @Test
    void saveMembershipCountry() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(newId, 1L,
                    new Address(null, city, street, "14", zip)), false);
        });
    }

    @Test
    void saveMembershipCity() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(newId, 1L,
                    new Address(country, null, street, "14", zip)), false);
        });
    }

    @Test
    void saveMembershipStreet() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(newId, 1L,
                    new Address(country, city, null, "14", zip)), false);
        });
    }

    @Test
    void saveMembershipNumber() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(newId, 1L,
                    new Address(country, city, street, null, zip)), false);
        });
    }

    @Test
    void saveMembershipPostal() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(newId, 1L,
                    new Address(country, city, street, "1", "x")), false);
        });
    }

    @Test
    void saveMembershipNoMember() {
        assertThrows(IllegalArgumentException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(m2, 1L, address), false);
        });
    }

    @Test
    void saveMembershipAlreadyThere() {
        assertThrows(MemberAlreadyInHoaException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(id, 1L, address), false);
        });
    }
    
    @Test
    void validateCountryCityStreetNull() {
        assertFalse(validateCountryCityStreet(null));
    }

    @Test
    void validateCountryCityStreetEmpty() {
        assertFalse(validateCountryCityStreet(""));
    }

    @Test
    void validateCountryCityStreetLower() {
        assertFalse(validateCountryCityStreet("almostGood"));
    }

    @Test
    void validateCountryCityStreetWrongChar() {
        assertFalse(validateCountryCityStreet("Almost1almost"));
    }

    @Test
    void validateCountryCityStreetWrongChar2() {
        assertFalse(validateCountryCityStreet("A&lmost1almost"));
    }

    @Test
    void validateCountryCityStreetBlank() {
        assertFalse(validateCountryCityStreet("   "));
    }

    @Test
    void validateCountryCityStreetShort() {
        assertFalse(validateCountryCityStreet("Joe"));
    }

    @Test
    void validateCountryCityStreetLong() {
        assertFalse(validateCountryCityStreet("JoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMa1"));
    }

    @Test
    void validateCountryCityStreetCorrectLower() {
        assertTrue(validateCountryCityStreet("JoeM"));
    }

    @Test
    void validateCountryCityStreetCorrectUpper() {
        assertTrue(validateCountryCityStreet("JoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMa"));
    }

    @Test
    void validatePostalCodeNull() {
        assertFalse(validatePostalCode(null));
    }

    @Test
    void validatePostalCodeEmpty() {
        assertFalse(validatePostalCode(""));
    }

    @Test
    void validatePostalCodeBlank() {
        assertFalse(validatePostalCode("   "));
    }

    @Test
    void validatePostalCodeShort() {
        assertFalse(validatePostalCode("1234X"));
    }

    @Test
    void validatePostalCodeLong() {
        assertFalse(validatePostalCode("1234XAX"));
    }

    @Test
    void validatePostalCodeLong2() {
        assertFalse(validatePostalCode("12345X"));
    }

    @Test
    void validatePostalCodeCorrectLower() {
        assertTrue(validatePostalCode("1234Xa"));
    }

    @Test
    void validatePostalCodeCorrectUpper() {
        assertTrue(validatePostalCode("1234xA"));
    }


    @Test
    void validateStreetNumberNull() {
        assertFalse(validateStreetNumber(null));
    }

    @Test
    void validateStreetNumberEmpty() {
        assertFalse(validateStreetNumber(""));
    }

    @Test
    void validateStreetNumberBlank() {
        assertFalse(validateStreetNumber("   "));
    }

    @Test
    void validateStreetNumberShortOk() {
        assertTrue(validateStreetNumber("1"));
    }

    @Test
    void validateStreetNumberLetterSoon() {
        assertFalse(validateStreetNumber("123aa"));
    }

    @Test
    void validateStreetNumberLetterSoon2() {
        assertFalse(validateStreetNumber("1a2a"));
    }

    @Test
    void validateStreetNumberDigitLater() {
        assertFalse(validateStreetNumber("123a4"));
    }

    @Test
    void validateStreetLetter() {
        assertTrue(validateStreetNumber("a"));
    }

    @Test
    void validateStreetNumberCorrectUpper() {
        assertTrue(validateStreetNumber("1234x"));
    }

    @Test
    void stopMembership() {
        assertTrue(TimeUtils.absoluteDifference(start, LocalDateTime.now()).compareTo(
                        membershipService.stopMembership(new GetHoaModel(id, 1L)).getDuration()) == 0);
    }

    @Test
    void getMembershipsForMember() {
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        assertEquals(list, membershipService.getMembershipsForMember(id));
    }

    @Test
    void getActiveMembershipsForHoaId() {
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        assertEquals(list, membershipService.getActiveMembershipsByHoaId(1));
    }

    @Test
    void getMembershipsByMemberAndHoa() {
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        assertEquals(list, membershipService.getMembershipsByMemberAndHoa(id, 1L));
    }

    @Test
    void getActiveMemberships() {
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        list.add(membership2);
        assertEquals(list, membershipService.getActiveMemberships(id));
    }

    @Test
    void getActiveMembershipByMemberAndHoaNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            membershipService.getActiveMembershipByMemberAndHoa(m2, 1L);
        });
    }

    @Test
    void getActiveMembershipByMemberAndHoa() {
        assertEquals(membership, membershipService.getActiveMembershipByMemberAndHoa("member1", 1L));
    }

    @Test
    void getMembershipNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            membershipService.getMembership(1L);
        });
    }

    @Test
    void changeBoardException() {
        MembershipResponseModel model = new MembershipResponseModel(1, membership.getMemberId(),
            membership.getHoaId(), null, null, true, null, null);
        assertThrows(IllegalArgumentException.class, () -> membershipService.changeBoard(model, true));
    }

    @Test
    void getMembership() {
        assertEquals(membership, membershipService.getMembership(0L));
    }

    @Test
    void getAll() {
        assertEquals(new ArrayList<>(List.of(membership)), membershipService.getAll());
    }
}