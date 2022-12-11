package nl.tudelft.sem.template.hoa.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequestModel {
    private String activityName;
    private String activityDescription;
    private Long hoaId;
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime activityTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalTime activityDuration;

}