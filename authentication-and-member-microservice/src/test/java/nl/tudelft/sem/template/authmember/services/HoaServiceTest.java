package nl.tudelft.sem.template.authmember.services;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.models.*;
import nl.tudelft.sem.template.authmember.utils.HoaUtils;
import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HoaServiceTest {

    private transient MembershipService membershipService;

    private transient String tok = "token123";
    private transient String mem1 = "member1";
    private transient String mem2 = "member2";

    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private transient LocalDateTime start = LocalDateTime.now();
    private transient LocalDateTime end = start.plusHours(12);
    private transient Membership m1 = new Membership(mem1, 1L, address, start, null, true);
    private transient Membership m2 = new Membership(mem2, 1L, address, start, null, false);
    private transient Membership m3 = new Membership(mem2, 2L, address, start, null, true);
    private transient Membership m4 = new Membership(mem1, 2L, address, start.minusHours(10),TimeUtils.absoluteDifference(start, start.plusHours(5)), true);

    private transient JoinHoaModel bad = new JoinHoaModel();

    @Autowired
    private transient HoaService hoaService;

    private static MockedStatic<HoaUtils> hoaUtils;

    @BeforeAll
    static void registerMocks() {
        hoaUtils = mockStatic(HoaUtils.class);
        HoaResponseModel h1 = new HoaResponseModel(1L, "Netherlands", "Delft", "HOA1");
        HoaResponseModel h2 = new HoaResponseModel(2L, "Netherlands", "Delft", "HOA2");
        when(HoaUtils.getHoaById(1L, "token123"))
                .thenReturn(h1);
        when(HoaUtils.getHoaById(2L, "token123"))
                .thenReturn(h2);
        when(HoaUtils.getHoaById(3L, "token123"))
                .thenThrow(new IllegalArgumentException());
    }

    @AfterAll
    static void deregisterMocks() {
        hoaUtils.close();

    }

    @BeforeEach
    void setup() throws MemberAlreadyInHoaException, BadJoinHoaModelException {
        membershipService = Mockito.mock(MembershipService.class);

        List<Membership> list1 = new ArrayList<>();
        list1.add(m1);
        Mockito.when(this.membershipService.getActiveMemberships(mem1)).thenReturn(list1);

        List<Membership> list2 = new ArrayList<>();
        list2.add(m2);
        list2.add(m3);
        Mockito.when(this.membershipService.getActiveMemberships(mem2)).thenReturn(list2);

        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa(mem1, 1L)).thenReturn(m1);
        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa(mem1, 2L))
                .thenThrow(new IllegalArgumentException());
        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa(mem2, 1L)).thenReturn(m2);
        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa(mem2, 2L)).thenReturn(m3);

        List<Membership> list11 = new ArrayList<>();
        list11.add(m1);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa(mem1, 1L)).thenReturn(list11);
        List<Membership> list12 = new ArrayList<>();
        list12.add(m4);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa(mem1, 2L)).thenReturn(list12);

        List<Membership> list21 = new ArrayList<>();
        list21.add(m2);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa(mem2, 1L)).thenReturn(list21);
        List<Membership> list22 = new ArrayList<>();
        list22.add(m3);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa(mem2, 2L)).thenReturn(list22);

        Mockito.when(this.membershipService.saveMembership(bad)).thenThrow(new BadJoinHoaModelException("Bad model."));

        hoaService.setMembershipService(membershipService);
    }

    @Test
    void joinHoaAlreadyInHoa() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(address);
        m.setMemberId(mem1);
        m.setHoaId(1L);
        assertThrows(MemberAlreadyInHoaException.class, () -> hoaService.joinHoa(m, tok));
    }

    @Test
    void joinHoaDoesNotExist() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(address);
        m.setMemberId(mem1);
        m.setHoaId(3L);
        assertThrows(IllegalArgumentException.class, () -> hoaService.joinHoa(m, tok));
    }

    @Test
    void joinHoaDoesBadHoaModel() {
        bad.setAddress(address);
        bad.setMemberId("member3");
        bad.setHoaId(2L);
        assertThrows(BadJoinHoaModelException.class, () -> hoaService.joinHoa(bad, tok));
    }

    @Test
    void joinHoaWrongAddressCity() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(new Address("Netherlands", "Haag", "Rijswijk", "14", "1123AB"));
        m.setMemberId(mem1);
        m.setHoaId(2L);
        assertThrows(MemberDifferentAddressException.class, () -> hoaService.joinHoa(m, tok));
    }

    @Test
    void joinHoaWrongAddressCountry() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(new Address("Germnay", "Berlin", "Rijswijk", "14", "1123AB"));
        m.setMemberId(mem1);
        m.setHoaId(2L);
        assertThrows(MemberDifferentAddressException.class, () -> hoaService.joinHoa(m, tok));
    }

    @Test
    void joinHoaCorrect() throws MemberDifferentAddressException, MemberAlreadyInHoaException, BadJoinHoaModelException {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(address);
        m.setMemberId(mem1);
        m.setHoaId(2L);
        assertEquals(mem1, hoaService.joinHoa(m, tok));
    }

    @Test
    void leaveHoa() {
        // Member 1 will also be in HOA2
        Membership m5 = new Membership(mem1, 2L, address, start, null, false);
        List<Membership> list = new ArrayList<>();
        list.add(m1);
        list.add(m5);
        Mockito.when(this.membershipService.getActiveMemberships(mem1)).thenReturn(list);

        GetHoaModel m = new GetHoaModel();
        m.setMemberId(mem1);
        m.setHoaId(2L);

        m5.setDuration(TimeUtils.absoluteDifference(m5.getStartTime(), LocalDateTime.now()));
        Mockito.when(this.membershipService.stopMembership(m)).thenReturn(m1);
        assertEquals(m1, hoaService.leaveHoa(m));
    }

    @Test
    void getCurrentMembership() {
        assertEquals(m1, hoaService.getCurrentMembership(mem1, 1L));
    }

    @Test
    void getCurrentMembershipFalse() {
        assertThrows(IllegalArgumentException.class, () -> hoaService.getCurrentMembership(mem1, 2L));
    }

    @Test
    void getMembershipsForHoa1() {
        List<Membership> res = new ArrayList();
        res.add(m1);
        assertEquals(res, hoaService.getMembershipsForHoa(mem1, 1L));
        List<Membership> res2 = new ArrayList();
        res2.add(m2);
        assertEquals(res2, hoaService.getMembershipsForHoa(mem2, 1L));
    }

    @Test
    void getMembershipsForHoa2() {
        List<Membership> res = new ArrayList();
        res.add(m4);
        assertEquals(res, hoaService.getMembershipsForHoa(mem1, 2L));
        List<Membership> res2 = new ArrayList();
        res2.add(m3);
        assertEquals(res2, hoaService.getMembershipsForHoa(mem2, 2L));
    }
}