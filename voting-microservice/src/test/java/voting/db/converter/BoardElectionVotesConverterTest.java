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

    private static final String CHAD = "chad";

    @Test
    void convertToDatabaseColumnTest() {
        Map<String, String> map = Map.of();
        String res = sut.convertToDatabaseColumn(map);
        assertEquals("", res);

        map = Map.of(CHAD, "testChad");
        res = sut.convertToDatabaseColumn(map);
        assertEquals("chad=testChad", res);

        map = Map.of(CHAD, "testChad", "chad2", "testChad2");
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
        assertTrue(map.containsKey(CHAD));
        assertEquals("testChad", map.get(CHAD));

        testStr = "chad=testChad,chad2=testChad2";
        map = sut.convertToEntityAttribute(testStr);
        assertEquals(2, map.size());
        assertTrue(map.containsKey(CHAD));
        assertEquals("testChad", map.get(CHAD));
        assertTrue(map.containsKey("chad2"));
        assertEquals("testChad2", map.get("chad2"));
    }
}
