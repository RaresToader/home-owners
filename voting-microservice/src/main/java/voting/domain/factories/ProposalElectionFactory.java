package voting.domain.factories;

import voting.domain.Election;
import voting.domain.Proposal;
import voting.models.ElectionModel;
import voting.models.ProposalModel;

import java.time.LocalDateTime;

public class ProposalElectionFactory extends ElectionFactory {

    public ProposalElectionFactory() {
        // Left empty intentionally
    }

    @Override
    public Election createElection(String name, String description, int hoaId, LocalDateTime scheduledFor) {
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
        return createElection(prop.name, prop.description, prop.hoaId, prop.scheduledFor.createDate());
    }
}