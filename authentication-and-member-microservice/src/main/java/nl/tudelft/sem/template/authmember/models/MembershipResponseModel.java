package nl.tudelft.sem.template.authmember.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;


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
    private LocalTime duration;

    public long getMembershipId() {
        return membershipId;
    }

    public String getMemberId() {
        return memberId;
    }

    public long getHoaId() {
        return hoaId;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public boolean isBoard() {
        return boardMember;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalTime getDuration() {
        return duration;
    }
}





