package nl.tudelft.sem.template.authmember.domain.db;

import java.util.Optional;

import nl.tudelft.sem.template.authmember.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting member aggregate roots.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    /**
     * Find user by NetID.
     */
    Optional<Member> findByMemberID(String memberID);

    /**
     * Check if an existing user already uses a NetID.
     */
    boolean existsByMemberID(String memberID);
}