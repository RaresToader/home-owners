package voting.db.repos;

import org.junit.jupiter.api.Test;
import voting.domain.BoardElection;
import voting.domain.Election;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElectionRepositoryTest {

	@Test
	void findByElectionId() {
		Election boardElection = new BoardElection("BoardElection", "TestExample", 1,
			LocalDateTime.now(), 2, new ArrayList<>());
		ElectionRepository electionRepository = mock(ElectionRepository.class);
		when(electionRepository.findByElectionId(0)).thenReturn(Optional.of(boardElection));
		Optional<Election> answer = electionRepository.findByElectionId(0);
		assertTrue(answer.isPresent());
		assertEquals(boardElection, answer.get());
	}

	@Test
	void existsByHoaIdAndName() {
		Election boardElection = new BoardElection("BoardElection", "TestExample", 1,
			LocalDateTime.now(), 2, new ArrayList<>());
		ElectionRepository electionRepository = mock(ElectionRepository.class);
		when(electionRepository.existsByHoaIdAndName(boardElection.getHoaId(), boardElection.getName())).thenReturn(true);
		boolean answer = electionRepository.existsByHoaIdAndName(1, "BoardElection");
		assertTrue(answer);
	}

	@Test
	void getBoardElectionByHoaId() {
		Election boardElection = new BoardElection("BoardElection", "TestExample", 1,
			LocalDateTime.now(), 2, new ArrayList<>());
		boardElection.setStatus("ongoing");
		ElectionRepository electionRepository = mock(ElectionRepository.class);
		when(electionRepository.getBoardElectionByHoaId(boardElection.getHoaId())).thenReturn(Optional.of(boardElection));
		Optional<Election> answer = electionRepository.getBoardElectionByHoaId(1);
		assertTrue(answer.isPresent());
		assertEquals(boardElection, answer.get());
	}
}