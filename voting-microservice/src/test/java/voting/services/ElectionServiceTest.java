package voting.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import voting.annotations.TestSuite;
import voting.db.repos.ElectionRepository;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.domain.factories.BoardElectionFactory;
import voting.exceptions.BoardElectionAlreadyCreated;
import voting.exceptions.CannotProceedVote;
import voting.exceptions.ElectionCannotBeCreated;
import voting.exceptions.ElectionDoesNotExist;
import voting.exceptions.ProposalAlreadyCreated;
import voting.exceptions.ThereIsNoVote;
import voting.models.BoardElectionModel;
import voting.models.ElectionModel;
import voting.models.ProposalModel;
import voting.models.RemoveVoteModel;
import voting.models.TimeModel;
import voting.models.VotingModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static voting.annotations.TestSuite.TestType.INTEGRATION;

@TestSuite(testType = {INTEGRATION})
class ElectionServiceTest {

	private ElectionModel elModel;
	private TimeModel validTM;
	private BoardElectionModel beModel;
	private ProposalModel propModel;
	private VotingModel propVoteModel;
	private RemoveVoteModel removeVoteModel;

	private VotingModel beVoteModel;
	private ElectionService electionService;
	private ElectionRepository repository;

	private static final String EL = "Election";

	private static final String TESTEX = "TestExample";

