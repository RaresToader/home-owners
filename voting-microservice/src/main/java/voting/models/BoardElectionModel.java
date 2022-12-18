package voting.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = true)
public class BoardElectionModel extends ElectionModel {
    public int amountOfWinners;
    public ArrayList<Integer> candidates;

    /**
     * {@inheritDoc}
     */
    public boolean isValid() {
        return amountOfWinners > 0
                && candidates != null
                && candidates.size() > 0
                && super.isValid();
    }
}
