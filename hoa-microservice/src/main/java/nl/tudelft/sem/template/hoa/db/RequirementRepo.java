package nl.tudelft.sem.template.hoa.db;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.domain.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * The repository that will hold all of our requirements.
 */
@Repository
public interface RequirementRepo extends JpaRepository<Requirement, Long> {

    /**
     * Find requirement by id.
     */
    Optional<Requirement> findById(long reqId);

    /**
     * Find requirements by hoaID.
     */
    Optional<List<Requirement>> findByHoaId(long hoaId);
}
