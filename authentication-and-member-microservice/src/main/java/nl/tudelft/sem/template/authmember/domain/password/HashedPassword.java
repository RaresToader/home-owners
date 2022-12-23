package nl.tudelft.sem.template.authmember.domain.password;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a hashed password in our domain.
 */
@EqualsAndHashCode
@JsonSerialize(using = HashedPasswordSerializer.class)
public class HashedPassword {
    private final transient String hash;

    public HashedPassword(String hash) {
        // Validate input
        this.hash = hash;
    }

    @Override
    public String toString() {
        return hash;
    }
}
