package voting.domain.factories;

import voting.domain.BoardElection;
import voting.domain.Election;
import voting.models.BoardElectionModel;
import voting.models.ElectionModel;

import java.time.LocalDateTime;
import java.util.List;

public class BoardElectionFactory extends ElectionFactory {

    public BoardElectionFactory() {
        // Left empty intentionally
    }

    /**
     * Generates an election from base fields
     *
     * @param name         Name of election
     * @param description  Description of election
     * @param hoaId        Id of associated hoa
     * @param scheduledFor LocalDateTime to represent election's start time
     * @return Created board election with provided fields
     */
    @Override
    public Election createElection(String name, String description, long hoaId, LocalDateTime scheduledFor) {
        return new BoardElection(name, description, hoaId, scheduledFor, 0, List.of());
    }

    /**
     * Complete generator for board elections
     *
     * @param model           ElectionModel to use for election creation
     * @param amountOfWinners Amount of winners of the election
     * @param candidates      Board candidates of the election
     * @return Created board election with provided fields
     */
    public Election createElection(ElectionModel model, int amountOfWinners, List<String> candidates) {
        return new BoardElection(model.name, model.description, model.hoaId, model.scheduledFor.createDate(),
                amountOfWinners, candidates);
    }

    /**
     * Generates a board election from given model
     *
     * @param model Model to use for generation
     * @return Created board election, if model is valid and of proper subclass
     */
    @Override
    public Election createElection(ElectionModel model) {
        if (model.getClass() != BoardElectionModel.class || !model.isValid()) return null;
        BoardElectionModel beModel = (BoardElectionModel) model;
        BoardElection be = (BoardElection) createElection(beModel.name, beModel.description, beModel.hoaId,
                beModel.scheduledFor.createDate());
        be.setAmountOfWinners(beModel.amountOfWinners);
        be.setCandidates(beModel.candidates);
        return be;
    }
}
