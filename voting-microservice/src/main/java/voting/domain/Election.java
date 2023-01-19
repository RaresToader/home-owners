package voting.domain;

import lombok.NoArgsConstructor;
import voting.db.converters.LocalDateTimeConverter;
import voting.exceptions.CannotProceedVote;
import voting.exceptions.ThereIsNoVote;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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

    private long hoaId;
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
    public Election(String name, String description, long hoaId, LocalDateTime scheduledFor) {

        this.name = name;
        this.description = description;
        this.hoaId = hoaId;
        this.scheduledFor = scheduledFor;
        this.voteCount = 0;
        this.status = "scheduled";
    }

    /**
     * Allows for a member to vote on this election
     *
     * @param memberId Id of member that votes
     * @param choice       Choice of member that voted
     */
    public abstract void vote(String memberId, Object choice) throws CannotProceedVote;

    /**
     * Removes member's vote
     * @param memberId Id of member that wants to remove his vote
     * @throws ThereIsNoVote - member has not yet voted
     */
    public abstract void removeVote(String memberId) throws ThereIsNoVote;

    /**
     * Concludes the current election
     *
     * @return Result of election
     */
    public abstract Object conclude();

    public int getElectionId() {
        return electionId;
    }

    public long getHoaId() {
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

    // Use this method only for testing purposes!
    public void  setElectionId(int electionId) {
        this.electionId = electionId;
    }

    public void setHoaId(long hoaId) {
        this.hoaId = hoaId;
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
        if (this.getClass() != BoardElection.class)
            return ("prop:" + this.electionId + ":" + this.hoaId + ":" + this.name.hashCode()).hashCode();
        return ("be:" + this.electionId + ":" + this.hoaId + ":" + this.name.hashCode()).hashCode();
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }

    @Override
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
