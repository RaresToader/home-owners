package nl.tudelft.sem.template.hoa.utils;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A library to work with LocalDateTime objects.
 */
public class TimeUtils {

    /**
     * Returns the time difference between two LocalDateTime objects, as a new LDT object.
     *
     * @return LDT time since epoch equal to the amount of time in the difference
     */
    public static Duration absoluteDifference(LocalDateTime t1, LocalDateTime t2) {
        return Duration.between(t1, t2).abs();
    }

    public static Duration sum(Duration t1, Duration t2) {
        return Duration.ofSeconds(t1.getSeconds() + t2.getSeconds());
    }

    public static long yearsToSeconds(long y) {
        return 31557600L * y;
    }

    public static LocalDateTime getFirstEpochDate() {
        return Instant.ofEpochSecond(0L).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
