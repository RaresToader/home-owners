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
class NotificationsConverterTest {

    transient NotificationsConverter notificationsConverter = new NotificationsConverter();

    @Test
    void convertToDatabaseColumnTest() {
        Map<String, List<String>> map = Map.of("test", List.of("req1"), "test2", List.of("req21", "req2"));
        String result = notificationsConverter.convertToDatabaseColumn(map);
        assertEquals("test=[req1],test2=[req21,req2]", result);

        map = new HashMap<>();
        result = notificationsConverter.convertToDatabaseColumn(map);
        assertEquals("", result);
    }

    @Test
    void convertToEntityAttributeTest() {
        String str = "";
        Map<String, List<String>> map = notificationsConverter.convertToEntityAttribute(str);
        assertTrue(map.isEmpty());

        str = "test=[req1,req2,req3]";
        map = notificationsConverter.convertToEntityAttribute(str);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("test"));
        assertEquals(List.of("req1", "req2", "req3"), map.get("test"));
    }
}