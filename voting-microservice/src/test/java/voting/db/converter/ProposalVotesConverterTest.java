package voting.db.converter;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.converters.ProposalVotesConverter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class ProposalVotesConverterTest {
    ProposalVotesConverter sut = new ProposalVotesConverter();

    private static final String CHAD = "chad";

    @Test
    void convertToDatabaseColumnTest() {
        Map<String, Boolean> map = Map.of();
        String res = sut.convertToDatabaseColumn(map);
        assertEquals("", res);

        map = Map.of(CHAD, false);
        res = sut.convertToDatabaseColumn(map);
        assertEquals(CHAD + "=F", res);

        map = Map.of(CHAD, true, "chad2", false);
        String finalRes = sut.convertToDatabaseColumn(map);
        assertTrue(() -> finalRes.equals("1=1,2=0") || finalRes.equals("chad=T,chad2=F"));
    }

    @Test
    void convertToEntityAttributeTest() {
        String testStr = "";
        Map<String, Boolean> map = sut.convertToEntityAttribute(testStr);
        assertTrue(map.isEmpty());

        testStr = CHAD + "=T";
        map = sut.convertToEntityAttribute(testStr);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(CHAD));
        assertEquals(true, map.get(CHAD));

        testStr = CHAD + "=F,chad2=T";
        map = sut.convertToEntityAttribute(testStr);
        assertEquals(2, map.size());
        assertTrue(map.containsKey(CHAD));
        assertEquals(false, map.get(CHAD));
        assertTrue(map.containsKey("chad2"));
        assertEquals(true, map.get("chad2"));
    }
}
