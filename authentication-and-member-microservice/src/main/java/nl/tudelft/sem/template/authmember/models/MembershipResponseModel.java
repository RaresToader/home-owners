package nl.tudelft.sem.template.authmember.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authmember.domain.Address;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipResponseModel {
    private long membershipId;
    private String memberId;
    private long hoaId;
    private String country;
    private String city;
    //private LocalDateTime startTime;
    //private LocalDateTime duration;
    private boolean isBoard;
}
