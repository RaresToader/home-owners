package nl.tudelft.sem.template.hoa.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtTokenVerifierTest {
    private transient JwtTokenVerifier jwtTokenVerifier;
    private final transient String secret = "testSecret123";
    private final transient String user = "user123";

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.injectSecret(secret);
    }

    @Test
    public void validateNonExpiredToken() {
        String token = this.generateToken(secret, user, -10000000L, 10000000L);
        boolean actual = this.jwtTokenVerifier.validateToken(token);
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    public void validateExpiredToken() {
        String token = this.generateToken(secret, user, -10000000L, -5000000L);
        ThrowingCallable action = () -> {
            this.jwtTokenVerifier.validateToken(token);
        };
        Assertions.assertThatExceptionOfType(ExpiredJwtException.class).isThrownBy(action);
    }

    @Test
    public void validateTokenIncorrectSignature() {
        String token = this.generateToken("incorrectSecret", user, -10000000L, 10000000L);
        ThrowingCallable action = () -> {
            this.jwtTokenVerifier.validateToken(token);
        };
        Assertions.assertThatExceptionOfType(SignatureException.class).isThrownBy(action);
    }

    @Test
    public void validateMalformedToken() {
        String token = "malformedtoken";
        ThrowingCallable action = () -> {
            this.jwtTokenVerifier.validateToken(token);
        };
        Assertions.assertThatExceptionOfType(MalformedJwtException.class).isThrownBy(action);
    }

    @Test
    public void parseNetid() {
        String expected = user;
        String token = this.generateToken(secret, expected, -10000000L, 10000000L);
        String actual = this.jwtTokenVerifier.getMemberIdFromToken(token);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    private String generateToken(String jwtSecret, String netid, long issuanceOffset, long expirationOffset) {
        Map<String, Object> claims = new HashMap();
        return Jwts.builder().setClaims(claims).setSubject(netid)
                .setIssuedAt(new Date(System.currentTimeMillis() + issuanceOffset))
                .setExpiration(new Date(System.currentTimeMillis() + expirationOffset))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = this.jwtTokenVerifier.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(this.jwtTokenVerifier, secret);
    }
}