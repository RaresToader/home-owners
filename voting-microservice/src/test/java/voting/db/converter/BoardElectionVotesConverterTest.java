package voting.db.converter;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.converters.BoardElectionVotesConverter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class BoardElectionVotesConverterTest {
    BoardElectionVotesConverter sut = new BoardElectionVotesConverter();

    @Test
    void convertToDatabaseColumnTest() {
        Map<String, String> map = Map.of();
        String res = sut.convertToDatabaseColumn(map);
        assertEquals("", res);

        map = Map.of("chad", "testChad");
        res = sut.convertToDatabaseColumn(map);
        assertEquals("chad=testChad", res);

        map = Map.of("chad", "testChad", "chad2", "testChad2");
        String finalRes = sut.convertToDatabaseColumn(map);
        assertTrue(() -> finalRes.equals("chad=testChad,chad2=testChad2")
                || finalRes.equals("chad2=testChad2,chad=testChad"));
    }

    @Test
    void convertToEntityAttributeTest() {
        String testStr = "";
        Map<String, String> map = sut.convertToEntityAttribute(testStr);
        assertTrue(map.isEmpty());

        testStr = "chad=testChad";
        map = sut.convertToEntityAttribute(testStr);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("chad"));
        assertEquals("testChad", map.get("chad"));

        testStr = "chad=testChad,chad2=testChad2";
        map = sut.convertToEntityAttribute(testStr);
        assertEquals(2, map.size());
        assertTrue(map.containsKey("chad"));
        assertEquals("testChad", map.get("chad"));
        assertTrue(map.containsKey("chad2"));
        assertEquals("testChad2", map.get("chad2"));
    }
}
