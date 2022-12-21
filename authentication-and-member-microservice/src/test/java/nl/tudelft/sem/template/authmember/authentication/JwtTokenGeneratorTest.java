//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package nl.tudelft.sem.template.authmember.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import nl.tudelft.sem.template.authmember.domain.providers.TimeProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtTokenGeneratorTest {
    private transient JwtTokenGenerator jwtTokenGenerator;
    private transient TimeProvider timeProvider;
    private transient Instant mockedTime = Instant.parse("2021-12-31T13:25:34.00Z");
    private final String secret = "testSecret123";
    private String netId = "andy";
    private UserDetails user;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        this.timeProvider = (TimeProvider)Mockito.mock(TimeProvider.class);
        Mockito.when(this.timeProvider.getCurrentTime()).thenReturn(this.mockedTime);
        this.jwtTokenGenerator = new JwtTokenGenerator(this.timeProvider);
        this.injectSecret("testSecret123");
        this.user = new User(this.netId, "someHash", new ArrayList());
    }

    @Test
    public void generatedTokenHasCorrectIssuanceDate() {
        String token = this.jwtTokenGenerator.generateToken(this.user);
        Claims claims = this.getClaims(token);
        Assertions.assertThat(claims.getIssuedAt()).isEqualTo(this.mockedTime.toString());
    }

    @Test
    public void generatedTokenHasCorrectExpirationDate() {
        String token = this.jwtTokenGenerator.generateToken(this.user);
        Claims claims = this.getClaims(token);
        Assertions.assertThat(claims.getExpiration()).isEqualTo(this.mockedTime.plus(1L, ChronoUnit.DAYS).toString());
    }

    @Test
    public void generatedTokenHasCorrectNetId() {
        String token = this.jwtTokenGenerator.generateToken(this.user);
        Claims claims = this.getClaims(token);
        Assertions.assertThat(claims.getSubject()).isEqualTo(this.netId);
    }

    private Claims getClaims(String token) {
        return (Claims)Jwts.parser().setAllowedClockSkewSeconds(2147483647L).setSigningKey("testSecret123").parseClaimsJws(token).getBody();
    }

    private void injectSecret(String secret) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = this.jwtTokenGenerator.getClass().getDeclaredField("jwtSecret");
        declaredField.setAccessible(true);
        declaredField.set(this.jwtTokenGenerator, secret);
    }
}
