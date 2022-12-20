package nl.tudelft.sem.template.authmember.authentication;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;


class AuthManagerTest {
    private transient AuthManager authManager;
    private transient SecurityContextHolder mockSecurityContextHolder;
    private transient SecurityContext mockSecurityContext;
    private transient Authentication mockAuthentication;

    private String memberId = "john_doe";

    @BeforeEach
    public void setup() {
        this.mockSecurityContextHolder = (SecurityContextHolder)Mockito.mock(SecurityContextHolder.class);
        this.mockSecurityContext = (SecurityContext)Mockito.mock(SecurityContext.class);
        this.mockAuthentication = (Authentication)Mockito.mock(Authentication.class);
        Mockito.when(this.mockSecurityContextHolder.getContext()).thenReturn(this.mockSecurityContext);
        Mockito.when(this.mockSecurityContext.getAuthentication()).thenReturn(this.mockAuthentication);
        Mockito.when(this.mockAuthentication.getName()).thenReturn(this.memberId);


        this.authManager = new AuthManager();
    }

    @Test
    void getMemberId() {
//        Assertions.assertThat(authManager.getMemberId()).isEqualTo(this.memberId);
    }

    @Test
    void validateMember() {
//        assertThrows(IllegalAccessException.class, () -> {
//            authManager.validateMember("not_" + this.memberId);
//        });
    }
}