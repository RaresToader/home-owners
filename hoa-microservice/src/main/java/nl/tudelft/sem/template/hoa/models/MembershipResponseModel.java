package nl.tudelft.sem.template.hoa.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipResponseModel {
    private long membershipId;
    private String memberId;
    private long hoaId;
    //private Address address;
    //private LocalDateTime startTime;
    //private LocalDateTime duration;
    private boolean isBoard;
}
