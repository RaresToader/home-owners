package voting.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProposalTest {

	private String description;
	private LocalDateTime scheduledFor;
	private Proposal proposal;

	@BeforeEach
	void setUp() {
		this.description = "TestExample";
		this.scheduledFor = LocalDateTime.now();
		this.proposal = new Proposal("Proposal", description, 1, scheduledFor);
	}

	@Test
	void isWinningChoiceTest() {
		assertEquals(false, proposal.isWinningChoice());
	}

	@Test
	void setWinningChoiceTest() {
		proposal.setWinningChoice(true);
		assertEquals(true, proposal.isWinningChoice());
	}

	@Test
	void getVotesTest() {
		assertTrue(proposal.getVotes().isEmpty());
	}

	@Test
	void setVotesTest() {
		HashMap<Integer, Integer> map = new HashMap<>();
		map.put(1,1);
		proposal.setVotes(map);
		assertEquals(map, proposal.getVotes());
	}

	@Test
	void failedVote() {
		assertTrue(proposal.getVotes().isEmpty());
		proposal.vote(1, 1);
		assertTrue(proposal.getVotes().isEmpty());
		assertEquals(0, proposal.getVoteCount());
	}

	@Test
	void successfulVote() {
		assertTrue(proposal.getVotes().isEmpty());
		proposal.setStatus("ongoing");
		proposal.vote(1, 1);
		HashMap<Integer, Integer> votes = new HashMap<>();
		votes.put(1, 1);
		assertEquals(votes, proposal.getVotes());
		assertEquals(1, proposal.getVoteCount());
	}

	@Test
	void findOutcome() {
		proposal.setStatus("ongoing");
		proposal.vote(1, 1);
		assertTrue(proposal.findOutcome());
	}

	@Test
	void conclude() {
		boolean ans = proposal.conclude();
		assertEquals("finished", proposal.getStatus());
		assertEquals(false, ans);
	}
}