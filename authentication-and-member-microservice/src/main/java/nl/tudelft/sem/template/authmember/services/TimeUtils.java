package nl.tudelft.sem.template.authmember.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * A library to work with LocalDateTime objects
 */
public class TimeUtils {

    /**
     * Returns the time difference between two LocalDateTime objects, as a new LDT object.
     * @return LDT time since epoch equal to the amount of time in the difference
     */
    public static LocalDateTime absoluteDifference(LocalDateTime t1, LocalDateTime t2) {
        long milis = Duration.between(t1, t2).abs().toMillis();
        return Instant.ofEpochMilli(milis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
