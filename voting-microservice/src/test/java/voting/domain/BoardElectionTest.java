package voting.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.exceptions.ThereIsNoVote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static voting.annotations.TestSuite.TestType.UNIT;

@TestSuite(testType = UNIT)
class BoardElectionTest {

	private BoardElection boardElection;
	private List<String> candidates;

	@BeforeEach
	void setUp() {
		this.candidates = new ArrayList<>(List.of("0", "1", "2"));
		this.boardElection = new BoardElection("BoardElection", "TestExample", 1,
				LocalDateTime.now(), 2, candidates);
	}

	@Test
	void getCandidatesTest() {
		assertEquals(this.candidates, boardElection.getCandidates());
	}

	@Test
	void setCandidatesTest() {
		boardElection.setCandidates(new ArrayList<>(List.of("4", "5")));
		assertEquals(new ArrayList<>(List.of("4", "5")), boardElection.getCandidates());
	}

	@Test
	void getVotesTest() {
		assertTrue(boardElection.getVotes().isEmpty());
	}

	@Test
	void setVotesTest() {
		HashMap<String, String> map = new HashMap<>();
		map.put("1", "1");
		boardElection.setVotes(map);
		assertEquals(map, boardElection.getVotes());
	}

	@Test
	void getAmountOfWinnersTest() {
		assertEquals(2, boardElection.getAmountOfWinners());
	}

	@Test
	void setAmountOfWinnersTest() {
		boardElection.setAmountOfWinners(3);
		assertEquals(3, boardElection.getAmountOfWinners());
	}

	@Test
	void failedVote() {
		boardElection.vote("1", "1");
		assertTrue(boardElection.getVotes().isEmpty());
		assertEquals(0, boardElection.getVoteCount());

		boardElection.setStatus("ongoing");
		boardElection.vote("1", "42");
		assertTrue(boardElection.getVotes().isEmpty());
		assertEquals(0, boardElection.getVoteCount());
	}

	@Test
	void successfulVote() {
		assertTrue(boardElection.getVotes().isEmpty());
		boardElection.setStatus("ongoing");
		boardElection.vote("1", "1");
		HashMap<String, String> votes = new HashMap<>();
		votes.put("1", "1");
		assertEquals(votes, boardElection.getVotes());
		assertEquals(1, boardElection.getVoteCount());
	}

	@Test
	void removeVoteFail() {
		boardElection.setStatus("ongoing");
		boardElection.vote("1", "2");
		assertThrows(ThereIsNoVote.class, () -> boardElection.removeVote("0"));
	}

	@Test
	void removeVoteSuccess() throws ThereIsNoVote {
		boardElection.setStatus("ongoing");
		boardElection.vote("1", "2");
		boardElection.removeVote("1");
		assertTrue(boardElection.getVotes().isEmpty());
	}

	@Test
	void findOutcome() {
		boardElection.setStatus("ongoing");
		List<Integer> states = new ArrayList<>(List.of(
				1, 1, 2, 0, 1, 1, 1, 0, 3, 2, 4, 1, 4, 2));
		List<Set<String>> expectations = new ArrayList<>(List.of(
				// One positive
				// 1 -> 1
				Set.of("1"),
				// Majority is no longer valid, two winners
				// 1 -> 1, 0 -> 1
				Set.of("1", "0"),
				// Idempotence
				Set.of("1", "0"),
				// 0 -> 2, no votes for 1 and 2
				Set.of("0"),
				// Third join, one vote for newer candidate
				// 0 -> 2, 2 -> 1
				Set.of("0", "2"),
				// Tie > earlier candidate is chosen
				// 0 -> 2, 1 -> 1, 2 -> 1
				Set.of("0", "1"),
				// New majority
				// 0 -> 2, 2 -> 2, 1 -> 0
				Set.of("0", "2")));
		for (int i = 0; i < expectations.size(); i++) {
			boardElection.vote(String.valueOf(states.get(2 * i)), String.valueOf(states.get(2 * i + 1)));
			assertEquals(expectations.get(i), boardElection.findOutcome());
		}
	}

	@Test
	void conclude() {
		Set<String> ans = boardElection.conclude();
		assertEquals("finished", boardElection.getStatus());
		assertTrue(ans.isEmpty());
	}

}