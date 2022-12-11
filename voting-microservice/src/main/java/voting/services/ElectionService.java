package voting.services;

import org.springframework.stereotype.Service;
import voting.domain.BoardElection;
import voting.domain.Election;
import voting.domain.Proposal;
import voting.db.repos.ElectionRepository;
import voting.exceptions.BoardElectionAlreadyCreated;
import voting.exceptions.ElectionCannotBeCreated;
import voting.exceptions.ElectionDoesNotExist;
import voting.exceptions.ProposalAlreadyCreated;
import voting.models.BoardElectionModel;
import voting.models.ProposalModel;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ElectionService {
    private final transient ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    /**
     * Creates a board election
     * @param model Model from which to create the election
     * @return New board election, if one does not exist with same hoaID
     * @throws BoardElectionAlreadyCreated If board election for the given HoaID already exists
     */
    public BoardElection createBoardElection(BoardElectionModel model)
            throws BoardElectionAlreadyCreated, ElectionCannotBeCreated {
        if (!model.isValid()) throw new ElectionCannotBeCreated("Some/all of the provided fields are invalid");
        LocalDateTime d = model.scheduledFor.createDate();
        BoardElection boardElection = new BoardElection(model.name, model.description, model.hoaId, d,
                model.amountOfWinners, model.candidates);
        if (electionRepository.getBoardElectionByHoaId(model.hoaId).isEmpty()) {
            electionRepository.save(boardElection);
            return boardElection;
        } else throw new BoardElectionAlreadyCreated("Board election with hoaId: "
                + model.hoaId
                + "already exists.");
    }

    /**
     * Creates a proposal
     * @param model Model from which to create the proposal
     * @return New proposal, if one does not exist with same hoaID and name
     * @throws ProposalAlreadyCreated If proposal for the given HoaID and name already exists
     */
    public Proposal createProposal(ProposalModel model) throws ProposalAlreadyCreated, ElectionCannotBeCreated {
        if (!model.isValid()) throw new ElectionCannotBeCreated("Some/all of the provided fields are invalid");
        LocalDateTime d = model.scheduledFor.createDate();
        Proposal proposal = new Proposal(model.name, model.description, model.hoaId, d);
        if (!electionRepository.existsByHoaIdAndName(model.hoaId, model.name)) {
            electionRepository.save(proposal);
            return proposal;
        } else throw new ProposalAlreadyCreated("Proposal with hoaId: "
                + model.hoaId
                + "and name: "
                + model.name + "already exists.");
    }

    /**
     * Method called when a member wants to vote
     * @param electionId Id of election to vote for
     * @param memberShipId Id of member that wants to vote
     * @param choice Choice of member (binary for proposal, id for candidates)
     * @throws ElectionDoesNotExist If election does not exist with provided id
     */
    public void vote(int electionId, int memberShipId, int choice) throws ElectionDoesNotExist {
        if (electionId <= 0 || memberShipId <= 0) throw new ElectionDoesNotExist("Ids not valid");
        Optional<Election> election = this.electionRepository.findByElectionId(electionId);
        if (election.isEmpty()) throw new ElectionDoesNotExist("Election not found");
        election.get().vote(memberShipId, choice);
        this.electionRepository.save(election.get());
    }

    /**
     * Gets an election with the given id
     * @param electionId Id of election to fetch
     * @return Fetched election, if it exists
     * @throws ElectionDoesNotExist If an election with given id does not exist
     */
    public Election getElection(int electionId) throws ElectionDoesNotExist {
        Optional<Election> e = this.electionRepository.findByElectionId(electionId);
        if (e.isEmpty()) throw new ElectionDoesNotExist("Election with provided id does not exist");
        return e.get();
    }

    /**
     * Concludes an election with the given id
     * @param electionId Id of election to conclude
     * @return Result of the election
     * @throws ElectionDoesNotExist If an election with provided id does not exist
     */
    public Object conclude(int electionId) throws ElectionDoesNotExist {
        Optional<Election> e = this.electionRepository.findByElectionId(electionId);
        if (e.isEmpty()) throw new ElectionDoesNotExist("Election with provided id does not exist");
        Object res = e.get().conclude();
        this.electionRepository.save(e.get());
        return res;
    }
}