	@BeforeEach
	void setUp() {
		validTM = new TimeModel(10, 10, 10, 10, 10, 10);
		elModel = new ElectionModel("BoardElection", "TestBoardElection", 1, validTM);
		beModel = new BoardElectionModel(elModel, 2, new ArrayList<>(List.of("1", "2", "3")));

		propModel = new ProposalModel("Proposal", "TestProposal", 1, validTM);

		propVoteModel = new VotingModel(0, "chad", "false");
		beVoteModel = new VotingModel(0, "chad", "1");
		removeVoteModel = new RemoveVoteModel(0, "chad");
		repository = mock(ElectionRepository.class);
		when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);
		electionService = new ElectionService(repository);
	}

	@Test
	void createBoardElectionInvalidModel() {
		beModel = new BoardElectionModel(elModel, 0, beModel.candidates);
		assertThrows(ElectionCannotBeCreated.class, () -> electionService.createBoardElection(beModel));
		verify(repository, times(0)).getBoardElectionByHoaId(beModel.hoaId);
	}

	@Test
	void createBoardElectionAlreadyExisting() {
		beModel = new BoardElectionModel(new ElectionModel(beModel.name, beModel.description, 2,
				new TimeModel(10, 10, 10, 10, 10,
						LocalDateTime.now().getYear() + 2)), beModel.amountOfWinners, beModel.candidates);
		Election boardElection = new BoardElection("BoardElection2", "ExistingElection", 2,
			LocalDateTime.now().plusYears(1), 2, new ArrayList<>());
		when(repository.getBoardElectionByHoaId(2)).thenReturn(Optional.of(boardElection));
		assertThrows(BoardElectionAlreadyCreated.class, () -> electionService.createBoardElection(beModel));
		verify(repository, times(1)).getBoardElectionByHoaId(beModel.hoaId);
		verify(repository, times(0)).save(boardElection);
	}

	@Test
	void createBoardElectionSuccessful() throws BoardElectionAlreadyCreated, ElectionCannotBeCreated {
		beModel = new BoardElectionModel(new ElectionModel(beModel.name, beModel.description, beModel.hoaId,
				new TimeModel(10, 10, 10, 10, 10,
						LocalDateTime.now().getYear() + 2)), beModel.amountOfWinners, beModel.candidates);
		Election boardElection = new BoardElectionFactory().createElection(beModel);
		when(repository.getBoardElectionByHoaId(1)).thenReturn(Optional.empty());
		assertEquals(boardElection, electionService.createBoardElection(beModel));
		verify(repository, times(1)).getBoardElectionByHoaId(beModel.hoaId);
		verify(repository, times(1)).save(boardElection);
	}

	@Test
	void createProposalInvalidModel() {
		propModel = new ProposalModel(propModel.name, propModel.description, -1, propModel.scheduledFor);
		assertThrows(ElectionCannotBeCreated.class, () -> electionService.createProposal(propModel));
		verify(repository, times(0)).getBoardElectionByHoaId(propModel.hoaId);
	}

	@Test
	void createProposalAlreadyExisting() {
		beModel = new BoardElectionModel(new ElectionModel(elModel.name, elModel.description,
				2, elModel.scheduledFor), beModel.amountOfWinners, beModel.candidates);
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
		propVoteModel = new VotingModel(-1, propVoteModel.memberId, propVoteModel.choice);
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
		Election proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
		validTM = new TimeModel(validTM.seconds, validTM.minutes, validTM.hours, 9, validTM.month, validTM.year);
		when(repository.findByElectionId(propVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(propVoteModel, validTM.createDate()));
		verify(repository, times(1)).findByElectionId(propVoteModel.electionId);
	}

	@Test
	void voteEnded() {
		Election proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
		proposal.setStatus("finished");
		when(repository.findByElectionId(propVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(propVoteModel, LocalDateTime.now()));
		verify(repository, times(1)).findByElectionId(propVoteModel.electionId);
	}

	@Test
	void voteNotACandidate() {
		BoardElection boardElection = new BoardElection(beModel.name, beModel.description, beModel.hoaId,
				beModel.scheduledFor.createDate(), beModel.amountOfWinners, new ArrayList<>(List.of("6", "7")));
		when(repository.findByElectionId(beVoteModel.electionId)).thenReturn(Optional.of(boardElection));
		assertThrows(CannotProceedVote.class, () -> electionService.vote(beVoteModel, validTM.createDate()));
		verify(repository, times(1)).findByElectionId(beVoteModel.electionId);
	}

	@Test
	void voteSuccessfulProposal() throws ElectionDoesNotExist, CannotProceedVote {
		Proposal proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
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
		when(repository.findByElectionId(beVoteModel.electionId)).thenReturn(Optional.of(boardElection));
		electionService.vote(beVoteModel, LocalDateTime.now());
		assertEquals("ongoing", boardElection.getStatus());
		assertTrue(boardElection.getVotes().containsKey(beVoteModel.memberId)
			&& boardElection.getVotes().get(beVoteModel.memberId).equals(beVoteModel.choice));
		verify(repository, times(1)).findByElectionId(beVoteModel.electionId);
		verify(repository, times(1)).save(boardElection);
	}

	@Test
	void removeVoteSuccessful() throws ElectionDoesNotExist, CannotProceedVote, ThereIsNoVote {
		Proposal proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
		proposal.setStatus("ongoing");
		proposal.vote("chad", true);
		when(repository.findByElectionId(removeVoteModel.electionId)).thenReturn(Optional.of(proposal));
		electionService.removeVote(removeVoteModel, LocalDateTime.now());
		assertTrue(proposal.getVotes().isEmpty());
		verify(repository, times(1)).findByElectionId(removeVoteModel.electionId);
		verify(repository, times(1)).save(proposal);
	}

	@Test
	void removeVoteInvalidModel() {
		removeVoteModel = new RemoveVoteModel(-1, removeVoteModel.memberId);
		assertThrows(ElectionDoesNotExist.class, () -> electionService.removeVote(removeVoteModel, LocalDateTime.now()));
		verify(repository, times(0)).findByElectionId(removeVoteModel.electionId);
	}

	@Test
	void removeVoteNotExisting() {
		when(repository.findByElectionId(removeVoteModel.electionId)).thenReturn(Optional.empty());
		assertThrows(ElectionDoesNotExist.class, () -> electionService.removeVote(removeVoteModel, LocalDateTime.now()));
		verify(repository, times(1)).findByElectionId(removeVoteModel.electionId);
	}

	@Test
	void removeVoteNotStarted() {
		Election proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
		validTM = new TimeModel(validTM.seconds, validTM.minutes, validTM.hours, 9, validTM.month, validTM.year);
		when(repository.findByElectionId(removeVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.removeVote(removeVoteModel, validTM.createDate()));
		verify(repository, times(1)).findByElectionId(removeVoteModel.electionId);
	}

	@Test
	void removeVoteEnded() {
		Election proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
		proposal.setStatus("finished");
		when(repository.findByElectionId(removeVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(CannotProceedVote.class, () -> electionService.removeVote(removeVoteModel, LocalDateTime.now()));
		verify(repository, times(1)).findByElectionId(removeVoteModel.electionId);
	}

	@Test
	void removeVoteNotVoted() throws ThereIsNoVote, ElectionDoesNotExist, CannotProceedVote {
		Proposal proposal = new Proposal(EL, TESTEX, 1, validTM.createDate());
		proposal.setStatus("ongoing");
		proposal.vote("notChad", true);
		when(repository.findByElectionId(removeVoteModel.electionId)).thenReturn(Optional.of(proposal));
		assertThrows(ThereIsNoVote.class, () -> electionService.removeVote(removeVoteModel, LocalDateTime.now()));
		assertFalse(proposal.getVotes().isEmpty());
		verify(repository, times(1)).findByElectionId(removeVoteModel.electionId);
		verify(repository, times(0)).save(proposal);
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