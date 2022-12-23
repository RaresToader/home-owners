package voting.services;

import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.repos.ElectionRepository;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.exceptions.BoardElectionAlreadyCreated;
import voting.exceptions.CannotProceedVote;
import voting.exceptions.ElectionCannotBeCreated;
import voting.exceptions.ElectionDoesNotExist;
import voting.exceptions.ProposalAlreadyCreated;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;
import voting.models.TimeModel;
import voting.models.VotingModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static voting.annotations.TestSuite.TestType.INTEGRATION;

@TestSuite(testType = {INTEGRATION})
class ElectionServiceTest {

	private TimeModel validTM;
	private BoardElectionModel beModel;
	private ProposalModel propModel;
	private VotingModel propVoteModel;

	private VotingModel beVoteModel;
	private ElectionService electionService;
	private ElectionRepository repository;

	@BeforeEach
	void setUp() {
		validTM = new TimeModel(10, 10, 10, 10, 10, 10);
		beModel = new BoardElectionModel();
		beModel.name = "BoardElection";
		beModel.description = "TestBoardElection";
		beModel.candidates = new ArrayList<>(List.of("1", "2", "3"));
		beModel.amountOfWinners = 2;

		propModel = new ProposalModel();
		propModel.name = "Proposal";
		propModel.description = "TestProposal";

		beModel.scheduledFor = propModel.scheduledFor = validTM;
		beModel.hoaId = propModel.hoaId = 1;

		propVoteModel = new VotingModel(0, "chad", "false");
		beVoteModel = new VotingModel(0, "chad", "1");
		repository = mock(ElectionRepository.class);
		electionService = new ElectionService(repository);
	}

	@Test
	void createBoardElectionInvalidModel() {
		beModel.amountOfWinners = 0;
		assertThrows(ElectionCannotBeCreated.class, () -> electionService.createBoardElection(beModel));
		verify(repository, times(0)).getBoardElectionByHoaId(beModel.hoaId);
	}

	@Test
	void createBoardElectionAlreadyExisting() {
		beModel.hoaId = 2;
		beModel.scheduledFor = new TimeModel(10, 10, 10, 10, 10,
				LocalDateTime.now().getYear() + 2);
		Election boardElection = new BoardElection("BoardElection2", "ExistingElection", 2,
			LocalDateTime.now().plusYears(1), 2, new ArrayList<>());
		when(repository.getBoardElectionByHoaId(2)).thenReturn(Optional.of(boardElection));
		assertThrows(BoardElectionAlreadyCreated.class, () -> electionService.createBoardElection(beModel));
		verify(repository, times(1)).getBoardElectionByHoaId(beModel.hoaId);
		verify(repository, times(0)).save(boardElection);
	}

	@Test
	void createBoardElectionSuccessful() throws BoardElectionAlreadyCreated, ElectionCannotBeCreated {
		beModel.scheduledFor = new TimeModel(10, 10, 10, 10, 10,
				LocalDateTime.now().getYear() + 2);
		Election boardElection = new BoardElection(beModel.name, beModel.description, beModel.hoaId,
			LocalDateTime.now().plusYears(2), beModel.amountOfWinners, beModel.candidates);
		when(repository.getBoardElectionByHoaId(1)).thenReturn(Optional.empty());
		assertEquals(boardElection, electionService.createBoardElection(beModel));
		verify(repository, times(1)).getBoardElectionByHoaId(beModel.hoaId);
		verify(repository, times(1)).save(boardElection);
	}

	@Test
	void createProposalInvalidModel() {
		propModel.hoaId = -1;
		assertThrows(ElectionCannotBeCreated.class, () -> electionService.createProposal(propModel));
		verify(repository, times(0)).getBoardElectionByHoaId(propModel.hoaId);
	}

