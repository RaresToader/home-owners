package nl.tudelft.sem.template.authmember.authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {

    private final transient JwtTokenVerifier jwtTokenVerifier;

    public AuthManager(JwtTokenVerifier jwtTokenVerifier) {
        this.jwtTokenVerifier = jwtTokenVerifier;
    }

    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void validateMember(String member) throws IllegalAccessException {
        if(!jwtTokenVerifier.validateToken(member)) {
            throw new IllegalAccessException();
        }
        if (!member.equals(getMemberId())) {
            throw new IllegalAccessException();
        }
    }
}
