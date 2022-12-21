package nl.tudelft.sem.template.authmember.utils;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A library to work with LocalDateTime objects.
 */
public class TimeUtils {

    /**
     * Returns the time difference between two LocalDateTime objects, as a new LDT object.
     *
     * @return LDT time since epoch equal to the amount of time in the difference
     */
    public static LocalTime absoluteDifference(LocalDateTime t1, LocalDateTime t2) {
        Duration duration = Duration.between(t1, t2);
        long seconds = Math.abs(duration.getSeconds());
        long absSeconds = seconds % 60;
        long absMinutes = (seconds / 60) % 60;
        long absHours = (seconds / (60 * 60)) % 24;
        return LocalTime.of(
                (int) absHours, (int) absMinutes, (int) absSeconds);
    }
}
