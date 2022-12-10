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
    Optional<Membership> findByMembershipId(String membershipId);

    /**
     * Check if a membership exists.
     */
    boolean existsByMembershipId(String membershipId);

    List<Membership> findAllByMemberId(String membershipId);

    List<Membership> findAllByMemberIdAndHoaId(String memberId, int hoaId);

    /**
     * Finds active memberships.
     */
    List<Membership> findAllByMemberIdAndDurationIsNull(String memberId);

    Optional<Membership> findByMemberIdAndHoaIdAndDurationIsNull(String memberId, int hoaId);

}