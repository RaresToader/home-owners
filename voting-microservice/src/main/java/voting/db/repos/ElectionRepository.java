package voting.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import voting.domain.Election;
import voting.exceptions.ElectionDoesNotExist;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Integer> {
    Election findByElectionId(Integer electionId) throws ElectionDoesNotExist;

    boolean existsByHoaIdAndName(int hoaId, String name);

    @Query("SELECT u FROM BoardElection u WHERE u.hoaId = ?1")
    Election getBoardElectionByHoaId(int hoaId);

}
