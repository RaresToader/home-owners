package nl.tudelft.sem.template.hoa.models;

import lombok.Data;

@Data
public class VotingModel {
    public int electionId;
    public int membershipId;
    public int choice;

}
