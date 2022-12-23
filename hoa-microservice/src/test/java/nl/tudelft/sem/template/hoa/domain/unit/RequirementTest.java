package nl.tudelft.sem.template.hoa.domain.unit;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@TestSuite(testType = UNIT)
public class RequirementTest {

    private static final String PROMPT = "Cannot be null.";

    @Test
    void constructorTest() {
        Requirement req = new Requirement(PROMPT, 10L);
        assertNotNull(req);
    }

    @Test
    void getRequirementTest() {
        Requirement req = new Requirement(PROMPT, 10L);
        assertEquals(req.getPrompt(), PROMPT);
    }

    @Test
    void getHoaId() {
        Requirement req = new Requirement(PROMPT, 10L);
        assertEquals(req.getHoaId(), 10L);
    }

    @Test
    void hashCodeTest() {
        Requirement req = new Requirement(PROMPT, 10L);
        Map<Requirement, Integer> map = new HashMap<>();
        map.put(req, 1);
        assertEquals(map.get(req), 1);
    }

    @Test
    void equalsTest1() {
        Requirement req = new Requirement(PROMPT, 10L);
        assertEquals(req, req);
    }

    @Test
    void equalsTest2() {
        Requirement req = new Requirement(PROMPT, 10L);
        Assertions.assertNotEquals(req, null);
    }

}
