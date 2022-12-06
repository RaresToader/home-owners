package nl.tudelft.sem.template.authmember.domain.db;

import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.authmember.domain.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting member aggregate roots.
 */
@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {
    /**
     * Find membership by id.
     */
    Optional<Membership> findByMembershipID(String membershipID);

    /**
     * Check if a membership exists.
     */
    boolean existsByMembershipID(String membershipID);

    List<Membership> findAllByMemberID(String membershipID);

    List<Membership> findAllByMemberIDAndHoaID(String memberID, int hoaID);

    /**
     * Finds active memberships
     */
    List<Membership> findAllByMemberIDAndDurationIsNull(String memberID);
    Optional<Membership> findByMemberIDAndHoaIDAndDurationIsNull(String memberID, int hoaID);

}