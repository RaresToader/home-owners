package voting.domain;

import lombok.NoArgsConstructor;
import voting.db.converters.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "elections")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "election_type",
        discriminatorType = DiscriminatorType.INTEGER)
@NoArgsConstructor
public abstract class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    int electionId;

    private int hoaId;
    private String name;
    private String description;
    private int voteCount;
    private String status;

    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime scheduledFor;

    /**
     * Creates a new election, called exclusively by a subclass
     *
     * @param name         Name of election
     * @param description  Description of election
     * @param hoaId        Id of Hoa that it is a part of
     * @param scheduledFor Time object, when the election will start
     */
    public Election(String name, String description, int hoaId, LocalDateTime scheduledFor) {
        this.name = name;
        this.description = description;
        this.hoaId = hoaId;
        this.scheduledFor = scheduledFor;
        this.voteCount = 0;
        this.status = "scheduled";
    }

    /**
     * Checks if the member with the provided id can participate in the election
     *
     * @param memberId Id of member to consider
     * @return Whether the member can vote
     */
    private boolean canParticipate(Integer memberId) {
        return false;
    }

    /**
     * Allows for a member to vote on this election
     *
     * @param membershipId Id of member that votes
     * @param choice       Choice of member that voted
     */
    public void vote(int membershipId, int choice) {
    }

    /**
     * Concludes the current election
     *
     * @return Result of election
     */
    public Object conclude() {
        return null;
    }

    public int getElectionId() {
        return electionId;
    }

    public int getHoaId() {
        return hoaId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Election other = (Election) o;
        return other.electionId == this.electionId;
    }

    @Override
    public int hashCode() {
        return (this.electionId + this.hoaId + this.name.hashCode() + "").hashCode();
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }

    public String toString() {
        return "Election{"
                + "electionID='" + electionId
                + '\'' + ", hoaID='" + hoaId
                + '\'' + ", name='" + name
                + '\'' + ", description='" + description
                + '\'' + ", voteCount='" + voteCount
                + '\'' + ", time=" + scheduledFor.toString() + '}';
    }

}
