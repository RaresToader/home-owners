package voting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.sql.Time;
import java.util.HashMap;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Proposal extends Election {
    private boolean winningChoice;
    private String status;
    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="example_attributes", joinColumns=@JoinColumn(name="example_id"))
    private HashMap<Integer, Boolean> votes;

    public Proposal(String name, String description, int hoaId, Time scheduledFor) {
        super(name, description, hoaId, scheduledFor);
        winningChoice = false;
        status = "scheduled";
        votes = new HashMap<>();
    }

    public void vote(int membershipId, boolean vote) {
        if (status.equals("ongoing")) {
            votes.put(membershipId, vote);
            this.incrementVoteCount();
        }
    }
    public boolean findOutcome() {
        Integer[] counts = votes.values().stream()
                .collect(() -> new Integer[2],
                        (acc, b) -> {if (b) acc[1]++; else acc[0]++;},
                        (acc, acc2) -> {acc[0] += acc2[0]; acc[1] += acc2[1];});
        return counts[0] <= counts[1];
    }
    public Boolean conclude() {
        winningChoice = findOutcome();
        status = "finished";
        return winningChoice;
    }
}


