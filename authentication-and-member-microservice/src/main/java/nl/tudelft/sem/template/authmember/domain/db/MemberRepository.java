package nl.tudelft.sem.template.authmember.domain.db;

import java.util.Optional;
import nl.tudelft.sem.template.authmember.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for querying and persisting member aggregate roots.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    /**
     * Find user by memberId.
     */
    Optional<Member> findByMemberId(String memberId);

    /**
     * Check if an existing user already uses a memberId.
     */
    boolean existsByMemberId(String memberId);
}