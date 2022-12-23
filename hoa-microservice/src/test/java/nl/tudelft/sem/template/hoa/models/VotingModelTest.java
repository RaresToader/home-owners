package nl.tudelft.sem.template.hoa.models;

import static nl.tudelft.sem.template.hoa.annotations.TestSuite.TestType.UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.hoa.annotations.TestSuite;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestSuite(testType = UNIT)
class VotingModelTest {

    @ParameterizedTest
    @CsvSource({"-1, test, false, false, Invalid electionId", "1, test, false, true, Valid",
            "1, test, tested, true, Valid"})
    void isValidTest(int electionID, String memberID, String voteChoice, boolean expected, String testdesc) {
        VotingModel sut = new VotingModel(electionID, memberID, voteChoice);
        assertEquals(expected, sut.isValid(), testdesc);
    }

}