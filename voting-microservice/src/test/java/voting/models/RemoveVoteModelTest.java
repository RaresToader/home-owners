package voting.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import voting.annotations.TestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static voting.annotations.TestSuite.TestType.BOUNDARY;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = {BOUNDARY, UNIT})
class RemoveVoteModelTest {

	@ParameterizedTest
	@CsvSource({"-1, test, false, Invalid electionId", "1, test, true, Valid",
		"1, test, true, Valid"})
	void isValidTest(int electionID, String memberID, boolean expected, String testDesc) {
		RemoveVoteModel sut = new RemoveVoteModel(electionID, memberID);
		assertEquals(expected, sut.isValid(), testDesc);
	}

}