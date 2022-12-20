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
        List<String> candidates = new ArrayList<>();
        String result = sut.convertToDatabaseColumn(candidates);
        assertEquals("", result);

        candidates.add("test1");
        candidates.add("test2");
        result = sut.convertToDatabaseColumn(candidates);
        assertEquals("test1,test2", result);
    }

    @Test
    void convertToEntityAttributeTest() {
        String testStr = "";
        List<String> candidates = sut.convertToEntityAttribute(testStr);
        assertTrue(candidates.isEmpty());

        testStr = "test1,test2";
        candidates = sut.convertToEntityAttribute(testStr);
        assertEquals(2, candidates.size());
        assertTrue(candidates.containsAll(List.of("test1", "test2")));
    }
}
