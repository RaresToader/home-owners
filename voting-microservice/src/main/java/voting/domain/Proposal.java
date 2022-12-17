package voting.domain;

import lombok.NoArgsConstructor;
import voting.db.converters.VotesConverter;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.HashMap;

@Entity
@DiscriminatorValue("1")
@NoArgsConstructor
public class Proposal extends Election {
    private boolean winningChoice;

    @Convert(converter = VotesConverter.class)
    private HashMap<Integer, Integer> votes;

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

    public HashMap<Integer, Integer> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<Integer, Integer> votes) {
        this.votes = votes;
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
    public void vote(int membershipId, int vote) {
        if (getStatus().equals("ongoing") && canParticipate(membershipId)) {
            votes.put(membershipId, vote);
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
                    boolean positive = b == 1;
                    if (positive) acc[1]++;
                    else acc[0]++;
                },
                (acc, acc2) -> {
                    acc[0] += acc2[0];
                    acc[1] += acc2[1];
                });
        return counts[0] < counts[1];
    }

    /**
     * {@inheritDoc}
     */
    public Boolean conclude() {
        winningChoice = findOutcome();
        setStatus("finished");
        return winningChoice;
    }
}


