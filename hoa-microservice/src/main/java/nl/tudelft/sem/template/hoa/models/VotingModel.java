package nl.tudelft.sem.template.hoa.models;

import lombok.Data;

@Data
public class VotingModel {
    public int electionId;
    public String memberId;
    public String choice;

}
