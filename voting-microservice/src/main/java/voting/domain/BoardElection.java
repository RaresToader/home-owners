package voting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import voting.domain.converter.CandidatesConverter;
import voting.domain.converter.VotesConverter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BoardElection extends Election {

    private int amountOfWinners;
    private String status;

    @Convert(converter = CandidatesConverter.class)
    private ArrayList<Integer> candidates;

    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="example_attributes", joinColumns=@JoinColumn(name="example_id"))
    //@Convert(converter = VotesConverter.class)
    private Map<Integer, Integer> votes;

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

    @Override
    public void vote(int membershipId, int voteChoice) {
        if (this.status.equals("ongoing")) {
            votes.put(membershipId, voteChoice);
            this.incrementVoteCount();
        }
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
