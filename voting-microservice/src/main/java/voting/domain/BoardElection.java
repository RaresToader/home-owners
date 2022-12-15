package voting.domain;

import lombok.NoArgsConstructor;
import voting.db.converters.VotesConverter;
import voting.db.converters.CandidatesConverter;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@DiscriminatorValue("0")
public class BoardElection extends Election {

    private int amountOfWinners;

    @Convert(converter = CandidatesConverter.class)
    private List<Integer> candidates;

    @Convert(converter = VotesConverter.class)
    private HashMap<Integer, Integer> votes;

    /**
     * Create a board election
     *
     * @param name            Name of board election
     * @param description     Description of board election
     * @param hoaId           hoaID the board election refers to
     * @param scheduledFor    LocalDateTime object, when the election will start
     * @param amountOfWinners Amount of winners for this board election
     * @param candidates      List of member ids of board candidates
     */
    public BoardElection(String name, String description, int hoaId, LocalDateTime scheduledFor, int amountOfWinners,
                         ArrayList<Integer> candidates) {
        super(name, description, hoaId, scheduledFor);
        this.amountOfWinners = amountOfWinners;
        this.candidates = candidates;
        votes = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    private boolean canParticipate(Integer memberId) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vote(int membershipId, int voteChoice) {
        if (getStatus().equals("ongoing") && canParticipate(membershipId)) {
            votes.put(membershipId, voteChoice);
            this.incrementVoteCount();
        }
    }

    /**
     * Calculates the outcome of a board election
     *
     * @return List of winners, capped if less than amountOfWinners
     */
    public List<Integer> findOutcome() {
        return votes.values()
                .stream()
                .sorted(Collections.reverseOrder())
                .limit(Math.min(amountOfWinners, candidates.size()))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> conclude() {
        List<Integer> currentWinners = findOutcome();
        this.setStatus("finished");
        return currentWinners;
    }
}
