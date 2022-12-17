package voting.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardElectionTest {

	private String description;
	private LocalDateTime scheduledFor;
	private BoardElection boardElection;
	private ArrayList<Integer> candidates;

	@BeforeEach
	void setUp() {
		this.description = "TestExample";
		this.scheduledFor = LocalDateTime.now();
		this.candidates = new ArrayList<>(List.of(1, 2, 3));
		this.boardElection = new BoardElection("BoardElection", description, 1, scheduledFor, 2, candidates);
	}

	@Test
	void getCandidatesTest() {
		assertEquals(this.candidates, boardElection.getCandidates());
	}

	@Test
	void setCandidatesTest() {
		boardElection.setCandidates(new ArrayList<>(List.of(4, 5)));
		assertEquals(new ArrayList<>(List.of(4, 5)), boardElection.getCandidates());
	}

	@Test
	void getVotesTest() {
		assertTrue(boardElection.getVotes().isEmpty());
	}

	@Test
	void setVotesTest() {
		HashMap<Integer, Integer> map = new HashMap<>();
		map.put(1,1);
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
		assertTrue(boardElection.getVotes().isEmpty());
		boardElection.vote(1, 1);
		assertTrue(boardElection.getVotes().isEmpty());
		assertEquals(0, boardElection.getVoteCount());
	}

	@Test
	void successfulVote() {
		assertTrue(boardElection.getVotes().isEmpty());
		boardElection.setStatus("ongoing");
		boardElection.vote(1, 1);
		HashMap<Integer, Integer> votes = new HashMap<>();
		votes.put(1, 1);
		assertEquals(votes, boardElection.getVotes());
		assertEquals(1, boardElection.getVoteCount());
	}
	
}