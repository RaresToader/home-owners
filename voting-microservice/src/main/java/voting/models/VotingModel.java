package voting.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VotingModel {
    public final int electionId;
    public final String memberId;
    public final String choice;

    /**
     * Checks whether this model is a valid one for creating a vote
     *
     * @return Boolean to represent the model's validity
     */
    public boolean isValid() {
        return electionId >= 0
                && !choice.equals(memberId);
    }

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass() && this == o;
    }
}
