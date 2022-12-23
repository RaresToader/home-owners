package voting.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VotingModel {
    public int electionId;
    public String memberId;
    public String choice;

    /**
     * Checks whether this model is a valid one for creating a vote
     *
     * @return Boolean to represent the model's validity
     */
    public boolean isValid() {
        return electionId >= 0
                && !choice.equals(memberId);
    }
}
