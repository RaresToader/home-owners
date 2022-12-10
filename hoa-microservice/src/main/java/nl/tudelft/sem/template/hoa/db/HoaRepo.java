package nl.tudelft.sem.template.hoa.db;

import java.util.Optional;
import nl.tudelft.sem.template.hoa.domain.Hoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository that stores the associations.
 */
@Repository
public interface HoaRepo extends JpaRepository<Hoa, Long> {

    /**
     * Find association by id.
     */
    Optional<Hoa> findById(long hoaId);

}
