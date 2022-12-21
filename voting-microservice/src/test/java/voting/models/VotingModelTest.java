package voting.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import voting.annotations.TestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static voting.annotations.TestSuite.TestType.BOUNDARY;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = {BOUNDARY, UNIT})
class VotingModelTest {

    @ParameterizedTest
    @CsvSource({"-1, test, false, false, Invalid electionId", "1, test, false, true, Valid",
            "1, test, tested, true, Valid"})
    void isValidTest(int electionID, String memberID, String voteChoice, boolean expected, String testdesc) {
        VotingModel sut = new VotingModel(electionID, memberID, voteChoice);
        assertEquals(expected, sut.isValid(), testdesc);
    }

}