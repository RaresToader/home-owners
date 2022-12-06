package nl.tudelft.sem.template.hoa.models;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    private LocalDateTime activityTime;
    private LocalTime activityDuration;

}