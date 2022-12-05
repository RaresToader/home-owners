package voting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.util.HashMap;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Proposal extends Election {
    private boolean winningChoice;
    private String status;
    private HashMap<String, Boolean> votes;

    private boolean canParticipate(String memberId) {
        return true;
    }
    public void vote(String memberId, boolean vote) {
        if (status.equals("ongoing") && canParticipate(memberId)) {
            votes.put(memberId, vote);
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


