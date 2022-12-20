package voting.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ElectionServiceTest {

	private TimeModel validTM;
	private BoardElectionModel beModel;
	private ProposalModel pModel;
	private VotingModel vModel;
	private ElectionService electionService;
	private ElectionRepository repository;

	@BeforeEach
	void setUp() {
		validTM = new TimeModel(10, 10, 10, 10, 10, 10);
		ArrayList<Integer> candidates = new ArrayList<>(List.of(1, 2, 3));
		beModel = new BoardElectionModel();
		pModel = new ProposalModel();
		vModel = new VotingModel(0, 1, 2);
		beModel.hoaId = pModel.hoaId = 1;
		beModel.name = "BoardElection";
		beModel.description = "TestBoardElection";
		beModel.candidates = candidates;
		beModel.amountOfWinners = 2;
		beModel.scheduledFor = pModel.scheduledFor = validTM;
		pModel.name = "Proposal";
		pModel.description = "TestProposal";
		repository = mock(ElectionRepository.class);
		electionService = new ElectionService(repository);
	}

	@Test
	void createBoardElectionInvalidModel() {
		beModel.amountOfWinners = 0;
		assertThrows(ElectionCannotBeCreated.class, () -> electionService.createBoardElection(beModel));
		Mockito.verify(repository, times(0)).getBoardElectionByHoaId(beModel.getHoaId());
	}

	@Test
	void createBoardElectionAlreadyExisting() {
		beModel.hoaId = 2;
		Election boardElection = new BoardElection("BoardElection2", "ExistingElection", 2,
			LocalDateTime.now(), 2, new ArrayList<>());
		when(repository.getBoardElectionByHoaId(2)).thenReturn(Optional.of(boardElection));
		assertThrows(BoardElectionAlreadyCreated.class, () -> electionService.createBoardElection(beModel));
		Mockito.verify(repository, times(1)).getBoardElectionByHoaId(beModel.getHoaId());
		Mockito.verify(repository, times(0)).save(boardElection);
	}

	@Test
	void createBoardElectionSuccessful() throws BoardElectionAlreadyCreated, ElectionCannotBeCreated {
		Election boardElection = new BoardElection(beModel.getName(), beModel.getDescription(), beModel.getHoaId(),
			beModel.getScheduledFor().createDate(), beModel.getAmountOfWinners(), beModel.getCandidates());
		when(repository.getBoardElectionByHoaId(1)).thenReturn(Optional.empty());
		assertEquals(boardElection, electionService.createBoardElection(beModel));
		Mockito.verify(repository, times(1)).getBoardElectionByHoaId(beModel.getHoaId());
		Mockito.verify(repository, times(1)).save(boardElection);
	}

	@Test
	void createProposalInvalidModel() {
		pModel.hoaId = -1;
		assertThrows(ElectionCannotBeCreated.class, () -> electionService.createProposal(pModel));
		Mockito.verify(repository, times(0)).getBoardElectionByHoaId(pModel.getHoaId());
	}

	@Test
	void createProposalAlreadyExisting() {
		beModel.hoaId = 2;
		when(repository.existsByHoaIdAndName(pModel.getHoaId(), pModel.getName())).thenReturn(true);
		assertThrows(ProposalAlreadyCreated.class, () -> electionService.createProposal(pModel));
		Mockito.verify(repository, times(1)).existsByHoaIdAndName(pModel.getHoaId(), pModel.getName());
		verifyNoMoreInteractions(repository);
	}

	@Test
	void createProposalSuccessful() throws ElectionCannotBeCreated, ProposalAlreadyCreated {
		Election proposal = new Proposal(beModel.getName(), beModel.getDescription(), beModel.getHoaId(),
			beModel.getScheduledFor().createDate());
		when(repository.getBoardElectionByHoaId(1)).thenReturn(Optional.empty());
		assertEquals(proposal, electionService.createProposal(pModel));
		Mockito.verify(repository, times(1)).existsByHoaIdAndName(pModel.getHoaId(), pModel.getName());
		Mockito.verify(repository, times(1)).save(proposal);
	}

	@Test
	void voteInvalidModel() {
		vModel.electionId = -1;
		assertThrows(ElectionDoesNotExist.class, () -> electionService.vote(vModel, LocalDateTime.now()));
		Mockito.verify(repository, times(0)).findByElectionId(vModel.getElectionId());
	}

	@Test
	void voteNotExisting() {
		when(repository.findByElectionId(vModel.getElectionId())).thenReturn(Optional.empty());
		assertThrows(ElectionDoesNotExist.class, () -> electionService.vote(vModel, LocalDateTime.now()));
		Mockito.verify(repository, times(1)).findByElectionId(vModel.getElectionId());
	}

	@Test
	void voteNotStarted() {
		Election proposal = new Proposal("Election", "TestExample", 1, validTM.createDate());
		validTM.day = 9;
		when(repository.findByElectionId(vModel.getElectionId())).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(vModel, validTM.createDate()));
		Mockito.verify(repository, times(1)).findByElectionId(vModel.getElectionId());
	}

	@Test
	void voteEnded() {
		Election proposal = new Proposal("Election", "TestExample", 1, validTM.createDate());
		proposal.setStatus("finished");
		when(repository.findByElectionId(vModel.getElectionId())).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(vModel, LocalDateTime.now()));
		Mockito.verify(repository, times(1)).findByElectionId(vModel.getElectionId());
	}

	@Test
	void voteNotACandidate() {
		BoardElection boardElection = new BoardElection(beModel.getName(), beModel.getDescription(), beModel.getHoaId()
			, beModel.getScheduledFor().createDate(), beModel.getAmountOfWinners(), new ArrayList<>(List.of(6, 7)));
		when(repository.findByElectionId(vModel.getElectionId())).thenReturn(Optional.of(boardElection));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(vModel, validTM.createDate()));
		Mockito.verify(repository, times(1)).findByElectionId(vModel.getElectionId());
	}

	@Test
	void voteSuccessfulProposal() throws ElectionDoesNotExist, CannotProceedVote {
		Proposal proposal = new Proposal("Election", "TestExample", 1, validTM.createDate());
		when(repository.findByElectionId(vModel.getElectionId())).thenReturn(Optional.of(proposal));
		electionService.vote(vModel, LocalDateTime.now());
		assertEquals("ongoing", proposal.getStatus());
		assertTrue(proposal.getVotes().containsKey(vModel.getMembershipId())
			&& (proposal.getVotes().get(vModel.getMembershipId()) == vModel.getChoice()));
		Mockito.verify(repository, times(1)).findByElectionId(vModel.getElectionId());
		Mockito.verify(repository, times(1)).save(proposal);
	}

	@Test
	void voteSuccessfulBoard() throws ElectionDoesNotExist, CannotProceedVote {
		BoardElection boardElection = new BoardElection(beModel.getName(), beModel.getDescription(), beModel.getHoaId()
			, beModel.getScheduledFor().createDate(), beModel.getAmountOfWinners(), beModel.getCandidates());
		when(repository.findByElectionId(vModel.getElectionId())).thenReturn(Optional.of(boardElection));
		electionService.vote(vModel, LocalDateTime.now());
		assertEquals("ongoing", boardElection.getStatus());
		assertTrue(boardElection.getVotes().containsKey(vModel.getMembershipId())
			&& (boardElection.getVotes().get(vModel.getMembershipId()) == vModel.getChoice()));
		Mockito.verify(repository, times(1)).findByElectionId(vModel.getElectionId());
		Mockito.verify(repository, times(1)).save(boardElection);
	}

	@Test
	void getElectionFail() {
		when(repository.findByElectionId(2)).thenReturn(Optional.empty());
		assertThrows(ElectionDoesNotExist.class, () -> electionService.getElection(2));
	}

	@Test
	void getElectionSuccess() throws ElectionDoesNotExist {
		Election proposal = new Proposal(pModel.getName(), pModel.getDescription(), pModel.getHoaId()
			, pModel.getScheduledFor().createDate());
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
		Election proposal = new Proposal(pModel.getName(), pModel.getDescription(), pModel.getHoaId()
			, pModel.getScheduledFor().createDate());
		when(repository.findByElectionId(1)).thenReturn(Optional.of(proposal));
		assertEquals(proposal.conclude(), electionService.conclude(1));
		verify(repository, times(1)).save(proposal);
	}
}