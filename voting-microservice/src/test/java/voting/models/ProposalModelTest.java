package voting.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import voting.annotations.TestSuite;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static voting.annotations.TestSuite.TestType.BOUNDARY;

@TestSuite(testType = {BOUNDARY})
class ProposalModelTest {

    @ParameterizedTest
    @MethodSource("argGen")
    void isValidTest(int hoaId, String name, String desc, TimeModel scheduledFor, boolean expected, String testdesc) {
        ProposalModel sut = new ProposalModel();
        sut.hoaId = hoaId;
        sut.name = name;
        sut.description = desc;
        sut.scheduledFor = scheduledFor;
        assertEquals(expected, sut.isValid(), testdesc);
    }

    static Stream<Arguments> argGen() {
        TimeModel validTM = new TimeModel();
        validTM.seconds = validTM.minutes = validTM.hours = validTM.day = validTM.month = validTM.year = 10;
        TimeModel invalidTM = new TimeModel();
        invalidTM.minutes = invalidTM.hours = invalidTM.day = invalidTM.month = invalidTM.year = 10;
        invalidTM.seconds = -1;
        String validName = "validName";
        String validDesc = "validDescriptionThatIsLong:))";
        return Stream.of(
                // Invalid cases
                Arguments.of(-1, validName, validDesc, validTM, false, "Invalid hoaID"),
                Arguments.of(1, "", validDesc, validTM, false, "No name"),
                Arguments.of(1, null, validDesc, validTM, false, "Null name"),
                Arguments.of(1, "a".repeat(90), validDesc, validTM, false, "Long name"),
                Arguments.of(1, validName, "", validTM, false, "No desc"),
                Arguments.of(1, validName, null, validTM, false, "Null desc"),
                Arguments.of(1, validName, "b".repeat(240), validTM, false, "Long desc"),
                Arguments.of(1, validName, validDesc, invalidTM, false, "Invalid time"),
                Arguments.of(1, validName, validDesc, null, false, "Null time"),
                // Valid cases
                Arguments.of(1, validName, validDesc, validTM, true, "All valid")
        );
    }
}
