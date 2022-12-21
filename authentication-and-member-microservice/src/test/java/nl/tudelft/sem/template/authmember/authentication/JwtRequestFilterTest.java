//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package nl.tudelft.sem.template.authmember.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.stream.Stream;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtRequestFilterTest {
    private transient JwtRequestFilter jwtRequestFilter;
    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient FilterChain mockFilterChain;
    private transient JwtTokenVerifier mockJwtTokenVerifier;
    private transient String token = "randomtoken123";
    private transient String user = "user123";
    private transient String auth = "Authorization";
    /**
     * Setup method
     */
    @BeforeEach
    public void setup() {
        this.mockRequest = (HttpServletRequest) Mockito.mock(HttpServletRequest.class);
        this.mockResponse = (HttpServletResponse) Mockito.mock(HttpServletResponse.class);
        this.mockFilterChain = (FilterChain) Mockito.mock(FilterChain.class);
        this.mockJwtTokenVerifier = (JwtTokenVerifier) Mockito.mock(JwtTokenVerifier.class);
        this.jwtRequestFilter = new JwtRequestFilter(this.mockJwtTokenVerifier);
        SecurityContextHolder.getContext().setAuthentication((Authentication) null);
    }

    @AfterEach
    public void assertChainContinues() throws ServletException, IOException {
        ((FilterChain) Mockito.verify(this.mockFilterChain)).doFilter(this.mockRequest, this.mockResponse);
        Mockito.verifyNoMoreInteractions(new Object[]{ this.mockFilterChain });
    }

    @Test
    public void correctToken() throws ServletException, IOException {
        Mockito.when(this.mockRequest.getHeader(auth)).thenReturn("Bearer " + token);
        Mockito.when(this.mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        Mockito.when(this.mockJwtTokenVerifier.getMemberIdFromToken(token)).thenReturn(user);
        this.jwtRequestFilter.doFilterInternal(this.mockRequest, this.mockResponse, this.mockFilterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(user);
    }

    @Test
    public void invalidToken() throws ServletException, IOException {
        Mockito.when(this.mockRequest.getHeader(auth)).thenReturn("Bearer " + token);
        Mockito.when(this.mockJwtTokenVerifier.validateToken(token)).thenReturn(false);
        Mockito.when(this.mockJwtTokenVerifier.getMemberIdFromToken(token)).thenReturn(user);
        this.jwtRequestFilter.doFilterInternal(this.mockRequest, this.mockResponse, this.mockFilterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * Parametrized test.
     */
    @ParameterizedTest
    @MethodSource({"tokenVerificationExceptionGenerator"})
    public void tokenVerificationException(Class<? extends Throwable> throwable) throws ServletException, IOException {
        Mockito.when(this.mockRequest.getHeader(auth)).thenReturn("Bearer " + token);
        Mockito.when(this.mockJwtTokenVerifier.validateToken(token)).thenThrow(throwable);
        Mockito.when(this.mockJwtTokenVerifier.getMemberIdFromToken(token)).thenReturn(user);
        this.jwtRequestFilter.doFilterInternal(this.mockRequest, this.mockResponse, this.mockFilterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private static Stream<Arguments> tokenVerificationExceptionGenerator() {
        return Stream.of(Arguments.of(new Object[]{ExpiredJwtException.class}),
                Arguments.of(new Object[]{IllegalArgumentException.class}), Arguments.of(new Object[]{JwtException.class}));
    }

    @Test
    public void nullToken() throws ServletException, IOException {
        Mockito.when(this.mockRequest.getHeader(auth)).thenReturn((String) null);
        this.jwtRequestFilter.doFilterInternal(this.mockRequest, this.mockResponse, this.mockFilterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void invalidPrefix() throws ServletException, IOException {
        Mockito.when(this.mockRequest.getHeader(auth)).thenReturn("Bearer1 " + token);
        Mockito.when(this.mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        Mockito.when(this.mockJwtTokenVerifier.getMemberIdFromToken(token)).thenReturn(user);
        this.jwtRequestFilter.doFilterInternal(this.mockRequest, this.mockResponse, this.mockFilterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void noPrefix() throws ServletException, IOException {
        Mockito.when(this.mockRequest.getHeader(auth)).thenReturn(token);
        Mockito.when(this.mockJwtTokenVerifier.validateToken(token)).thenReturn(true);
        Mockito.when(this.mockJwtTokenVerifier.getMemberIdFromToken(token)).thenReturn(user);
        this.jwtRequestFilter.doFilterInternal(this.mockRequest, this.mockResponse, this.mockFilterChain);
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
