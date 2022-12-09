package nl.tudelft.sem.template.hoa.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Address;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipResponseModel {
    private int membershipId;
    private String memberId;
    private int hoaId;
    private Address address;
    private LocalDateTime startTime;
    private LocalDateTime duration;
    private boolean isBoard;
}
