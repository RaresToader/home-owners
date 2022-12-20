package voting.domain;

import lombok.NoArgsConstructor;
import voting.annotations.Generated;
import voting.db.converters.ProposalVotesConverter;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@DiscriminatorValue("1")
@NoArgsConstructor
public class Proposal extends Election {
    private boolean winningChoice;

    @Convert(converter = ProposalVotesConverter.class)
    private Map<String, Boolean> votes;

    /**
     * Creates a proposal
     *
     * @param name         Name of proposal
     * @param description  Description of proposal
     * @param hoaId        hoaID the proposal refers to
     * @param scheduledFor Time object, when the election will start
     */
    public Proposal(String name, String description, int hoaId, LocalDateTime scheduledFor) {
        super(name, description, hoaId, scheduledFor);
        winningChoice = false;
        votes = new HashMap<>();
    }

    public boolean isWinningChoice() {
        return winningChoice;
    }

    public void setWinningChoice(boolean winningChoice) {
        this.winningChoice = winningChoice;
    }

    public Map<String, Boolean> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, Boolean> votes) {
        this.votes = votes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vote(String memberId, Object vote) {
        if (getStatus().equals("ongoing")) {
            votes.put(memberId, (Boolean) vote);
            this.incrementVoteCount();
        }
    }

    /**
     * Calculates the outcome of a proposal
     *
     * @return Binary decision, based on majority voting
     */
    public boolean findOutcome() {
        Integer[] counts = votes.values().stream().collect(() -> new Integer[]{0, 0},
                (acc, b) -> {
                    // PMD thinks it's smelly if boolean is not stored in a variable
                    // ?????
                    if (b) acc[1]++;
                    else acc[0]++;
                }, this::findOutcomeAccHelper);
        return counts[0] < counts[1];
    }

    /**
     * Accumulator helper function made for testability
     * Combines all sub-acc values to master acc
     *
     * @param a Master accumulator
     * @param b Sub-accumulator
     */
    @Generated
    public void findOutcomeAccHelper(Integer[] a, Integer[] b) {
        a[0] += b[0];
        a[1] += b[1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean conclude() {
        winningChoice = findOutcome();
        setStatus("finished");
        return winningChoice;
    }
}


