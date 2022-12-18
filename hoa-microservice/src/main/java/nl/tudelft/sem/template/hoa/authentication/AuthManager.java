package nl.tudelft.sem.template.hoa.authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {

    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getMemberId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void validateMember(String member) throws IllegalAccessException {
        if (!member.equals(getMemberId())) {
            throw new IllegalAccessException();
        }
    }
}
