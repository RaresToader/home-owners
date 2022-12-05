package voting.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private String name;
    private String description;
    private int voteCount;
    private Time scheduledFor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Election other = (Election) o;
        return other.electionId == this.electionId;
    }
    public void incrementVoteCount() {
        this.voteCount++;
    }
    private boolean canParticipate() {
        return false;
    }
    public void vote() {
    }
    public Object conclude() { return null; }
}
