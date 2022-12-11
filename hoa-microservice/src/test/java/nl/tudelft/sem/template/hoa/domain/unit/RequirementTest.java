package nl.tudelft.sem.template.hoa.domain.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class RequirementTest {

    @Test
    void constructorTest() {
        Requirement req = new Requirement("Cannot be null.", 10L);
        assertNotNull(req);
    }

    @Test
    void getRequirementTest() {
        Requirement req = new Requirement("Cannot be null.", 10L);
        assertEquals(req.getPrompt(), "Cannot be null.");
    }

    @Test
    void getHoaId() {
        Requirement req = new Requirement("Cannot be null.", 10L);
        assertEquals(req.getHoaId(), 10L);
    }

    @Test
    void hashCodeTest() {
        Requirement req = new Requirement("Cannot be null.", 10L);
        Map<Requirement, Integer> map = new HashMap<>();
        map.put(req, 1);
        assertEquals(map.get(req), 1);
    }

    @Test
    void equalsTest1() {
        Requirement req = new Requirement("Cannot be null.", 10L);
        assertEquals(req, req);
    }

    @Test
    void equalsTest2() {
        Requirement req = new Requirement("Cannot be null.", 10L);
        Assertions.assertNotEquals(req, null);
    }

}
