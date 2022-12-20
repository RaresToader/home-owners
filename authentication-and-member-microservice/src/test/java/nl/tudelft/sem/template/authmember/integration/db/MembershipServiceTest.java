package nl.tudelft.sem.template.authmember.integration.db;

import nl.tudelft.sem.template.authmember.domain.db.MemberRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipRepository;
import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

public class MembershipServiceTest {

    private MembershipService membershipService;
    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        membershipService = new MembershipService(membershipRepository, memberRepository);
    }



}
