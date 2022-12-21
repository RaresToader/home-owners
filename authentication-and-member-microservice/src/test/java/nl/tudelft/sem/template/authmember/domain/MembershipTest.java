package nl.tudelft.sem.template.authmember.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MembershipTest {

    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private transient LocalDateTime start = LocalDateTime.now();
    private transient LocalDateTime end = start.plusHours(12);
    private transient Membership membership = new Membership("joe_member", 1L, address, start, end, true);

    @Test
    void testToString() {
        assertEquals("Membership{membershipID=0, memberID='joe_member', hoaID=1, address="
                + address.toString() + ", startTime=" + start.toString() + ", duration=" + end.toString()
                + ", isBoard=true}", membership.toString());
    }

    @Test
    void getMembershipId() {
        assertEquals(0L, membership.getMembershipId());
    }

    @Test
    void getMemberId() {
        assertEquals("joe_member", membership.getMemberId());
    }

    @Test
    void getHoaId() {
        assertEquals(1L, membership.getHoaId());
    }

    @Test
    void getAddress() {
        assertEquals(address, membership.getAddress());
    }

    @Test
    void getStartTime() {
        assertEquals(start, membership.getStartTime());
    }

    @Test
    void getDuration() {
        assertEquals(end, membership.getDuration());
    }

    @Test
    void isInBoard() {
        assertEquals(true, membership.isInBoard());
    }

    @Test
    void setDuration() {
        membership.setDuration(start.plusHours(24));
        assertEquals(start.plusHours(24), membership.getDuration());
    }
}