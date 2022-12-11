package voting.domain;

import lombok.NoArgsConstructor;
import voting.domain.converter.VotesConverter;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.sql.Time;
import java.util.HashMap;

@Entity
@DiscriminatorValue("1")
@NoArgsConstructor
public class Proposal extends Election {
    private boolean winningChoice;
    private String status;

    @Convert(converter = VotesConverter.class)
    private HashMap<Integer, Boolean> votes;

    /**
     * Creates a proposal
     *
     * @param name         Name of proposal
     * @param description  Description of proposal
     * @param hoaId        hoaID the proposal refers to
     * @param scheduledFor Time object, when the election will start
     */
    public Proposal(String name, String description, int hoaId, Time scheduledFor) {
        super(name, description, hoaId, scheduledFor);
        winningChoice = false;
        status = "scheduled";
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
    public void vote(int membershipId, boolean vote) {
        if (status.equals("ongoing") && canParticipate(membershipId)) {
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
        Integer[] counts = votes.values().stream()
                .collect(() -> new Integer[2],
                        (acc, b) -> {
                            if (b) acc[1]++;
                            else acc[0]++;
                        },
                        (acc, acc2) -> {
                            acc[0] += acc2[0];
                            acc[1] += acc2[1];
                        });
        return counts[0] <= counts[1];
    }

    /**
     * {@inheritDoc}
     */
    public Boolean conclude() {
        winningChoice = findOutcome();
        status = "finished";
        return winningChoice;
    }
}


