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
    @CsvSource({"-1, 2, 3, false, Invalid electionId", "1, -2, 3, false, Invalid memberId",
            "1, 2, -4, false, Invalid voteChoice", "1, 2, 3, true, Valid"})
    void isValidTest(int electionID, int memberID, int voteChoice, boolean expected, String testdesc) {
        VotingModel sut = new VotingModel(electionID, memberID, voteChoice);
        assertEquals(expected, sut.isValid(), testdesc);
    }

}