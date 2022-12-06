package nl.tudelft.sem.template.hoa.db;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.hoa.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository that will hold all of our activities.
 */
@Repository
public interface ActivityRepo extends JpaRepository<Activity, Long> {

    /**
     * Find activity by id.
     */
    Optional<Activity> findById(long activityId);

    /**
     * Find activity by hoaID.
     */
    Optional<List<Activity>> findByHoaId(long hoaId);
}
