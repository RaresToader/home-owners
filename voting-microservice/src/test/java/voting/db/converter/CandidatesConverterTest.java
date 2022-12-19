package voting.db.converter;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.converters.CandidatesConverter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class CandidatesConverterTest {

    CandidatesConverter sut = new CandidatesConverter();

    @Test
    void convertToDatabaseColumnTest() {
        List<Integer> candidates = new ArrayList<>();
        String result = sut.convertToDatabaseColumn(candidates);
        assertEquals("", result);

        candidates.add(1);
        candidates.add(2);
        result = sut.convertToDatabaseColumn(candidates);
        assertEquals("1,2", result);
    }

    @Test
    void convertToEntityAttributeTest() {
        String testStr = "";
        List<Integer> candidates = sut.convertToEntityAttribute(testStr);
        assertTrue(candidates.isEmpty());

        testStr = "1,2,3,4,5";
        candidates = sut.convertToEntityAttribute(testStr);
        assertEquals(5, candidates.size());
        assertTrue(candidates.containsAll(List.of(1, 2, 3, 4, 5)));
    }
}
