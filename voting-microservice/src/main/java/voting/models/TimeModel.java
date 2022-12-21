package voting.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimeModel {
    public int seconds;
    public int minutes;
    public int hours;
    public int day;
    public int month;
    public int year;

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
