package voting.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.sql.Time;

@Entity
@Data
@Table(name = "elections")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public abstract class Election {

    @Id
    @Column(name = "electionId", nullable = false)
    int electionId;
    int hoaId;
    private String name;
    private String description;
    private int voteCount;
    private Time scheduledFor;

    public Election(String name, String description, int hoaId, Time scheduledFor) {
        this.name = name;
        this.description = description;
        this.hoaId = hoaId;
        this.scheduledFor = scheduledFor;
        this.voteCount = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Election other = (Election) o;
        return other.electionId == this.electionId;
    }

    public int getElectionId() {
        return electionId;
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }
    public void vote(int membershipId, int choice) {
    }
    public Object conclude() { return null; }
}
