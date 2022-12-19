package voting.models;

import lombok.Data;

@Data
public class VotingModel {
    public int electionId;
    public int membershipId;
    public int choice;

    /**
     * Checks whether this model is a valid one for creating a vote
     *
     * @return Boolean to represent the model's validity
     */
    public boolean isValid() {
        return electionId >= 0
                && membershipId >= 0
                && choice >= 0;
    }
}
