package nl.tudelft.sem.template.hoa.db;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestSuite(testType = UNIT)
class ReportsConverterTest {

    transient ReportsConverter reportsConverter = new ReportsConverter();

    @Test
    void convertToDatabaseColumnTest() {
        Map<String, List<Long>> map = Map.of("test", List.of(1L), "test2", List.of(2L, 21L));
        final String result = reportsConverter.convertToDatabaseColumn(map);
        assertTrue(() -> result.contains("test=[1]") &&
                (result.contains("test2=[2,21]") || result.contains("test2=[21,2]")));

        map = new HashMap<>();
        String res = reportsConverter.convertToDatabaseColumn(map);
        assertEquals("", res);
    }

    @Test
    void convertToEntityAttributeTest() {
        String str = "";
        Map<String, List<Long>> map = reportsConverter.convertToEntityAttribute(str);
        assertTrue(map.isEmpty());

        str = "test=[1,2,3]";
        map = reportsConverter.convertToEntityAttribute(str);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("test"));
        assertEquals(List.of(1L, 2L, 3L), map.get("test"));
    }
}