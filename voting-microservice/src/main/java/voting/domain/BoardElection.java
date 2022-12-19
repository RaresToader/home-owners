package voting.domain;

import lombok.NoArgsConstructor;
import voting.db.converters.VotesConverter;
import voting.db.converters.CandidatesConverter;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@DiscriminatorValue("0")
public class BoardElection extends Election {

    private int amountOfWinners;

    @Convert(converter = CandidatesConverter.class)
    private List<String> candidates;

    @Convert(converter = VotesConverter.class)
    private Map<Integer, Integer> votes;

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
                         List<String> candidates) {
        super(name, description, hoaId, scheduledFor);
        this.amountOfWinners = amountOfWinners;
        this.candidates = candidates;
        votes = new HashMap<>();
    }

    public int getAmountOfWinners() {
        return amountOfWinners;
    }

    public void setAmountOfWinners(int amountOfWinners) {
        this.amountOfWinners = amountOfWinners;
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<String> candidates) {
        this.candidates = candidates;
    }

    public Map<Integer, Integer> getVotes() {
        return votes;
    }

    public void setVotes(Map<Integer, Integer> votes) {
        this.votes = votes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vote(int membershipId, int voteChoice) {
        if (getStatus().equals("ongoing") && candidates.contains(voteChoice)) {
            votes.put(membershipId, voteChoice);
            this.incrementVoteCount();
        }
    }

    /**
     * Calculates the outcome of a board election
     *
     * @return Set of winners, capped if less than amountOfWinners
     */
    public Set<Integer> findOutcome() {
        var votesByCandidate = votes.entrySet().stream()
                // Map candidateIds (values) to keys, and vote counts to values
                .collect(Collectors.toMap(Map.Entry::getValue,
                        e -> votes.values().stream().filter(ee -> ee.equals(e.getValue())).count(),
                        Long::sum));
        return votesByCandidate.entrySet()
                .stream()
                // Master sort by number of votes, candidate application order as a tie-breaker
                .sorted(Comparator.comparing(i -> candidates.indexOf(i.getKey())))
                .sorted(Comparator.comparing(i -> -i.getValue()))
                .limit(Math.min(amountOfWinners, candidates.size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> conclude() {
        Set<Integer> currentWinners = findOutcome();
        this.setStatus("finished");
        return currentWinners;
    }

    public void addParticipant(String memberId) {
        this.candidates.add(memberId);
    }

    public boolean removeParticipant(String memberId) {
        return this.candidates.remove(memberId);
    }
}
