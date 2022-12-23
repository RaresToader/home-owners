//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package nl.tudelft.sem.template.authmember.authentication;

import nl.tudelft.sem.template.authmember.domain.Member;
import nl.tudelft.sem.template.authmember.domain.password.HashedPassword;
import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@DirtiesContext(
        classMode = ClassMode.BEFORE_EACH_TEST_METHOD
)
public class JwtUserDetailsServiceTest {
    @Autowired
    private transient JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private transient MemberRepository userRepository;

    private final transient String secret = "password123Hash";
    private final transient String member = "member1";

    @Test
    public void loadUserByUsername_withValidUser_returnsCorrectUser() {
        HashedPassword testHashedPassword = new HashedPassword(secret);
        Member appUser = new Member(member, testHashedPassword);
        this.userRepository.save(appUser);
        UserDetails actual = this.jwtUserDetailsService.loadUserByUsername(member);
        Assertions.assertThat(actual.getUsername()).isEqualTo(member);
        Assertions.assertThat(actual.getPassword()).isEqualTo(testHashedPassword.toString());
    }

    @Test
    public void loadUserByUsername_withNonexistentUser_throwsException() {
        Member appUser = new Member(member, new HashedPassword(secret));
        this.userRepository.save(appUser);
        ThrowingCallable action = () -> {
            this.jwtUserDetailsService.loadUserByUsername("member2");
        };
        Assertions.assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(action);
    }
}
