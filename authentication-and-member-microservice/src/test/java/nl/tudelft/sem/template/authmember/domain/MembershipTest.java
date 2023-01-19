package nl.tudelft.sem.template.authmember.domain;

import nl.tudelft.sem.template.authmember.utils.TimeUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MembershipTest {

    private transient Address address = new Address("Netherlands", "Delft", "Drebelweg", "14", "1111AA");
    private transient LocalDateTime start = LocalDateTime.now();
    private transient LocalDateTime end = start.plusHours(12);
    private transient Membership membership = new Membership("joe_member",
            1L, address, start, TimeUtils.absoluteDifference(start, end), true);

    @Test
    void testToString() {
        assertEquals("Membership{membershipID=0, memberID='joe_member', hoaID=1, address="
                + address.toString() + ", startTime=" + start.toString()
                + ", duration=" + TimeUtils.absoluteDifference(start, end).toString()
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
        assertEquals(Duration.ofHours(12), membership.getDuration());
    }

    @Test
    void isInBoard() {
        assertEquals(true, membership.isInBoard());
    }

    @Test
    void setDuration() {
        membership.setDuration(TimeUtils.absoluteDifference(start, start.plusHours(16)));
        assertEquals(16, membership.getDuration().toHours());
    }
}