package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadRegistrationModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.domain.password.PasswordHashingService;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.models.RegistrationModel;
import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.SerializationUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MembershipServiceTest {

    private transient MembershipRepository membershipRepository;
    private transient MemberRepository memberRepository;
    private transient MembershipService membershipService;

    private transient String id = "member1";
    private transient String new_id = "member111";
    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private transient LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC);

    private transient Membership membership = new Membership(id, 1, address, start, null, false);
    private transient Membership membership2 = new Membership(id, 2, address, start, null, true);
    private transient Membership membership3 = new Membership(id, 2, address, start, TimeUtils.absoluteDifference(start, start.plusHours(12)), true);

    @BeforeEach
    void setup(){
        memberRepository = Mockito.mock(MemberRepository.class);
        membershipRepository = Mockito.mock(MembershipRepository.class);
        membershipService = new MembershipService(membershipRepository, memberRepository);

        Mockito.when(this.membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull(id, 1l)).thenReturn(java.util.Optional.ofNullable(membership));
        Mockito.when(this.membershipRepository.findByMemberIdAndHoaIdAndDurationIsNull("member2", 1l)).thenReturn(java.util.Optional.ofNullable(null));
        Mockito.when(this.membershipRepository.findByMembershipId(0l)).thenReturn(java.util.Optional.ofNullable(membership));
        Mockito.when(this.membershipRepository.findAll()).thenReturn(new ArrayList<>());
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        list.add(membership2);
        Mockito.when(this.membershipRepository.findAllByMemberIdAndDurationIsNull(id)).thenReturn(list);
        List<Membership> list2 = new ArrayList<>();
        list2.add(membership);
        Mockito.when(this.membershipRepository.findAllByMemberIdAndHoaId(id, 1l)).thenReturn(list2);
        List<Membership> list3 = new ArrayList<>();
        list3.add(membership);
        list3.add(membership3);
        Mockito.when(this.membershipRepository.findAllByMemberId(id)).thenReturn(list2);
        Mockito.when(this.memberRepository.findByMemberId("member2")).thenReturn(java.util.Optional.ofNullable(null));
        Mockito.when(this.memberRepository.findByMemberId(id)).thenReturn(java.util.Optional.ofNullable(new Member()));
        Mockito.when(this.memberRepository.findByMemberId(new_id)).thenReturn(java.util.Optional.ofNullable(new Member()));;
    }

    @AfterEach
    void flushRepo() {
        membershipRepository.deleteAll();
    }

    @Test
    void saveMembership() throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        assertTrue(membershipService.saveMembership(new JoinHoaModel(new_id, 1l, address)));
    }

    @Test
    void saveMembershipCountry() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(new_id, 1l, new Address(null, "Delft", "Drebelweg", "14", "1111AA")));
        });
    }

    @Test
    void saveMembershipCity() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(new_id, 1l, new Address("Netherlands", null, "Drebelweg", "14", "1111AA")));
        });
    }

    @Test
    void saveMembershipStreet() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(new_id, 1l, new Address("Netherlands", "Delft", null, "14", "1111AA")));
        });
    }

    @Test
    void saveMembershipNumber() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(new_id, 1l, new Address("Netherlands", "Delft", "Drebelweg", null, "1111AA")));
        });
    }

    @Test
    void saveMembershipPostal() {
        assertThrows(BadJoinHoaModelException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(new_id, 1l, new Address("Netherlands", "Delft", "Drebelweg", "1", "x")));
        });
    }

    @Test
    void saveMembershipNoMember() {
        assertThrows(IllegalArgumentException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel("member2", 1l, address));
        });
    }

    @Test
    void saveMembershipAlreadyThere() {
        assertThrows(MemberAlreadyInHoaException.class, () -> {
            membershipService.saveMembership(new JoinHoaModel(id, 1l, address));
        });
    }
    
    @Test
    void validateCountryCityStreetNull() {
        assertFalse(membershipService.validateCountryCityStreet(null));
    }

    @Test
    void validateCountryCityStreetEmpty() {
        assertFalse(membershipService.validateCountryCityStreet(""));
    }

    @Test
    void validateCountryCityStreetLower() {
        assertFalse(membershipService.validateCountryCityStreet("almostGood"));
    }

    @Test
    void validateCountryCityStreetWrongChar() {
        assertFalse(membershipService.validateCountryCityStreet("Almost1almost"));
    }

    @Test
    void validateCountryCityStreetWrongChar2() {
        assertFalse(membershipService.validateCountryCityStreet("A&lmost1almost"));
    }

    @Test
    void validateCountryCityStreetBlank() {
        assertFalse(membershipService.validateCountryCityStreet("   "));
    }

    @Test
    void validateCountryCityStreetShort() {
        assertFalse(membershipService.validateCountryCityStreet("Joe"));
    }

    @Test
    void validateCountryCityStreetLong() {
        assertFalse(membershipService.validateCountryCityStreet("JoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMa1"));
    }

    @Test
    void validateCountryCityStreetCorrectLower() {
        assertTrue(membershipService.validateCountryCityStreet("JoeM"));
    }

    @Test
    void validateCountryCityStreetCorrectUpper() {
        assertTrue(membershipService.validateCountryCityStreet("JoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMaJoeMa"));
    }

    @Test
    void validatePostalCode() {
    }

    @Test
    void validatePostalCodeNull() {
        assertFalse(membershipService.validatePostalCode(null));
    }

    @Test
    void validatePostalCodeEmpty() {
        assertFalse(membershipService.validatePostalCode(""));
    }

    @Test
    void validatePostalCodeBlank() {
        assertFalse(membershipService.validatePostalCode("   "));
    }

    @Test
    void validatePostalCodeShort() {
        assertFalse(membershipService.validatePostalCode("1234X"));
    }

    @Test
    void validatePostalCodeLong() {
        assertFalse(membershipService.validatePostalCode("1234XAX"));
    }

    @Test
    void validatePostalCodeLong2() {
        assertFalse(membershipService.validatePostalCode("12345X"));
    }

    @Test
    void validatePostalCodeCorrectLower() {
        assertTrue(membershipService.validatePostalCode("1234Xa"));
    }

    @Test
    void validatePostalCodeCorrectUpper() {
        assertTrue(membershipService.validatePostalCode("1234xA"));
    }


    @Test
    void validateStreetNumberNull() {
        assertFalse(membershipService.validateStreetNumber(null));
    }

    @Test
    void validateStreetNumberEmpty() {
        assertFalse(membershipService.validateStreetNumber(""));
    }

    @Test
    void validateStreetNumberBlank() {
        assertFalse(membershipService.validateStreetNumber("   "));
    }

    @Test
    void validateStreetNumberShortOk() {
        assertTrue(membershipService.validateStreetNumber("1"));
    }

    @Test
    void validateStreetNumberLetterSoon() {
        assertFalse(membershipService.validateStreetNumber("123aa"));
    }

    @Test
    void validateStreetNumberLetterSoon2() {
        assertFalse(membershipService.validateStreetNumber("1a2a"));
    }

    @Test
    void validateStreetNumberDigitLater() {
        assertFalse(membershipService.validateStreetNumber("123a4"));
    }

    @Test
    void validateStreetLetter() {
        assertTrue(membershipService.validateStreetNumber("a"));
    }

    @Test
    void validateStreetNumberCorrectUpper() {
        assertTrue(membershipService.validateStreetNumber("1234x"));
    }

    @Test
    void stopMembership() {
        assertEquals(TimeUtils.absoluteDifference(start, LocalDateTime.now()), membershipService.stopMembership(new GetHoaModel(id, 1L)).getDuration());
    }

    @Test
    void getMembershipsForMember() {
        List<Membership> list = new ArrayList<>();
        list.add(membership);
        assertEquals(list, membershipService.getMembershipsForMember(id));
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
            membershipService.getActiveMembershipByMemberAndHoa("member2", 1l);
        });
    }

    @Test
    void getActiveMembershipByMemberAndHoa() {
        assertEquals(membership, membershipService.getActiveMembershipByMemberAndHoa("member1", 1l));
    }

    @Test
    void getMembershipNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            membershipService.getMembership(1l);
        });
    }

    @Test
    void getMembership() {
        assertEquals(membership, membershipService.getMembership(0l));
    }

    @Test
    void getAll() {
        assertEquals(new ArrayList<>(), membershipService.getAll());
    }
}