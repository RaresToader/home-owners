package nl.tudelft.sem.template.authmember.domain.password;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A DDD service for hashing passwords.
 */
public class PasswordHashingService {

    private final transient PasswordEncoder encoder;

    public PasswordHashingService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public HashedPassword hash(String password) {
        return new HashedPassword(encoder.encode(password));
    }
}
