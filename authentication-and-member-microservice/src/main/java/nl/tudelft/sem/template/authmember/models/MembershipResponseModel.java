package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipResponseModel {
    private long membershipId;
    private String memberId;
    private long hoaId;
    private String country;
    private String city;
    private boolean isBoard;
    private LocalDateTime startTime;
    private LocalDateTime duration;
}
