package nl.tudelft.sem.template.authmember.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authmember.domain.converters.AddressConverter;


/**
 * A DDD value object representing a membership, a relation between member and some hoa.
 */
@Entity
@Table(name = "memberships")
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //Automatically assign IDs
    @Column(name = "membershipid", nullable = false)
    private int membershipId;
    @Column(name = "memberid")
    private String memberId;
    @Column(name = "hoaid")

    private int hoaId;
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

    /**
     * Constructor for the membership class.
     *
     * @param memberId  the id of the member
     * @param hoaId     the hoa the member joined
     * @param address   the address of the member
     * @param startTime the start time
     * @param duration  the duration
     * @param isBoard   if the member is in the board
     */
    public Membership(String memberId, int hoaId, Address address, LocalDateTime startTime, LocalDateTime duration,
                      boolean isBoard) {
        this.memberId = memberId;
        this.hoaId = hoaId;
        this.address = address;
        this.startTime = startTime;
        this.duration = duration;
        this.isBoard = isBoard;
    }

    @Override
    public String toString() {
        return "Membership{"
                + "membershipID=" + membershipId + ", memberID='" + memberId
                + '\'' + ", hoaID=" + hoaId + ", address="
                + address + ", startTime=" + startTime + ", duration="
                + duration + ", isBoard=" + isBoard + '}';
    }

    public int getMembershipId() {
        return membershipId;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getHoaId() {
        return hoaId;
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

    public boolean isInBoard() {
        return isBoard;
    }

    public void setDuration(LocalDateTime time) {
        this.duration = time;
    }
}