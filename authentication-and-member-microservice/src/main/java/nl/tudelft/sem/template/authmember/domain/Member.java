package nl.tudelft.sem.template.authmember.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "members")
@NoArgsConstructor
public class Member {
    /**
     * Identifier for the application user.
     */
    @Id
    @Column(name = "memberid", nullable = false)
    private String memberId;

    //TODO: Not store plaintext passwords
    @Column(name = "password_hash", nullable = false)
    private String password;

    public Member(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
    }


    public String getMemberId() {
        return memberId;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        return memberId.equals(member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
