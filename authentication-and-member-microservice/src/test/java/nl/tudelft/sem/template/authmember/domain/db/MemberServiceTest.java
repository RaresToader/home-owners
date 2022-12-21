package nl.tudelft.sem.template.authmember.domain.db;

import nl.tudelft.sem.template.authmember.domain.Membership;
import nl.tudelft.sem.template.authmember.domain.password.PasswordHashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    private MemberRepository memberRepository;
    private PasswordHashingService passwordHashingService;

    @BeforeEach
    void setup(){
//        membershipService = Mockito.mock(MembershipService.class);

    }

    @Test
    void registerUser() {
    }

    @Test
    void validateUsername() {
    }

    @Test
    void validatePassword() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void getMember() {
    }
}