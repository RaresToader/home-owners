package nl.tudelft.sem.template.authmember.services;

import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.domain.exceptions.BadJoinHoaModelException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberAlreadyInHoaException;
import nl.tudelft.sem.template.authmember.domain.exceptions.MemberDifferentAddressException;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;

class HoaServiceTest {

    private MembershipService membershipService;

    @Autowired
    private HoaService hoaService;

    @BeforeEach
    void setup(){
        membershipService = Mockito.mock(MembershipService.class);
        Mockito.when(this.membershipService.getActiveMemberships("member1")).thenReturn(new ArrayList<>());
    }

    @Test
    void joinHoa() throws MemberDifferentAddressException, MemberAlreadyInHoaException, BadJoinHoaModelException {
//        hoaService.joinHoa(new JoinHoaModel());

    }

    @Test
    void leaveHoa() {
    }

    @Test
    void getCurrentMembership() {
    }

    @Test
    void getMembershipsForHoa() {
    }
}