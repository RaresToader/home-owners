package nl.tudelft.sem.template.authmember.domain;

import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MemberTest {

    private transient HashedPassword password = new HashedPassword("MAMA_password_123");
    private transient Member member = new Member("Joe_Mama", password);

    @Test
    void getMemberId() {
        assertEquals("Joe_Mama", member.getMemberId());
    }

    @Test
    void getPassword() {
        assertEquals(password, member.getPassword());
    }

    @Test
    void testEquals() {
        Member b = new Member("Joe_Mama", new HashedPassword("Mama_different"));
        assertTrue(member.equals(b));
    }

    @Test
    void testEqualsSame() {
        assertTrue(member.equals(member));
    }

    @Test
    void testEqualsDiff() {
        Member b = new Member("Joe_Papa", new HashedPassword("Mama_different"));
        assertFalse(member.equals(b));
    }

    @Test
    void testEqualsDiffClass() {
        assertFalse(member.equals(password));
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(member.getMemberId()), member.hashCode());
    }
}