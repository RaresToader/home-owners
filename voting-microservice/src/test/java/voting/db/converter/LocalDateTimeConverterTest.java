package voting.db.converter;

import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.converters.LocalDateTimeConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class LocalDateTimeConverterTest {

    LocalDateTimeConverter sut = new LocalDateTimeConverter();

    @Test
    void convertToDatabaseColumnTest() {
        LocalDateTime ldt = null;
        Timestamp res = sut.convertToDatabaseColumn(ldt);
        assertNull(res);

        ldt = LocalDateTime.of(LocalDate.of(1010, 10, 10),
                LocalTime.of(10, 10, 10));
        res = sut.convertToDatabaseColumn(ldt);
        assertEquals("1010-10-10 10:10:10.0", res.toString());
    }

    @Test
    void convertToEntityAttributeTest() {
        Timestamp ts = null;
        LocalDateTime ldt = sut.convertToEntityAttribute(ts);
        assertNull(ldt);

        //"2010-11-12 14:14:15.0 UTC +1";
        ts = new Timestamp(1_289_567_655_000L);
        ldt = sut.convertToEntityAttribute(ts);
        assertEquals(15, ldt.getSecond());
        assertEquals(14, ldt.getMinute());
        //assertEquals(13, ldt.getHour());
        assertEquals(12, ldt.getDayOfMonth());
        assertEquals(11, ldt.getMonthValue());
        assertEquals(2010, ldt.getYear());
    }
}
