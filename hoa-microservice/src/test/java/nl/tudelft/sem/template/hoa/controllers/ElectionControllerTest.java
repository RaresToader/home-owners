package nl.tudelft.sem.template.hoa.controllers;

public class ElectionControllerTest {

//    transient List<MembershipResponseModel> memberships;
//    transient ElectionController controller = new ElectionController();
//
//    private static final String SEM = "SEM";
//
//    @BeforeEach
//    void setup() {
//        memberships = new ArrayList<>();
//        memberships.add(
//            new MembershipResponseModel(0L, "0", 1, "a", "b",
//                false, TimeUtils.getFirstEpochDate(),
//                TimeUtils.dateFromYearsSinceEpoch(9)));
//    }
//
//    @Test
//    public void testJoinElectionCorrect() {
//
//        MembershipResponseModel a1 =
//            new MembershipResponseModel(0L, "0", 0, "a", "b",
//                false, TimeUtils.getFirstEpochDate(),
//                TimeUtils.dateFromYearsSinceEpoch(7));
//        MembershipResponseModel a2 =
//            new MembershipResponseModel(0L, "0", 0, "a", "b",
//                true, TimeUtils.getFirstEpochDate(),
//                TimeUtils.dateFromYearsSinceEpoch(3));
//        memberships.add(a1);
//        memberships.add(a2);
//
//        try (MockedStatic<MembershipUtils> membershipUtils = Mockito.mockStatic(MembershipUtils.class)) {
//            membershipUtils.when(() -> MembershipUtils.getMembershipsForUser(SEM, "a")).thenReturn(memberships);
//            try (MockedStatic<ElectionUtils> electionUtils = Mockito.mockStatic(ElectionUtils.class)) {
//                electionUtils.when(() -> ElectionUtils.joinElection(SEM, 0)).thenReturn(true);
//
//                assertTrue(controller.joinElection(SEM, 0, "a").getBody());
//            }
//        }
//    }
//
//    @Test
//    public void testJoinElectionWrong() {
//
//        MembershipResponseModel a1 =
//            new MembershipResponseModel(0L, "0", 0, "a", "b",
//                true, TimeUtils.getFirstEpochDate(),
//                TimeUtils.dateFromYearsSinceEpoch(7));
//        MembershipResponseModel a2 =
//            new MembershipResponseModel(0L, "0", 0, "a", "b",
//                true, TimeUtils.getFirstEpochDate(),
//                TimeUtils.dateFromYearsSinceEpoch(4));
//        memberships.add(a1);
//        memberships.add(a2);
//
//        try (MockedStatic<MembershipUtils> membershipUtils = Mockito.mockStatic(MembershipUtils.class)) {
//            membershipUtils.when(() -> MembershipUtils.getMembershipsForUser(SEM, "a")).thenReturn(memberships);
//            try (MockedStatic<ElectionUtils> electionUtils = Mockito.mockStatic(ElectionUtils.class)) {
//                electionUtils.when(() -> ElectionUtils.joinElection(SEM, 0)).thenReturn(true);
//
//                assertThrows(ResponseStatusException.class, () -> controller.joinElection(SEM, 0, "a")
//                        .getBody());
//            }
//        }
//    }

}
