package nl.tudelft.sem.template.authmember.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MembershipTest {

    private Address a = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = start.plusHours(12);
    private Membership m = new Membership("joe_member", 1l, a, start, end, true);

    @Test
    void testToString() {
        assertEquals("Membership{membershipID=0, memberID='joe_member', hoaID=1, address="+a.toString()+", startTime="+start.toString()+", duration="+end.toString()+", isBoard=true}", m.toString());
    }

    @Test
    void getMembershipId() {
        assertEquals(0l, m.getMembershipId());
    }

    @Test
    void getMemberId() {
        assertEquals("joe_member", m.getMemberId());
    }

    @Test
    void getHoaId() {
        assertEquals(1l, m.getHoaId());
    }

    @Test
    void getAddress() {
        assertEquals(a, m.getAddress());
    }

    @Test
    void getStartTime() {
        assertEquals(start, m.getStartTime());
    }

    @Test
    void getDuration() {
        assertEquals(end, m.getDuration());
    }

    @Test
    void isInBoard() {
        assertEquals(true, m.isInBoard());
    }

    @Test
    void setDuration() {
        m.setDuration(start.plusHours(24));
        assertEquals(start.plusHours(24), m.getDuration());
    }
}