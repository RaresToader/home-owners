package nl.tudelft.sem.template.hoa.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.hoa.annotations.Generated;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Generated
@Data
@AllArgsConstructor
public class TimeModel {
    public int seconds;
    public int minutes;
    public int hours;
    public int day;
    public int month;
    public int year;

    /**
     * Create a time model from an int array with all the required elements
     * (array follows ISO date format, starting from year and ending with nanoseconds)
     *
     * @param nums Integer array containing localdatetime values
     */
    public static TimeModel createModelFromArr(Integer[] nums) {
        return new TimeModel(nums[5], nums[4], nums[3], nums[2], nums[1], nums[0]);
    }

    public boolean isValid() {
        return createDate() != null;
    }

    /**
     * Returns a new LocalDateTime object based on this model's attributes
     *
     * @return new LocalDateTime object
     */
    public LocalDateTime createDate() {
        try {
            return LocalDateTime.of(LocalDate.of(this.year, this.month, this.day),
                    LocalTime.of(this.hours, this.minutes, this.seconds));
        } catch (DateTimeException e) {
            return null;
        }
    }
}
