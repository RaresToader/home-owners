package nl.tudelft.sem.template.authmember.services;

import nl.tudelft.sem.template.authmember.domain.Address;
import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.models.GetHoaModel;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.hibernate.mapping.Join;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)

class HoaServiceTest {

    private MembershipService membershipService;

    private Address a = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusHours(12);
    private Membership m1 = new Membership("member1", 1l, a, start, end, true);
    private Membership m2 = new Membership("member2", 1l, a, start, end, false);
    private Membership m3 = new Membership("member2", 2l, a, start, end, true);
    private Membership m4 = new Membership("member1", 2l, a, start.minusHours(10), start.minusHours(5), true);

    @Autowired
    private transient HoaService hoaService;

    @BeforeEach
    void setup(){
        membershipService = Mockito.mock(MembershipService.class);

        List<Membership> list1 = new ArrayList<>();
        list1.add(m1);
        Mockito.when(this.membershipService.getActiveMemberships("member1")).thenReturn(list1);

        List<Membership> list2 = new ArrayList<>();
        list2.add(m2);
        list2.add(m3);
        Mockito.when(this.membershipService.getActiveMemberships("member2")).thenReturn(list2);

        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa("member1", 1l)).thenReturn(m1);
        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa("member1", 2l)).thenThrow(new IllegalArgumentException());
        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa("member2", 1l)).thenReturn(m2);
        Mockito.when(this.membershipService.getActiveMembershipByMemberAndHoa("member2", 2l)).thenReturn(m3);

        List<Membership> list11 = new ArrayList<>();
        list11.add(m1);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa("member1", 1l)).thenReturn(list11);
        List<Membership> list12 = new ArrayList<>();
        list12.add(m4);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa("member1", 2l)).thenReturn(list12);

        List<Membership> list21 = new ArrayList<>();
        list21.add(m2);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa("member2", 1l)).thenReturn(list21);
        List<Membership> list22 = new ArrayList<>();
        list22.add(m3);
        Mockito.when(this.membershipService.getMembershipsByMemberAndHoa("member2", 2l)).thenReturn(list22);

        hoaService.setMembershipService(membershipService);
    }

    @Test
    void joinHoaAlreadyInHoa() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(a);
        m.setMemberId("member1");
        m.setHoaId(1l);
        assertThrows(MemberAlreadyInHoaException.class, () -> hoaService.joinHoa(m));
    }

    @Test
    void joinHoaDoesNotExist() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(a);
        m.setMemberId("member1");
        m.setHoaId(3l);
        assertThrows(IllegalArgumentException.class, () -> hoaService.joinHoa(m));
    }

    //TODO: Add static method mocking
    @Test
    void joinHoaWrongAddress() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(new Address("Netherlands", "Haag", "Rijswijk", "14", "1123AB"));
        m.setMemberId("member1");
        m.setHoaId(2l);
        assertThrows(IllegalArgumentException.class, () -> hoaService.joinHoa(m));
    }

    @Test
    void joinHoa() {
        JoinHoaModel m = new JoinHoaModel();
        m.setAddress(a);
        m.setMemberId("member1");
        m.setHoaId(2l);
        assertThrows(IllegalArgumentException.class, () -> hoaService.joinHoa(m));
    }

    @Test
    void leaveHoa() {
        // Member 1 will also be in HOA2
        Membership m5 = new Membership("member1", 2l, a, start, end, false);
        List<Membership> list = new ArrayList<>();
        list.add(m1);
        list.add(m5);
        Mockito.when(this.membershipService.getActiveMemberships("member1")).thenReturn(list);

        GetHoaModel m = new GetHoaModel();
        m.setMemberId("member1");
        m.setHoaId(2l);

        m5.setDuration(TimeUtils.absoluteDifference(m5.getStartTime(), LocalDateTime.now()));
        Mockito.when(this.membershipService.stopMembership(m)).thenReturn(m1);
        assertEquals(m1, hoaService.leaveHoa(m));
    }

    @Test
    void getCurrentMembership() {
        assertEquals(m1, hoaService.getCurrentMembership("member1", 1l));
    }

    @Test
    void getCurrentMembershipFalse() {
        assertThrows(IllegalArgumentException.class, () -> hoaService.getCurrentMembership("member1", 2l));
    }

    @Test
    void getMembershipsForHoa1() {
        List<Membership> res = new ArrayList();
        res.add(m1);
        assertEquals(res, hoaService.getMembershipsForHoa("member1", 1l));
        List<Membership> res2 = new ArrayList();
        res2.add(m2);
        assertEquals(res2, hoaService.getMembershipsForHoa("member2", 1l));
    }

    @Test
    void getMembershipsForHoa2() {
        List<Membership> res = new ArrayList();
        res.add(m4);
        assertEquals(res, hoaService.getMembershipsForHoa("member1", 2l));
        List<Membership> res2 = new ArrayList();
        res2.add(m3);
        assertEquals(res2, hoaService.getMembershipsForHoa("member2", 2l));
    }
}