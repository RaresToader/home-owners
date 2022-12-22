package nl.tudelft.sem.template.hoa.authentication;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthManagerTest {
    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        this.authManager = new AuthManager();
    }

    @Test
    public void getMemberIdTest() {
        String expected = "user123";
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(expected, (Object) null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        String actual = this.authManager.getMemberId();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void validateMemberTest() throws IllegalAccessException {
        String expected = "user123";
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(expected, (Object) null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        Assertions.assertThat(this.authManager.validateMember(expected)).isTrue();
    }

    @Test
    public void validateMemberTestFalse() throws IllegalAccessException {
        String expected = "user123";
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(expected, (Object) null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        assertThrows(IllegalAccessException.class, () -> {
            this.authManager.validateMember("user_not_123");
        });
    }
}