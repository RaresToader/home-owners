package voting.models;

import java.util.List;

public class BoardElectionModel extends ElectionModel {
    public final int amountOfWinners;
    public final List<String> candidates;

    /**
     * Default constructors for [de-]serializer
     */
    public BoardElectionModel() {
        amountOfWinners = 0;
        candidates = null;
    }

    /**
     * Creates a board election model from supermodel
     *
     * @param em              Election Model to be used for the board election model
     * @param amountOfWinners Amount of election winners
     * @param candidates      Candidates' memberIds of election
     */
    public BoardElectionModel(ElectionModel em, int amountOfWinners, List<String> candidates) {
        this(em.name, em.description, em.hoaId, em.scheduledFor, amountOfWinners, candidates);
    }

    /**
     * Creates a board election model from base fields
     *
     * @param name            Name of board election
     * @param description     Description of board election
     * @param hoaId           Id of associated hoa
     * @param scheduledFor    TimeModel to represent election's start time (voting time)
     * @param amountOfWinners Amount of election winners
     * @param candidates      Candidates' memberIds of election
     */
    public BoardElectionModel(String name, String description, long hoaId, TimeModel scheduledFor,
                              int amountOfWinners, List<String> candidates) {
        super(name, description, hoaId, scheduledFor);
        this.amountOfWinners = amountOfWinners;
        this.candidates = candidates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return amountOfWinners > 0
                && candidates != null
                && super.isValid();
    }
}
