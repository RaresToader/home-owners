package voting.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import voting.domain.Election;

import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Integer> {

    Optional<Election> findByElectionId(Integer electionId);

    boolean existsByHoaIdAndName(long hoaId, String name);

    @Query("SELECT u FROM BoardElection u WHERE u.hoaId = ?1 AND u.status LIKE 'ongoing'")
    Optional<Election> getBoardElectionByHoaId(int hoaId);

}
