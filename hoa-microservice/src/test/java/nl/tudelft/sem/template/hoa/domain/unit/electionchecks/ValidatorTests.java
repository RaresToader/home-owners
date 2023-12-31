package nl.tudelft.sem.template.hoa.domain.unit.electionchecks;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotBoardForTooLongValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.NotInAnyOtherBoardValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.TimeInCurrentHoaValidator;
import nl.tudelft.sem.template.hoa.domain.electionchecks.Validator;
import nl.tudelft.sem.template.hoa.exception.InvalidParticipantException;
import nl.tudelft.sem.template.hoa.models.MembershipResponseModel;
import nl.tudelft.sem.template.hoa.utils.TimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestSuite(testType = UNIT)
public class ValidatorTests {

    transient Validator boardTimeValidator;
    transient Validator notInOtherBoardValidator;
    transient Validator timeInCurrentHoaValidator;

    //Used to check that validators filter properly
    transient  MembershipResponseModel wrongHOA =
        new MembershipResponseModel(0L, "0", 1, "a", "b",
            false, TimeUtils.getFirstEpochDate(), Duration.ofDays(9 * 365));
    transient List<MembershipResponseModel> memberships;

    @BeforeEach
    void setup() {
        boardTimeValidator = new NotBoardForTooLongValidator();
        notInOtherBoardValidator = new NotInAnyOtherBoardValidator();
        timeInCurrentHoaValidator = new TimeInCurrentHoaValidator();
        memberships = new ArrayList<>();
        memberships.add(wrongHOA);
    }

    @Test
    void notBoardTooLongTrueTest() throws InvalidParticipantException {
        MembershipResponseModel nineYearsInBoard =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                true, TimeUtils.getFirstEpochDate(), Duration.ofDays(9 * 365));
        MembershipResponseModel oneYearInBoard =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                true, TimeUtils.getFirstEpochDate(), Duration.ofDays(365));
        //9 years
        memberships.add(nineYearsInBoard);
        assertTrue(boardTimeValidator.handle(memberships, 0));
        //10 years
        memberships.add(oneYearInBoard);
        assertTrue(boardTimeValidator.handle(memberships, 0));
        //11 years
        memberships.add(oneYearInBoard);
        assertThrows(InvalidParticipantException.class, () -> boardTimeValidator.handle(memberships, 0));
    }

    @Test
    void notBoardForTooLongTestInHoaButNotInBoard() throws InvalidParticipantException {
        MembershipResponseModel inHoaButNotBoard =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                false, TimeUtils.getFirstEpochDate(), Duration.ofDays(11 * 365));
        memberships.add(inHoaButNotBoard);
        assertTrue(boardTimeValidator.handle(memberships, 0));
    }

    @Test
    void notAnyOtherBoardValidatorTest() throws InvalidParticipantException {
        MembershipResponseModel currentBoard =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                true, TimeUtils.getFirstEpochDate(), Duration.ofDays(365));
        //User has never been in a board
        assertTrue(notInOtherBoardValidator.handle(memberships, 0));
        memberships.add(currentBoard);
        //User is in a board, but it's the current HOA one -> it's fine
        assertTrue(notInOtherBoardValidator.handle(memberships, 0));
        MembershipResponseModel otherBoard =
            new MembershipResponseModel(0L, "0", 1, "a", "b",
                true, TimeUtils.getFirstEpochDate(), Duration.ofDays(365));
        //User is in another board -> not fine
        memberships.add(otherBoard);
        assertThrows(InvalidParticipantException.class, () -> notInOtherBoardValidator.handle(memberships, 0));
    }

    @Test
    void timeInCurrentHoaValidatorTest() throws InvalidParticipantException {
        MembershipResponseModel oneYear =
            new MembershipResponseModel(0L, "0", 0, "a", "b",
                false, TimeUtils.getFirstEpochDate(), Duration.ofDays(366));

        //User 9 years in other board, but not in current
        assertThrows(InvalidParticipantException.class, () -> timeInCurrentHoaValidator.handle(memberships, 0));
        memberships.add(oneYear);
        memberships.add(oneYear);
        assertThrows(InvalidParticipantException.class, () -> timeInCurrentHoaValidator.handle(memberships, 0));
        memberships.add(oneYear);
        assertTrue(timeInCurrentHoaValidator.handle(memberships, 0));
    }

}