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
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    /**
     * Find membership by id.
     */
    Optional<Membership> findByMembershipId(long membershipId);

    /**
     * Check if a membership exists.
     */
    boolean existsByMembershipId(long membershipId);

    List<Membership> findAllByMemberId(String membershipId);

    List<Membership> findAllByMemberIdAndHoaId(String memberId, long hoaId);

    /**
     * Finds active memberships.
     */
    List<Membership> findAllByMemberIdAndDurationIsNull(String memberId);

    List<Membership> findAllByDurationIsNull();

    Optional<Membership> findByMemberIdAndHoaIdAndDurationIsNull(String memberId, long hoaId);

}