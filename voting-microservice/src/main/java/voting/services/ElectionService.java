package voting.services;

import org.springframework.stereotype.Service;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.domain.db.repos.ElectionRepository;
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

	public void vote(int electionId, int memberShipId, int choice) {
		Election election = this.electionRepository.findByElectionId(electionId).
			orElseThrow(() -> new IllegalArgumentException("Election not found"));
		election.vote(memberShipId, choice);
		this.electionRepository.save(election);
	}

	public Election getElection(int electionId) {
		return this.electionRepository.findByElectionId(electionId).get();
	}

	public void conclude(int electionId) {
		Election election = this.electionRepository.findByElectionId(electionId).
			orElseThrow(() -> new IllegalArgumentException("Election not found"));
		election.conclude();
		this.electionRepository.save(election);
	}
}