	@Test
	void createProposalAlreadyExisting() {
		beModel.hoaId = 2;
		when(repository.existsByHoaIdAndName(propModel.hoaId, propModel.name)).thenReturn(true);
		assertThrows(ProposalAlreadyCreated.class, () -> electionService.createProposal(propModel));
		verify(repository, times(1)).existsByHoaIdAndName(propModel.hoaId,
				propModel.name);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void createProposalSuccessful() throws ElectionCannotBeCreated, ProposalAlreadyCreated {
		Election proposal = new Proposal(beModel.name, beModel.description, beModel.hoaId,
			beModel.scheduledFor.createDate());
		when(repository.getBoardElectionByHoaId(1)).thenReturn(Optional.empty());
		assertEquals(proposal, electionService.createProposal(propModel));
		verify(repository, times(1)).existsByHoaIdAndName(propModel.hoaId,
				propModel.name);
		verify(repository, times(1)).save(proposal);
	}

	@Test
	void voteInvalidModel() {
		propVoteModel.electionId = -1;
		assertThrows(ElectionDoesNotExist.class, () -> electionService.vote(propVoteModel, LocalDateTime.now()));
		verify(repository, times(0)).findByElectionId(propVoteModel.electionId);
	}

	@Test
	void voteNotExisting() {
		when(repository.findByElectionId(propVoteModel.electionId)).thenReturn(Optional.empty());
		assertThrows(ElectionDoesNotExist.class, () -> electionService.vote(propVoteModel, LocalDateTime.now()));
		verify(repository, times(1)).findByElectionId(propVoteModel.electionId);
	}

	@Test
	void voteNotStarted() {
		Election proposal = new Proposal("Election", "TestExample", 1, validTM.createDate());
		validTM.day = 9;
		when(repository.findByElectionId(propVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(propVoteModel, validTM.createDate()));
		verify(repository, times(1)).findByElectionId(propVoteModel.electionId);
	}

	@Test
	void voteEnded() {
		Election proposal = new Proposal("Election", "TestExample", 1, validTM.createDate());
		proposal.setStatus("finished");
		when(repository.findByElectionId(propVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(propVoteModel, LocalDateTime.now()));
		verify(repository, times(1)).findByElectionId(propVoteModel.electionId);
	}

	@Test
	void voteNotACandidate() {
		BoardElection boardElection = new BoardElection(beModel.name, beModel.description, beModel.hoaId,
				beModel.scheduledFor.createDate(), beModel.amountOfWinners, new ArrayList<>(List.of("6", "7")));
		when(repository.findByElectionId(beVoteModel.getElectionId())).thenReturn(Optional.of(boardElection));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(beVoteModel, validTM.createDate()));
		verify(repository, times(1)).findByElectionId(beVoteModel.electionId);
	}

	@Test
	void voteSuccessfulProposal() throws ElectionDoesNotExist, CannotProceedVote {
		Proposal proposal = new Proposal("Election", "TestExample", 1, validTM.createDate());
		when(repository.findByElectionId(propVoteModel.electionId)).thenReturn(Optional.of(proposal));
		electionService.vote(propVoteModel, LocalDateTime.now());
		assertEquals("ongoing", proposal.getStatus());
		assertTrue(proposal.getVotes().containsKey(propVoteModel.memberId)
			&& proposal.getVotes().get(propVoteModel.memberId).equals(false));
		verify(repository, times(1)).findByElectionId(propVoteModel.electionId);
		verify(repository, times(1)).save(proposal);
	}

	@Test
	void voteSuccessfulBoard() throws ElectionDoesNotExist, CannotProceedVote {
		BoardElection boardElection = new BoardElection(beModel.name, beModel.description, beModel.hoaId,
				beModel.scheduledFor.createDate(), beModel.amountOfWinners, beModel.candidates);
		when(repository.findByElectionId(beVoteModel.getElectionId())).thenReturn(Optional.of(boardElection));
		electionService.vote(beVoteModel, LocalDateTime.now());
		assertEquals("ongoing", boardElection.getStatus());
		assertTrue(boardElection.getVotes().containsKey(beVoteModel.memberId)
			&& boardElection.getVotes().get(beVoteModel.memberId).equals(beVoteModel.choice));
		verify(repository, times(1)).findByElectionId(beVoteModel.electionId);
		verify(repository, times(1)).save(boardElection);
	}

	@Test
	void getElectionFail() {
		when(repository.findByElectionId(2)).thenReturn(Optional.empty());
		assertThrows(ElectionDoesNotExist.class, () -> electionService.getElection(2));
	}

	@Test
	void getElectionSuccess() throws ElectionDoesNotExist {
		Election proposal = new Proposal(propModel.name, propModel.description, propModel.hoaId,
				propModel.scheduledFor.createDate());
		when(repository.findByElectionId(1)).thenReturn(Optional.of(proposal));
		assertEquals(proposal, electionService.getElection(1));
	}

	@Test
	void concludeFail() {
		when(repository.findByElectionId(2)).thenReturn(Optional.empty());
		assertThrows(ElectionDoesNotExist.class, () -> electionService.conclude(2));
	}

	@Test
	void concludeSuccess() throws ElectionDoesNotExist {
		Election proposal = new Proposal(propModel.name, propModel.description, propModel.hoaId,
				propModel.scheduledFor.createDate());
		when(repository.findByElectionId(1)).thenReturn(Optional.of(proposal));
		assertEquals(proposal.conclude(), electionService.conclude(1));
		verify(repository, times(1)).save(proposal);
	}
}