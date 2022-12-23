package nl.tudelft.sem.template.hoa.models;

import lombok.Data;

@Data
public class RemoveVoteModel {
	public int electionId;
	public String memberId;
}
