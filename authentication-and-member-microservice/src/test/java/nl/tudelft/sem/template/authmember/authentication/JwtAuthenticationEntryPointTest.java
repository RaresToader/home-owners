//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package nl.tudelft.sem.template.authmember.authentication;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationEntryPointTest {
    private transient JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient AuthenticationException dummyAuthenticationException;

    @BeforeEach
    public void setup() {
        this.mockRequest = (HttpServletRequest)Mockito.mock(HttpServletRequest.class);
        this.mockResponse = (HttpServletResponse)Mockito.mock(HttpServletResponse.class);
        this.dummyAuthenticationException = (AuthenticationException)Mockito.mock(AuthenticationException.class);
        this.jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    }

    @Test
    public void commenceTest() throws ServletException, IOException {
        this.jwtAuthenticationEntryPoint.commence(this.mockRequest, this.mockResponse, this.dummyAuthenticationException);
        Mockito.verifyNoInteractions(new Object[]{this.mockRequest});
        ((HttpServletResponse)Mockito.verify(this.mockResponse)).addHeader("WWW-Authenticate", "Bearer");
        ((HttpServletResponse)Mockito.verify(this.mockResponse)).sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        Mockito.verifyNoMoreInteractions(new Object[]{this.mockResponse});
    }
}
