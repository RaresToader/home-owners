package voting.models;

import lombok.AllArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
public class TimeModel {
    public final int seconds;
    public final int minutes;
    public final int hours;
    public final int day;
    public final int month;
    public final int year;

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

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass() && this == o;
    }
}
