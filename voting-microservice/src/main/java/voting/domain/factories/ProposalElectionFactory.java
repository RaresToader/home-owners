package voting.domain.factories;

import voting.domain.Election;
import voting.domain.Proposal;
import voting.models.ElectionModel;
import voting.models.ProposalModel;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

public class ProposalElectionFactory extends ElectionFactory {

    public ProposalElectionFactory() {
        // Left empty intentionally
    }

    @Override
    public Election createElection(String name, String description, long hoaId, LocalDateTime scheduledFor) {
        return new Proposal(name, description, hoaId, scheduledFor);
    }

    /**
     * Generates a proposal election from given model
     *
     * @param model Model to use for generation
     * @return Created proposal, if model is valid and of proper subclass
     */
    @Override
    public Election createElection(ElectionModel model) {
        if (model.getClass() != ProposalModel.class || !model.isValid()) return null;
        ProposalModel prop = (ProposalModel) model;
        return createElection(prop.name, prop.description, prop.hoaId, prop.scheduledFor.createDate().plusWeeks(2));
    }

    /**
     * Generates a proposal election from given model and given time
     *
     * @param model Model to use for generation
     * @param startAfter Time after which the voting can start
     * @return Created proposal, if model is valid and of proper subclass
     */
    public Election createElection(ElectionModel model, TemporalAmount startAfter) {
        if (model.getClass() != ProposalModel.class || !model.isValid()) return null;
        ProposalModel prop = (ProposalModel) model;
        return createElection(prop.name, prop.description, prop.hoaId, prop.scheduledFor.createDate().plus(startAfter));
    }
}