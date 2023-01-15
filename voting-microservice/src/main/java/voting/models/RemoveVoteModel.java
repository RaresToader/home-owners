package voting.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemoveVoteModel {

	public final int electionId;
	public final String memberId;

	/**
	 * Checks whether this model is a valid one for removing a vote
	 *
	 * @return Boolean to represent the model's validity
	 */
	public boolean isValid() {
		return electionId >= 0;
	}

	@Override
	public boolean equals(Object o) {
		return this.getClass() == o.getClass() && this == o;
	}
}
