package voting.domain.factories;

import voting.domain.Election;
import voting.models.ElectionModel;

import java.time.LocalDateTime;

public abstract class ElectionFactory {

    protected ElectionFactory() {
        // This constructor cannot be called, hence it is left empty.
    }

    public abstract Election createElection(String name, String description,
                                            int hoaId, LocalDateTime scheduledFor);

    public abstract Election createElection(ElectionModel model);
}
