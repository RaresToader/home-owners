package nl.tudelft.sem.template.hoa.utils;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
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
    public static LocalDateTime absoluteDifference(LocalDateTime t1, LocalDateTime t2) {
        long milis = Duration.between(t1, t2).abs().toMillis();
        return Instant.ofEpochMilli(milis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime sum(LocalDateTime t1, LocalDateTime t2) {
        long time = seconds(t1) + seconds(t2);
        return Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static long seconds(LocalDateTime t) {
        return t.toEpochSecond(OffsetDateTime.now().getOffset());
    }

    public static long yearsToSeconds(long y) {
        return 31557600L * y;
    }

    public static LocalDateTime getFirstEpochDate() {
        return Instant.ofEpochSecond(0L).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime dateFromYearsSinceEpoch(int y) {
        return Instant.ofEpochSecond(yearsToSeconds(y)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
