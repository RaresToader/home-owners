package voting.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveVoteModel {

	public int electionId;
	public String memberId;

	/**
	 * Checks whether this model is a valid one for removing a vote
	 *
	 * @return Boolean to represent the model's validity
	 */
	public boolean isValid() {
		return electionId >= 0;
	}
}
