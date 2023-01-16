package voting.models;

public class ProposalModel extends ElectionModel {

    /**
     * Create an ElectionModel object from base fields
     *
     * @param name         Name of election
     * @param description  Description of election
     * @param hoaId        id of associated hoa
     * @param scheduledFor TimeModel to represent an election's start time
     */
    public ProposalModel(String name, String description, long hoaId, TimeModel scheduledFor) {
        super(name, description, hoaId, scheduledFor);
    }
}
