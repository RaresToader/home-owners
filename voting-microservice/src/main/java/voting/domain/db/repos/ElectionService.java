package voting.domain.db.repos;

import org.springframework.stereotype.Service;
import voting.domain.BoardElection;
import voting.domain.Proposal;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;

@Service
public class ElectionService {
	private final transient ElectionRepository electionRepository;

	public ElectionService(ElectionRepository electionRepository) {
	  	this.electionRepository = electionRepository;
	}

	public BoardElection createBoardElection(BoardElectionModel model) {
		BoardElection boardElection = new BoardElection(model.getName(), model.getDescription(), model.getHoaId(),
			model.getScheduledFor(), model.getAmountOfWinners(), model.getCandidates());

		if (!electionRepository.existsByElectionId(boardElection.getElectionId())) {
			electionRepository.save(boardElection);
			return boardElection;
		}

		return null;
	}

	public Proposal createProposal(ProposalModel model) {
		Proposal proposal = new Proposal(model.getName(), model.getDescription(), model.getHoaId(), model.getScheduledFor());

		if (!electionRepository.existsByElectionId(proposal.getElectionId())) {
			electionRepository.save(proposal);
			return proposal;
		}

		return null;
	}

}
