package nl.tudelft.sem.template.authmember.utils;

import java.time.Duration;
import java.time.LocalDateTime;

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
        return Duration.between(t1, t2);
    }
}
