package voting.db.converter;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class VotesConverterTest {
//    VotesConverter sut = new VotesConverter();
//
//    @Test
//    void convertToDatabaseColumnTest() {
//        Map<Integer, Integer> map = Map.of();
//        String res = sut.convertToDatabaseColumn(map);
//        assertEquals("", res);
//
//        map = Map.of(42, 69);
//        res = sut.convertToDatabaseColumn(map);
//        assertEquals("42=69", res);
//
//        map = Map.of(1, 1, 2, 0);
//        String finalRes = sut.convertToDatabaseColumn(map);
//        assertTrue(() -> finalRes.equals("1=1,2=0") || finalRes.equals("2=0,1=1"));
//    }
//
//    @Test
//    void convertToEntityAttributeTest() {
//        String testStr = "";
//        Map<Integer, Integer> map = sut.convertToEntityAttribute(testStr);
//        assertTrue(map.isEmpty());
//
//        testStr = "11=12";
//        map = sut.convertToEntityAttribute(testStr);
//        assertEquals(1, map.size());
//        assertTrue(map.containsKey(11));
//        assertEquals(12, map.get(11));
//
//        testStr = "1=2,3=4";
//        map = sut.convertToEntityAttribute(testStr);
//        assertEquals(2, map.size());
//        assertTrue(map.containsKey(1));
//        assertEquals(2, map.get(1));
//        assertTrue(map.containsKey(3));
//        assertEquals(4, map.get(3));
//    }
}
