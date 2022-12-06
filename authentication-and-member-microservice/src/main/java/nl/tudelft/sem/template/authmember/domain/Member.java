package nl.tudelft.sem.template.authmember.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.authmember.domain.converters.AddressConverter;

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
    private String memberID;

    //TODO: Not store plaintext passwords
    @Column(name = "password_hash", nullable = false)
    private String password;

    public Member(String memberID, String password) {
        this.memberID = memberID;
        this.password = password;
    }


    public String getMemberID() {
        return memberID;
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
        return memberID.equals(member.memberID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberID);
    }
}
