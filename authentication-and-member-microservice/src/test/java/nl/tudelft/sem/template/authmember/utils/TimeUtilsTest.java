package nl.tudelft.sem.template.authmember.utils;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    @Test
    void absoluteDifference() {
        LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC);
        assertEquals(LocalDateTime.ofEpochSecond(2*3600,0, ZoneOffset.UTC),
                TimeUtils.absoluteDifference(start, start.plusHours(1)));
    }

    @Test
    void constructorTest() {
        TimeUtils t = new TimeUtils();
    }
}