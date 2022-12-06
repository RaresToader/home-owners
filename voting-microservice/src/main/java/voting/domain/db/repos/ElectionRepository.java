package voting.domain.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voting.domain.Election;
import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Integer> {
    Optional<Election> findByElectionId(Integer electionId);
    boolean existsByElectionId(Integer electionId);

}
