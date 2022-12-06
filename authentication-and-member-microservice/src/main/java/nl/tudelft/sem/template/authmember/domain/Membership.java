package nl.tudelft.sem.template.authmember.domain;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authmember.domain.converters.AddressConverter;

import javax.persistence.*;

/**
 * A DDD value object representing a membership, a relation between member and some hoa.
 */
@Entity
@Table(name = "memberships")
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO) //Automatically assign IDs
    @Column(name = "membershipid", nullable = false)
    private int membershipID;
    @Column(name = "memberid")
    private String memberID;
    @Column(name = "hoaid")

    private int hoaID;
    @Column(name = "address")
    @Lob
    @Convert(converter = AddressConverter.class)
    private Address address;
    @Column(name = "startTime")

    private LocalDateTime startTime;
    @Column(name = "duration")
    private LocalDateTime duration;
    @Column(name = "isBoard")
    private boolean isBoard;

    public Membership(String memberID, int hoaID, Address address, LocalDateTime startTime, LocalDateTime duration,
                      boolean isBoard) {
        this.memberID = memberID;
        this.hoaID = hoaID;
        this.address = address;
        this.startTime = startTime;
        this.duration = duration;
        this.isBoard = isBoard;
    }

    @Override
    public String toString() {
        return "Membership{" +
            "membershipID=" + membershipID +
            ", memberID='" + memberID + '\'' +
            ", hoaID=" + hoaID +
            ", address=" + address +
            ", startTime=" + startTime +
            ", duration=" + duration +
            ", isBoard=" + isBoard +
            '}';
    }

    public int getMembershipID() {
        return membershipID;
    }

    public String getMemberID() {
        return memberID;
    }

    public int getHoaID() {
        return hoaID;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getDuration() {
        return duration;
    }

    public boolean isBoard() {
        return isBoard;
    }

    public void setDuration(LocalDateTime time) {
        this.duration = time;
    }
}