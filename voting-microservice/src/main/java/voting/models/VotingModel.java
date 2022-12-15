package voting.models;

import lombok.Data;

@Data
public class VotingModel {
    public int electionId;
    public int membershipId;
    public int choice;

    public boolean isValid() {
        return electionId >= 0 && membershipId >= 0;
    }
}
