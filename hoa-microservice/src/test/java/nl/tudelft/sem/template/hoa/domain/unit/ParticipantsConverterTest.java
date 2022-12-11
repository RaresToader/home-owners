package nl.tudelft.sem.template.hoa.domain.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.template.hoa.domain.ParticipantsConverter;
import org.junit.jupiter.api.Test;

class ParticipantsConverterTest {

    ParticipantsConverter participantsConverter = new ParticipantsConverter();

    @Test
    void convertToDatabaseColumnTest() {
        List<Long> list = List.of(1L);
        String result = participantsConverter.convertToDatabaseColumn(list);
        assertEquals("1", result);

        list = List.of();
        result = participantsConverter.convertToDatabaseColumn(list);
        assertEquals("", result);

        list = List.of(1L, 2L, 3L, 4L);
        result = participantsConverter.convertToDatabaseColumn(list);
        assertEquals("1,2,3,4", result);
    }

    @Test
    void convertToEntityAttributeTest() {
        String string = "";
        List<Long> list = participantsConverter.convertToEntityAttribute(string);
        assertTrue(list.isEmpty());

        string = "1";
        list = participantsConverter.convertToEntityAttribute(string);
        assertEquals(1, list.size());
        assertTrue(list.contains(Long.parseLong(string)));

        string = "1,2,3,4";
        list = participantsConverter.convertToEntityAttribute(string);
        assertEquals(4, list.size());
        assertTrue(list.contains(1L));
        assertTrue(list.contains(2L));
        assertTrue(list.contains(3L));
        assertTrue(list.contains(4L));
    }
}