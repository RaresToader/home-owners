package nl.tudelft.sem.template.authmember.services;

import nl.tudelft.sem.template.authmember.domain.db.MembershipService;
import nl.tudelft.sem.template.authmember.models.JoinHoaModel;
import nl.tudelft.sem.template.authmember.utils.HoaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
    void joinHoa() {
        hoaService.joinHoa(new JoinHoaModel());

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