package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class MembershipResponseModel {
    private long membershipId;
    private String memberId;
    private long hoaId;
    private String country;
    private String city;
    private boolean boardMember;
    private LocalDateTime startTime;
    private Duration duration;
}





