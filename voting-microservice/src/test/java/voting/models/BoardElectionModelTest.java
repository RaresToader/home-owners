package voting.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import voting.annotations.TestSuite;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static voting.annotations.TestSuite.TestType.BOUNDARY;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = {BOUNDARY, UNIT})
class BoardElectionModelTest {

    @ParameterizedTest
    @MethodSource("argGen")
    void isValidTest(int hoaId, String name, String desc, TimeModel scheduledFor, int amountOfWinners,
                     List<String> candidates, boolean expected, String testdesc) {
        BoardElectionModel sut = new BoardElectionModel(
                name, desc, hoaId, scheduledFor, amountOfWinners, candidates);
        assertEquals(expected, sut.isValid(), testdesc);
    }

    static Stream<Arguments> argGen() {
        TimeModel validTM = new TimeModel(10, 10, 10, 10, 10, 10);
        TimeModel invalidTM = new TimeModel(-1, 10, 10, 10, 10, 10);
        String validName = "validName";
        String validDesc = "validDescriptionThatIsLong:))";
        return Stream.of(
                // Invalid cases
                Arguments.of(-1, validName, validDesc, validTM, 5, List.of("1", "2", "3"), false,
                        "Invalid hoaId"),
                Arguments.of(1, "", validDesc, validTM, 5, List.of("1", "2", "3"), false, "No name"),
                Arguments.of(1, validName, "", validTM, 5, List.of("1", "2", "3"), false, "No description"),
                Arguments.of(1, "a".repeat(90), validDesc, validTM, 5, List.of("1", "2", "3"), false,
                        "Long name"),
                Arguments.of(1, validName, "b".repeat(240), validTM, 5, List.of("1", "2", "3"), false,
                        "Long desc"),
                Arguments.of(1, validName, validDesc, invalidTM, 5, List.of("1", "2", "3"), false,
                        "Invalid time"),
                Arguments.of(1, validName, validDesc, validTM, 0, List.of("1", "2", "3"), false,
                        "Winners cannot be 0"),
                Arguments.of(1, validName, validDesc, validTM, 5, null, false, "Candidates cannot be null"),
                Arguments.of(1, "", "", validTM, 0, List.of(), false, "Combined fail"),
                // Valid cases
                Arguments.of(1, validName, validDesc, validTM, 5, List.of("1", "2", "3"), true, "Valid"),
                Arguments.of(1, validName, validDesc, validTM, 5, List.of(), true,
                        "Valid, note no candidates")
        );
    }
}
