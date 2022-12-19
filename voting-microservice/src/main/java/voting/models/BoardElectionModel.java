package voting.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BoardElectionModel extends ElectionModel {
    public int amountOfWinners;
    public List<Integer> candidates;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return amountOfWinners > 0
                && candidates != null
                && super.isValid();
    }
}
