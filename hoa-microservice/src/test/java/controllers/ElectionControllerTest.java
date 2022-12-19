package controllers;

import nl.tudelft.sem.template.hoa.controllers.ElectionController;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.ElectionUtils;
import nl.tudelft.sem.template.hoa.utils.MembershipUtils;
import nl.tudelft.sem.template.hoa.utils.TimeUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class ElectionControllerTest {

    List<MembershipResponseModel> memberships;
    ElectionController controller = new ElectionController();
    @BeforeEach
    void setup() {
        memberships = new ArrayList<>();
        memberships.add(
            new MembershipResponseModel(0L, "0", 1, "a", "b",
                false, TimeUtils.getFirstEpochDate(),
                TimeUtils.dateFromYearsSinceEpoch(9)));
    }

    @Test
    public void testJoinElectionCorrect() {

        MembershipResponseModel a1 =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                false, TimeUtils.getFirstEpochDate(),
                TimeUtils.dateFromYearsSinceEpoch(7));
        MembershipResponseModel a2 =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                true, TimeUtils.getFirstEpochDate(),
                TimeUtils.dateFromYearsSinceEpoch(3));
        memberships.add(a1);
        memberships.add(a2);

        try (MockedStatic<MembershipUtils> membershipUtils = Mockito.mockStatic(MembershipUtils.class)) {
            membershipUtils.when(() -> MembershipUtils.getMembershipsForUser("sem")).thenReturn(memberships);
            try (MockedStatic<ElectionUtils> electionUtils = Mockito.mockStatic(ElectionUtils.class)) {
                electionUtils.when(() -> ElectionUtils.joinElection("sem", 0)).thenReturn(true);

                assertTrue(controller.joinElection("sem", 0).getBody());
            }
        }
    }

    @Test
    public void testJoinElectionWrong() {

        MembershipResponseModel a1 =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                true, TimeUtils.getFirstEpochDate(),
                TimeUtils.dateFromYearsSinceEpoch(7));
        MembershipResponseModel a2 =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                true, TimeUtils.getFirstEpochDate(),
                TimeUtils.dateFromYearsSinceEpoch(4));
        memberships.add(a1);
        memberships.add(a2);

        try (MockedStatic<MembershipUtils> membershipUtils = Mockito.mockStatic(MembershipUtils.class)) {
            membershipUtils.when(() -> MembershipUtils.getMembershipsForUser("sem")).thenReturn(memberships);
            try (MockedStatic<ElectionUtils> electionUtils = Mockito.mockStatic(ElectionUtils.class)) {
                electionUtils.when(() -> ElectionUtils.joinElection("sem", 0)).thenReturn(true);

                assertThrows(ResponseStatusException.class, () -> controller.joinElection("sem", 0).getBody());
            }
        }
    }

}
