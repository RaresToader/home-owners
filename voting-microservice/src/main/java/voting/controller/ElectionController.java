package voting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import voting.domain.BoardElection;
import voting.domain.Proposal;
import voting.domain.db.repos.ElectionService;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;

@RestController
@RequestMapping("/voting")
public class ElectionController {

	private final transient ElectionService electionService;

	@Autowired
	public ElectionController(ElectionService electionService) {
		this.electionService = electionService;
	}

	@PostMapping("/proposal")
	public ResponseEntity<Proposal> createProposal(@RequestBody ProposalModel model) {
		try {
			Proposal proposal = electionService.createProposal(model);
			return ResponseEntity.ok(proposal);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create proposal", e);
		}
	}

	@PostMapping("/boardElection")
	public ResponseEntity<BoardElection> createBoardElection(@RequestBody BoardElectionModel model) {
		try {
			BoardElection boardElection = electionService.createBoardElection(model);
			return ResponseEntity.ok(boardElection);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create board election", e);
		}
	}
}
