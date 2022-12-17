package voting.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import voting.db.converters.LocalDateTimeConverter;

import javax.persistence.Convert;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElectionTest {

	private String description;
	private LocalDateTime scheduledFor;
	private int amountOfWinners;
	private ArrayList<Integer> candidates;
	private Election boardElection;
	private Election proposal;

	@BeforeEach
	void setUp() {
		this.description = "TestExample";
		this.scheduledFor = LocalDateTime.now();
		this.amountOfWinners = 2;
		this.candidates = new ArrayList<>(List.of(1, 2 ,3 ,4));
		this.boardElection = new BoardElection("BoardElection", description, 1, scheduledFor, amountOfWinners, candidates);
		this.proposal = new Proposal("Proposal", description, 1, scheduledFor);
	}

	@Test
	void getElectionId() {
		assertEquals(0 ,boardElection.getElectionId());
	}

	@Test
	void getHoaId() {
		assertEquals(1, boardElection.getHoaId());
		assertEquals(1, proposal.getHoaId());
	}

	@Test
	void getName() {
		assertEquals("BoardElection", boardElection.getName());
		assertEquals("Proposal", proposal.getName());
	}

	@Test
	void getDescription() {
		assertEquals(description, boardElection.getDescription());
		assertEquals(description, proposal.getDescription());
	}

	@Test
	void getVoteCount() {
		assertEquals(0, boardElection.getVoteCount());
		assertEquals(0, proposal.getVoteCount());
	}

	@Test
	void getScheduledFor() {
		assertEquals(scheduledFor, boardElection.getScheduledFor());
		assertEquals(scheduledFor, proposal.getScheduledFor());
	}

	@Test
	void setName() {
		boardElection.setName("NewName");
		assertEquals("NewName", boardElection.getName());
	}

	@Test
	void setDescription() {
		boardElection.setDescription("NewDescription");
		assertEquals("NewDescription", boardElection.getDescription());
	}

	@Test
	void setScheduledFor() {
		boardElection.setScheduledFor(LocalDateTime.of(1, 1 ,1, 1, 1));
		assertEquals(LocalDateTime.of(1, 1, 1, 1 ,1), boardElection.getScheduledFor());
	}

	@Test
	void getStatus() {
		assertEquals("scheduled", boardElection.getStatus());
		assertEquals("scheduled", proposal.getStatus());
	}

	@Test
	void setStatus() {
		boardElection.setStatus("finished");
		assertEquals("finished", boardElection.getStatus());
	}

	@Test
	void testEqualsDifferentClasses() {
		assertFalse(boardElection.equals(1));
	}

	@Test
	void testEqualsFalse() {
		assertFalse(boardElection.equals(proposal));
	}

	@Test
	void testEqualsTrue() {
		assertTrue(boardElection.equals(boardElection));
	}

	@Test
	void incrementVoteCount() {
		assertEquals(0, boardElection.getVoteCount());
		boardElection.incrementVoteCount();
		assertEquals(1, boardElection.getVoteCount());

	}

	@Test
	void testToString() {
		String ans = "Election{"
			+ "electionID='0"
			+ '\'' + ", hoaID='1"
			+ '\'' + ", name='BoardElection"
			+ '\'' + ", description='TestExample"
			+ '\'' + ", voteCount='0"
			+ '\'' + ", time=" + scheduledFor.toString() + '}';
		assertEquals(ans,boardElection.toString());
	}
}