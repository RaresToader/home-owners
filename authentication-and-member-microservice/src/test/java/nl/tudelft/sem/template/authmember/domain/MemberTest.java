package nl.tudelft.sem.template.authmember.domain;

import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private HashedPassword p = new HashedPassword("MAMA_password_123");
    private Member m = new Member("Joe_Mama", p);

    @Test
    void getMemberId() {
        assertEquals("Joe_Mama", m.getMemberId());
    }

    @Test
    void getPassword() {
        assertEquals(p, m.getPassword());
    }

    @Test
    void testEquals() {
        Member b = new Member("Joe_Mama", new HashedPassword("Mama_different"));
        assertTrue(m.equals(b));
    }

    @Test
    void testEqualsSame() {
        assertTrue(m.equals(m));
    }

    @Test
    void testEqualsDiff() {
        Member b = new Member("Joe_Papa", new HashedPassword("Mama_different"));
        assertTrue(m.equals(b));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(m.getMemberId()), m.hashCode());
    }
}