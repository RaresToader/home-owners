package nl.tudelft.sem.template.authmember.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;


class TimeUtilsTest {

    @Test
    void absoluteDifference() {
        LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC);
        assertEquals(Duration.ofHours(1),
                TimeUtils.absoluteDifference(start, start.plusHours(1)));
    }

    @Test
    void constructorTest() {
        new TimeUtils();
    }
}