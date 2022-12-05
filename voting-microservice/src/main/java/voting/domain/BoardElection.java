package voting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BoardElection extends Election {
    private int amountOfWinners;
    private String status;
    private ArrayList<Integer> candidates;
    private HashMap<String, Integer> votes;

    public BoardElection(String name, String description, int hoaId, Time scheduledFor, int amountOfWinners,
                         ArrayList<Integer> candidates) {
        super(name, description, hoaId, scheduledFor);
        this.amountOfWinners = amountOfWinners;
        this.candidates = candidates;
        status = "scheduled";
        votes = new HashMap<>();
    }
    private boolean canParticipate(String memberId) {
        return true;
    }
    public void vote(String memberId, int voteChoice) {
        votes.put(memberId, voteChoice);
        this.incrementVoteCount();
    }
    public List<Integer> findOutcome() {
        return votes.values()
                .stream()
                .sorted(Collections.reverseOrder())
                .limit(Math.min(amountOfWinners, candidates.size()))
                .collect(Collectors.toList());
    }
    public List<Integer> conclude() {
        List<Integer> currentWinners = findOutcome();
        status = "finished";
        return currentWinners;
    }
}
